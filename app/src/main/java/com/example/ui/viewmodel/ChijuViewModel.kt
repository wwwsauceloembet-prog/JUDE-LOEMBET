package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.PedagogicalAiEngine
import com.example.data.local.ChijuDatabase
import com.example.data.model.Assessment
import com.example.data.model.AttendanceRecord
import com.example.data.model.HomeworkAssignment
import com.example.data.model.LessonPlan
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.StudentGrade
import com.example.data.model.SubscriptionInfo
import com.example.data.repository.AssessmentStats
import com.example.data.repository.ChijuRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ChijuScreen(val label: String) {
    HOME("Accueil"),
    MY_CLASS("Ma classe"),
    LESSON_PLANNER("Cahier Journal"),
    AI_ASSISTANT("Assistant IA"),
    GRADES("Résultats"),
    SUBSCRIPTION("Abonnement"),
    REPORTS("Rapports"),
    SETTINGS("Paramètres")
}

@OptIn(ExperimentalCoroutinesApi::class)
class ChijuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChijuRepository
    init {
        val db = ChijuDatabase.getDatabase(application, viewModelScope)
        repository = ChijuRepository(db.chijuDao(), db.studentDao())
    }

    // Navigation & Global UI State
    private val _currentScreen = MutableStateFlow(ChijuScreen.HOME)
    val currentScreen: StateFlow<ChijuScreen> = _currentScreen.asStateFlow()

    private val _todayDate = MutableStateFlow("2026-11-02")
    val todayDate: StateFlow<String> = _todayDate.asStateFlow()

    private val _activeSession = MutableStateFlow("Matin")
    val activeSession: StateFlow<String> = _activeSession.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    fun navigateTo(screen: ChijuScreen) {
        _currentScreen.value = screen
    }

    // Classes
    val classes: StateFlow<List<SchoolClass>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedClassId = MutableStateFlow<Long?>(null)
    val selectedClassId: StateFlow<Long?> = _selectedClassId.asStateFlow()

    // Automatically pick first class if none selected
    val activeClass: StateFlow<SchoolClass?> = combine(classes, _selectedClassId) { list, id ->
        if (id != null) list.find { it.id == id } else list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectClass(cls: SchoolClass) {
        _selectedClassId.value = cls.id
    }

    // Students for currently active class
    val students: StateFlow<List<Student>> = activeClass.flatMapLatest { cls ->
        if (cls != null) repository.getStudentsForClass(cls.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Attendance for today & active session
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = combine(activeClass, _todayDate, _activeSession) { cls, date, session ->
        Triple(cls, date, session)
    }.flatMapLatest { (cls, date, session) ->
        if (cls != null) repository.getAttendanceForSession(cls.id, date, session) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lessons (Cahier Journal) for active class
    val lessons: StateFlow<List<LessonPlan>> = activeClass.flatMapLatest { cls ->
        if (cls != null) repository.getLessonsForClass(cls.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Assessments for active class
    val assessments: StateFlow<List<Assessment>> = activeClass.flatMapLatest { cls ->
        if (cls != null) repository.getAssessmentsForClass(cls.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Homework for active class
    val homework: StateFlow<List<HomeworkAssignment>> = activeClass.flatMapLatest { cls ->
        if (cls != null) repository.getHomeworkForClass(cls.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Subscription
    val subscription: StateFlow<SubscriptionInfo?> = repository.subscriptionInfo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // AI Generation UI State
    private val _aiResult = MutableStateFlow<PedagogicalAiEngine.AiGenerationResult?>(null)
    val aiResult: StateFlow<PedagogicalAiEngine.AiGenerationResult?> = _aiResult.asStateFlow()

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    // ---------------- Actions ---------------- //

    fun createClass(name: String, level: String, room: String, schoolName: String) {
        viewModelScope.launch {
            val id = repository.addClass(
                SchoolClass(
                    name = name,
                    gradeLevel = level,
                    roomNumber = room,
                    schoolName = schoolName
                )
            )
            _selectedClassId.value = id
            _userMessage.value = "Classe '$name' créée avec succès"
        }
    }

    fun deleteClass(cls: SchoolClass) {
        viewModelScope.launch {
            repository.deleteClass(cls)
            _userMessage.value = "Classe '${cls.name}' supprimée"
        }
    }

    fun addStudent(firstName: String, lastName: String, gender: String, birthDate: String, contact: String, niveau: String? = null) {
        val currentClass = activeClass.value ?: return
        val targetNiveau = niveau ?: currentClass.gradeLevel
        viewModelScope.launch {
            val matricule = "$targetNiveau-${(100..999).random()}"
            repository.addStudent(
                Student(
                    classId = currentClass.id,
                    matricule = matricule,
                    firstName = firstName,
                    lastName = lastName,
                    niveau = targetNiveau,
                    gender = gender,
                    birthDate = birthDate,
                    parentContact = contact
                )
            )
            _userMessage.value = "Élève $lastName $firstName ($targetNiveau) ajouté"
        }
    }

    fun getStudentsByNiveau(niveau: String): Flow<List<Student>> {
        return repository.getStudentsByNiveau(niveau)
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            _userMessage.value = "Élève retiré de la classe"
        }
    }

    fun markAllPresent() {
        val currentClass = activeClass.value ?: return
        val currentStudents = students.value
        val date = _todayDate.value
        val session = _activeSession.value
        viewModelScope.launch {
            repository.markAllStudentsPresent(
                classId = currentClass.id,
                studentIds = currentStudents.map { it.id },
                date = date,
                session = session
            )
            _userMessage.value = "Appel : Tous les élèves (${currentStudents.size}) marqués présents"
        }
    }

    fun setStudentAttendance(studentId: Long, status: String) {
        val currentClass = activeClass.value ?: return
        val date = _todayDate.value
        val session = _activeSession.value
        viewModelScope.launch {
            repository.recordAttendance(
                AttendanceRecord(
                    classId = currentClass.id,
                    studentId = studentId,
                    date = date,
                    session = session,
                    status = status
                )
            )
        }
    }

    fun saveLessonPlan(lesson: LessonPlan) {
        viewModelScope.launch {
            repository.saveLesson(lesson)
            _userMessage.value = "Séance enregistrée dans le Cahier Journal"
        }
    }

    fun updateLessonStatus(lesson: LessonPlan, newStatus: String, actualSummary: String) {
        viewModelScope.launch {
            val updated = lesson.copy(status = newStatus, actualSummary = actualSummary)
            repository.updateLesson(updated)
            _userMessage.value = "Séance mise à jour : $newStatus"
        }
    }

    fun deleteLesson(lesson: LessonPlan) {
        viewModelScope.launch {
            repository.deleteLesson(lesson)
            _userMessage.value = "Séance supprimée du Cahier Journal"
        }
    }

    fun addAssessment(
        subject: String,
        title: String,
        date: String,
        maxScore: Double,
        coeff: Int,
        period: String
    ) {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            repository.addAssessment(
                Assessment(
                    classId = cls.id,
                    subject = subject,
                    title = title,
                    date = date,
                    maxScore = maxScore,
                    coefficient = coeff,
                    period = period
                )
            )
            _userMessage.value = "Évaluation '$title' créée"
        }
    }

    fun deleteAssessment(assessment: Assessment) {
        viewModelScope.launch {
            repository.deleteAssessment(assessment)
            _userMessage.value = "Évaluation supprimée"
        }
    }

    fun getGradesForAssessment(assessmentId: Long): Flow<List<StudentGrade>> {
        return repository.getGradesForAssessment(assessmentId)
    }

    fun saveStudentGrade(assessmentId: Long, studentId: Long, score: Double?, isAbsent: Boolean, comment: String) {
        viewModelScope.launch {
            repository.saveGrade(
                StudentGrade(
                    assessmentId = assessmentId,
                    studentId = studentId,
                    score = score,
                    isAbsent = isAbsent,
                    remarks = comment
                )
            )
        }
    }

    fun addHomework(subject: String, title: String, instructions: String, dueDate: String) {
        val cls = activeClass.value ?: return
        viewModelScope.launch {
            repository.addHomework(
                HomeworkAssignment(
                    classId = cls.id,
                    subject = subject,
                    title = title,
                    instructions = instructions,
                    assignedDate = _todayDate.value,
                    dueDate = dueDate
                )
            )
            _userMessage.value = "Devoir enregistré"
        }
    }

    fun toggleHomework(hw: HomeworkAssignment) {
        viewModelScope.launch {
            repository.updateHomework(hw.copy(isDone = !hw.isDone))
        }
    }

    fun deleteHomework(hw: HomeworkAssignment) {
        viewModelScope.launch {
            repository.deleteHomework(hw)
            _userMessage.value = "Devoir supprimé"
        }
    }

    // AI Generation
    fun generateAi(
        type: PedagogicalAiEngine.AssistantType,
        country: String,
        level: String,
        subject: String,
        topic: String
    ) {
        _isGeneratingAi.value = true
        viewModelScope.launch {
            val clsId = activeClass.value?.id ?: 1L
            val result = PedagogicalAiEngine.generateContent(
                type = type,
                country = country,
                level = level,
                subject = subject,
                topic = topic,
                classId = clsId,
                todayDate = _todayDate.value
            )
            _aiResult.value = result
            _isGeneratingAi.value = false
            _userMessage.value = "Proposition pédagogique générée !"
        }
    }

    fun clearAiResult() {
        _aiResult.value = null
    }

    fun importAiLessonToJournal() {
        val result = _aiResult.value ?: return
        val plan = result.lessonPlanTemplate ?: return
        viewModelScope.launch {
            repository.saveLesson(plan)
            _userMessage.value = "Leçon ajoutée avec succès au Cahier Journal !"
            _aiResult.value = null
            _currentScreen.value = ChijuScreen.LESSON_PLANNER
        }
    }

    // Subscription & Airtel Money
    fun activateSubscriptionWithAirtel(phone: String, transactionRef: String, teacherName: String, schoolName: String) {
        viewModelScope.launch {
            repository.activateSubscriptionWithAirtel(
                phone = phone,
                transactionRef = transactionRef,
                schoolName = schoolName,
                teacherName = teacherName
            )
            _userMessage.value = "Abonnement CHIJU activé avec succès pour 12 mois !"
        }
    }

    fun computeStats(grades: List<StudentGrade>, maxScore: Double): AssessmentStats {
        return repository.computeStats(grades, maxScore)
    }
}
