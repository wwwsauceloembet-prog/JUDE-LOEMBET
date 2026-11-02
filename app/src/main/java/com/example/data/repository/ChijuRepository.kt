package com.example.data.repository

import com.example.data.local.ChijuDao
import com.example.data.model.Assessment
import com.example.data.model.AttendanceRecord
import com.example.data.model.HomeworkAssignment
import com.example.data.model.LessonPlan
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.StudentGrade
import com.example.data.model.SubscriptionInfo
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.data.local.StudentDao

class ChijuRepository(
    private val dao: ChijuDao,
    private val studentDao: StudentDao? = null
) {

    // Classes
    val allClasses: Flow<List<SchoolClass>> = dao.getAllClasses()

    suspend fun addClass(schoolClass: SchoolClass): Long = dao.insertClass(schoolClass)
    suspend fun updateClass(schoolClass: SchoolClass) = dao.updateClass(schoolClass)
    suspend fun deleteClass(schoolClass: SchoolClass) = dao.deleteClass(schoolClass)

    // Students
    fun getStudentsForClass(classId: Long): Flow<List<Student>> =
        studentDao?.getStudentsForClass(classId) ?: dao.getStudentsForClass(classId)

    fun getAllStudents(): Flow<List<Student>> =
        studentDao?.getAllStudents() ?: dao.getAllStudents()

    fun getStudentsByNiveau(niveau: String): Flow<List<Student>> =
        studentDao?.getStudentsByNiveau(niveau) ?: dao.getAllStudents()

    fun getStudentById(id: Long): Flow<Student?> =
        studentDao?.getStudentById(id) ?: kotlinx.coroutines.flow.flowOf(null)

    suspend fun addStudent(student: Student): Long =
        studentDao?.insertStudent(student) ?: dao.insertStudent(student)

    suspend fun addStudents(students: List<Student>) {
        if (studentDao != null) {
            studentDao.insertStudents(students)
        } else {
            dao.insertStudents(students)
        }
    }

    suspend fun updateStudent(student: Student) {
        if (studentDao != null) {
            studentDao.updateStudent(student)
        } else {
            dao.updateStudent(student)
        }
    }

    suspend fun deleteStudent(student: Student) {
        if (studentDao != null) {
            studentDao.deleteStudent(student)
        } else {
            dao.deleteStudent(student)
        }
    }

    // Attendance
    fun getAttendanceForDate(classId: Long, date: String): Flow<List<AttendanceRecord>> =
        dao.getAttendanceForDate(classId, date)

    fun getAttendanceForSession(classId: Long, date: String, session: String): Flow<List<AttendanceRecord>> =
        dao.getAttendanceForSession(classId, date, session)

    fun getAllAttendanceForClass(classId: Long): Flow<List<AttendanceRecord>> =
        dao.getAllAttendanceForClass(classId)

    suspend fun recordAttendance(record: AttendanceRecord) =
        dao.insertOrUpdateAttendance(record)

    suspend fun markAllStudentsPresent(classId: Long, studentIds: List<Long>, date: String, session: String) {
        val records = studentIds.map { sId ->
            AttendanceRecord(
                classId = classId,
                studentId = sId,
                date = date,
                session = session,
                status = "PRESENT",
                notes = "Marqué présent groupé"
            )
        }
        dao.insertAttendanceRecords(records)
    }

    // Lessons (Cahier Journal)
    fun getLessonsForClass(classId: Long): Flow<List<LessonPlan>> = dao.getLessonsForClass(classId)
    fun getLessonsForDate(classId: Long, date: String): Flow<List<LessonPlan>> =
        dao.getLessonsForDate(classId, date)

    suspend fun saveLesson(lesson: LessonPlan): Long = dao.insertLesson(lesson)
    suspend fun updateLesson(lesson: LessonPlan) = dao.updateLesson(lesson)
    suspend fun deleteLesson(lesson: LessonPlan) = dao.deleteLesson(lesson)

    // Assessments & Grades
    fun getAssessmentsForClass(classId: Long): Flow<List<Assessment>> = dao.getAssessmentsForClass(classId)
    fun getGradesForAssessment(assessmentId: Long): Flow<List<StudentGrade>> =
        dao.getGradesForAssessment(assessmentId)

    suspend fun addAssessment(assessment: Assessment): Long = dao.insertAssessment(assessment)
    suspend fun updateAssessment(assessment: Assessment) = dao.updateAssessment(assessment)
    suspend fun deleteAssessment(assessment: Assessment) = dao.deleteAssessment(assessment)

    suspend fun saveGrade(grade: StudentGrade) = dao.insertOrUpdateGrade(grade)
    suspend fun saveGrades(grades: List<StudentGrade>) = dao.insertGrades(grades)

    // Homework
    fun getHomeworkForClass(classId: Long): Flow<List<HomeworkAssignment>> = dao.getHomeworkForClass(classId)
    suspend fun addHomework(hw: HomeworkAssignment): Long = dao.insertHomework(hw)
    suspend fun updateHomework(hw: HomeworkAssignment) = dao.updateHomework(hw)
    suspend fun deleteHomework(hw: HomeworkAssignment) = dao.deleteHomework(hw)

    // Subscription & Airtel Money
    val subscriptionInfo: Flow<SubscriptionInfo?> = dao.getSubscriptionInfo()

    suspend fun activateSubscriptionWithAirtel(
        phone: String,
        transactionRef: String,
        schoolName: String,
        teacherName: String
    ) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        // 365 days later
        val expireTime = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
        val expirationStr = sdf.format(Date(expireTime))

        dao.insertOrUpdateSubscription(
            SubscriptionInfo(
                id = 1,
                status = "ACTIF",
                planName = "CHIJU Enseignant Pro (Annuel)",
                priceText = "10 000 FCFA / an",
                officialAirtelNumber = "+241 77793074",
                activatedDate = today,
                expirationDate = expirationStr,
                daysRemaining = 365,
                lastTransactionRef = transactionRef,
                payerPhone = phone,
                teacherName = teacherName,
                schoolName = schoolName
            )
        )
    }

    // Helper: Compute average and stats
    fun computeStats(grades: List<StudentGrade>, maxScore: Double): AssessmentStats {
        val validGrades = grades.filter { it.score != null && !it.isAbsent }.map { it.score!! }
        if (validGrades.isEmpty()) {
            return AssessmentStats(0.0, 0.0, 0.0, 0, 0, 0.0)
        }
        val avg = validGrades.average()
        val min = validGrades.minOrNull() ?: 0.0
        val max = validGrades.maxOrNull() ?: 0.0
        val passingScore = maxScore / 2.0
        val passed = validGrades.count { it >= passingScore }
        val successRate = (passed.toDouble() / validGrades.size) * 100.0
        return AssessmentStats(
            average = avg,
            min = min,
            max = max,
            totalStudents = grades.size,
            passedStudents = passed,
            successRate = successRate
        )
    }
}

data class AssessmentStats(
    val average: Double,
    val min: Double,
    val max: Double,
    val totalStudents: Int,
    val passedStudents: Int,
    val successRate: Double
)
