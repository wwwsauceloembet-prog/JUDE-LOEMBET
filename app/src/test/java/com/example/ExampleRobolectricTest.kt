package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.PedagogicalAiEngine
import com.example.data.model.StudentGrade
import com.example.data.repository.ChijuRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CHIJU", appName)
  }

  @Test
  fun `pedagogical AI engine generates complete structured content`() {
    val result = PedagogicalAiEngine.generateContent(
      type = PedagogicalAiEngine.AssistantType.LESSON_PREP,
      country = "Gabon",
      level = "CM1",
      subject = "Mathématiques",
      topic = "Les fractions simples"
    )
    assertTrue(result.title.contains("Les fractions simples"))
    assertTrue(result.content.contains("OBJECTIFS D'APPRENTISSAGE"))
    assertTrue(result.content.contains("DÉROULEMENT CHRONOLOGIQUE"))
    assertTrue(result.content.contains("ÉVALUATION FORMATIVE"))
  }

  @Test
  fun `deterministic class average calculation test`() {
    val dummyDao = object : com.example.data.local.ChijuDao {
      override fun getAllClasses() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.SchoolClass>())
      override fun getClassById(id: Long) = kotlinx.coroutines.flow.flowOf(null)
      override suspend fun insertClass(schoolClass: com.example.data.model.SchoolClass) = 1L
      override suspend fun updateClass(schoolClass: com.example.data.model.SchoolClass) {}
      override suspend fun deleteClass(schoolClass: com.example.data.model.SchoolClass) {}
      override fun getStudentsForClass(classId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.Student>())
      override fun getAllStudents() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.Student>())
      override suspend fun insertStudent(student: com.example.data.model.Student) = 1L
      override suspend fun insertStudents(students: List<com.example.data.model.Student>) {}
      override suspend fun updateStudent(student: com.example.data.model.Student) {}
      override suspend fun deleteStudent(student: com.example.data.model.Student) {}
      override fun getAttendanceForSession(classId: Long, date: String, session: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.AttendanceRecord>())
      override fun getAttendanceForDate(classId: Long, date: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.AttendanceRecord>())
      override fun getAllAttendanceForClass(classId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.AttendanceRecord>())
      override suspend fun insertOrUpdateAttendance(record: com.example.data.model.AttendanceRecord) {}
      override suspend fun insertAttendanceRecords(records: List<com.example.data.model.AttendanceRecord>) {}
      override fun getLessonsForClass(classId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.LessonPlan>())
      override fun getLessonsForDate(classId: Long, date: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.LessonPlan>())
      override fun getAllLessons() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.LessonPlan>())
      override suspend fun insertLesson(lessonPlan: com.example.data.model.LessonPlan) = 1L
      override suspend fun updateLesson(lessonPlan: com.example.data.model.LessonPlan) {}
      override suspend fun deleteLesson(lessonPlan: com.example.data.model.LessonPlan) {}
      override fun getAssessmentsForClass(classId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.Assessment>())
      override suspend fun insertAssessment(assessment: com.example.data.model.Assessment) = 1L
      override suspend fun updateAssessment(assessment: com.example.data.model.Assessment) {}
      override suspend fun deleteAssessment(assessment: com.example.data.model.Assessment) {}
      override fun getGradesForAssessment(assessmentId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.StudentGrade>())
      override fun getAllGrades() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.StudentGrade>())
      override suspend fun insertOrUpdateGrade(grade: com.example.data.model.StudentGrade) {}
      override suspend fun insertGrades(grades: List<com.example.data.model.StudentGrade>) {}
      override fun getHomeworkForClass(classId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.model.HomeworkAssignment>())
      override suspend fun insertHomework(homework: com.example.data.model.HomeworkAssignment) = 1L
      override suspend fun updateHomework(homework: com.example.data.model.HomeworkAssignment) {}
      override suspend fun deleteHomework(homework: com.example.data.model.HomeworkAssignment) {}
      override fun getSubscriptionInfo() = kotlinx.coroutines.flow.flowOf(null)
      override suspend fun insertOrUpdateSubscription(subscriptionInfo: com.example.data.model.SubscriptionInfo) {}
    }

    val repository = ChijuRepository(dummyDao)
    val testGrades = listOf(
      StudentGrade(assessmentId = 1, studentId = 1, score = 15.0),
      StudentGrade(assessmentId = 1, studentId = 2, score = 10.0),
      StudentGrade(assessmentId = 1, studentId = 3, score = 5.0),
      StudentGrade(assessmentId = 1, studentId = 4, score = null, isAbsent = true)
    )
    val stats = repository.computeStats(testGrades, 20.0)
    assertEquals(10.0, stats.average, 0.01)
    assertEquals(5.0, stats.min, 0.01)
    assertEquals(15.0, stats.max, 0.01)
    assertEquals(2, stats.passedStudents)
    assertEquals(66.66, stats.successRate, 0.1)
  }

  @Test
  fun `student entity holds nom, prenom and niveau properly`() {
    val student = com.example.data.model.Student(
      matricule = "CM1-007",
      lastName = "DIALLO",
      firstName = "Aminata",
      niveau = "CM1",
      gender = "F"
    )
    assertEquals("DIALLO", student.nom)
    assertEquals("Aminata", student.prenom)
    assertEquals("CM1", student.niveau)
    assertEquals("DIALLO Aminata", student.fullName)
  }
}

