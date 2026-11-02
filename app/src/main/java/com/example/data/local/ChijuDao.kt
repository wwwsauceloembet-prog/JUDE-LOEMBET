package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Assessment
import com.example.data.model.AttendanceRecord
import com.example.data.model.HomeworkAssignment
import com.example.data.model.LessonPlan
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.StudentGrade
import com.example.data.model.SubscriptionInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ChijuDao {
    // Classes
    @Query("SELECT * FROM school_classes ORDER BY name ASC")
    fun getAllClasses(): Flow<List<SchoolClass>>

    @Query("SELECT * FROM school_classes WHERE id = :id LIMIT 1")
    fun getClassById(id: Long): Flow<SchoolClass?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(schoolClass: SchoolClass): Long

    @Update
    suspend fun updateClass(schoolClass: SchoolClass)

    @Delete
    suspend fun deleteClass(schoolClass: SchoolClass)

    // Students
    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY lastName ASC, firstName ASC")
    fun getStudentsForClass(classId: Long): Flow<List<Student>>

    @Query("SELECT * FROM students ORDER BY lastName ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    // Attendance
    @Query("SELECT * FROM attendance_records WHERE classId = :classId AND date = :date AND session = :session")
    fun getAttendanceForSession(classId: Long, date: String, session: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE classId = :classId AND date = :date")
    fun getAttendanceForDate(classId: Long, date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE classId = :classId")
    fun getAllAttendanceForClass(classId: Long): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(record: AttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecord>)

    // Lesson Plans (Cahier Journal)
    @Query("SELECT * FROM lesson_plans WHERE classId = :classId ORDER BY dateScheduled DESC, timeSlot ASC")
    fun getLessonsForClass(classId: Long): Flow<List<LessonPlan>>

    @Query("SELECT * FROM lesson_plans WHERE classId = :classId AND dateScheduled = :date ORDER BY timeSlot ASC")
    fun getLessonsForDate(classId: Long, date: String): Flow<List<LessonPlan>>

    @Query("SELECT * FROM lesson_plans ORDER BY dateScheduled DESC")
    fun getAllLessons(): Flow<List<LessonPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lessonPlan: LessonPlan): Long

    @Update
    suspend fun updateLesson(lessonPlan: LessonPlan)

    @Delete
    suspend fun deleteLesson(lessonPlan: LessonPlan)

    // Assessments & Grades
    @Query("SELECT * FROM assessments WHERE classId = :classId ORDER BY date DESC")
    fun getAssessmentsForClass(classId: Long): Flow<List<Assessment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: Assessment): Long

    @Update
    suspend fun updateAssessment(assessment: Assessment)

    @Delete
    suspend fun deleteAssessment(assessment: Assessment)

    @Query("SELECT * FROM student_grades WHERE assessmentId = :assessmentId")
    fun getGradesForAssessment(assessmentId: Long): Flow<List<StudentGrade>>

    @Query("SELECT * FROM student_grades")
    fun getAllGrades(): Flow<List<StudentGrade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGrade(grade: StudentGrade)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrades(grades: List<StudentGrade>)

    // Homework
    @Query("SELECT * FROM homework_assignments WHERE classId = :classId ORDER BY dueDate ASC")
    fun getHomeworkForClass(classId: Long): Flow<List<HomeworkAssignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: HomeworkAssignment): Long

    @Update
    suspend fun updateHomework(homework: HomeworkAssignment)

    @Delete
    suspend fun deleteHomework(homework: HomeworkAssignment)

    // Subscription
    @Query("SELECT * FROM subscription_info WHERE id = 1 LIMIT 1")
    fun getSubscriptionInfo(): Flow<SubscriptionInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSubscription(subscriptionInfo: SubscriptionInfo)
}
