package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "school_classes")
data class SchoolClass(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val gradeLevel: String, // CP1, CP2, CE1, CE2, CM1, CM2
    val schoolYear: String = "2026-2027",
    val roomNumber: String = "Salle 01",
    val schoolName: String = "École Publique Principale",
    val studentCount: Int = 0
)

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = SchoolClass::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("classId")]
)
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long = 1L,
    val matricule: String = "",
    val firstName: String, // prénom de l'élève
    val lastName: String,   // nom de famille
    val niveau: String = "CM1", // CP1, CP2, CE1, CE2, CM1, CM2
    val gender: String = "M", // M / F
    val birthDate: String = "",
    val parentContact: String = "",
    val status: String = "Actif"
) {
    val nom: String get() = lastName
    val prenom: String get() = firstName
    val fullName: String get() = "$lastName $firstName"
}

@Entity(
    tableName = "attendance_records",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("studentId"), Index(value = ["classId", "studentId", "date", "session"], unique = true)]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val studentId: Long,
    val date: String, // YYYY-MM-DD
    val session: String = "Matin", // Matin / Après-midi
    val status: String = "PRESENT", // PRESENT, ABSENT, RETARD, JUSTIFIE
    val notes: String = ""
)

@Entity(
    tableName = "lesson_plans",
    foreignKeys = [
        ForeignKey(
            entity = SchoolClass::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("classId")]
)
data class LessonPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val subject: String,
    val gradeLevel: String,
    val title: String,
    val dateScheduled: String,
    val timeSlot: String = "08h00 - 08h45",
    val durationMinutes: Int = 45,
    val objectives: String = "",
    val prerequisites: String = "",
    val materials: String = "",
    val phaseDiscovery: String = "",
    val phaseActivity: String = "",
    val phasePractice: String = "",
    val phaseSynthesis: String = "",
    val evaluation: String = "",
    val remediation: String = "",
    val actualSummary: String = "",
    val status: String = "PREVU", // PREVU, REALISE, REPORTE
    val isAiGenerated: Boolean = false
)

@Entity(
    tableName = "assessments",
    foreignKeys = [
        ForeignKey(
            entity = SchoolClass::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("classId")]
)
data class Assessment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val subject: String,
    val title: String,
    val date: String,
    val maxScore: Double = 20.0,
    val coefficient: Int = 1,
    val period: String = "Trimestre 1"
)

@Entity(
    tableName = "student_grades",
    foreignKeys = [
        ForeignKey(
            entity = Assessment::class,
            parentColumns = ["id"],
            childColumns = ["assessmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("assessmentId"), Index(value = ["assessmentId", "studentId"], unique = true)]
)
data class StudentGrade(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assessmentId: Long,
    val studentId: Long,
    val score: Double? = null,
    val isAbsent: Boolean = false,
    val remarks: String = ""
)

@Entity(
    tableName = "homework_assignments",
    foreignKeys = [
        ForeignKey(
            entity = SchoolClass::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("classId")]
)
data class HomeworkAssignment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val subject: String,
    val title: String,
    val instructions: String,
    val assignedDate: String,
    val dueDate: String,
    val isDone: Boolean = false
)

@Entity(tableName = "subscription_info")
data class SubscriptionInfo(
    @PrimaryKey val id: Long = 1,
    val status: String = "ACTIF", // ACTIF, EXPIRE, EXPIRATION_PROCHE, ESSAI
    val planName: String = "CHIJU Enseignant Pro",
    val priceText: String = "10 000 FCFA / an",
    val officialAirtelNumber: String = "+241 77793074",
    val activatedDate: String = "2026-09-15",
    val expirationDate: String = "2027-09-15",
    val daysRemaining: Int = 318,
    val lastTransactionRef: String = "AIRTEL-TXN-984210",
    val payerPhone: String = "+241 77 12 34 56",
    val teacherName: String = "Maître Kaboré",
    val schoolName: String = "Groupe Scolaire Excellence"
)
