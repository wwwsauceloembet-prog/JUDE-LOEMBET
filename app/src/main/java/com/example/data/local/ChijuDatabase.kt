package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Assessment
import com.example.data.model.AttendanceRecord
import com.example.data.model.HomeworkAssignment
import com.example.data.model.LessonPlan
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.StudentGrade
import com.example.data.model.SubscriptionInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SchoolClass::class,
        Student::class,
        AttendanceRecord::class,
        LessonPlan::class,
        Assessment::class,
        StudentGrade::class,
        HomeworkAssignment::class,
        SubscriptionInfo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChijuDatabase : RoomDatabase() {
    abstract fun chijuDao(): ChijuDao
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: ChijuDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ChijuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChijuDatabase::class.java,
                    "chiju_pedagogique.db"
                )
                    .addCallback(ChijuDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ChijuDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.chijuDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: ChijuDao) {
            // Seed Subscription Info
            dao.insertOrUpdateSubscription(
                SubscriptionInfo(
                    id = 1,
                    status = "ACTIF",
                    planName = "CHIJU Pro Enseignant",
                    priceText = "10 000 FCFA / an",
                    officialAirtelNumber = "+241 77793074",
                    activatedDate = "2026-09-10",
                    expirationDate = "2027-09-10",
                    daysRemaining = 312,
                    lastTransactionRef = "AIRTEL-TXN-77894",
                    payerPhone = "+241 77 12 34 56",
                    teacherName = "Mme Aminata Bamba",
                    schoolName = "Groupe Scolaire Public d'Application"
                )
            )

            // Seed Class 1
            val class1Id = dao.insertClass(
                SchoolClass(
                    name = "CM1 Excellence",
                    gradeLevel = "CM1",
                    schoolYear = "2026-2027",
                    roomNumber = "Salle 04",
                    schoolName = "Groupe Scolaire Public d'Application",
                    studentCount = 12
                )
            )

            // Seed Class 2
            val class2Id = dao.insertClass(
                SchoolClass(
                    name = "CE2 Réussite",
                    gradeLevel = "CE2",
                    schoolYear = "2026-2027",
                    roomNumber = "Salle 02",
                    schoolName = "Groupe Scolaire Public d'Application",
                    studentCount = 6
                )
            )

            // Seed Students for CM1 Excellence
            val studentsCm1 = listOf(
                Student(classId = class1Id, matricule = "CM1-001", lastName = "NGUEMA", firstName = "Marc-Aurèle", gender = "M", birthDate = "14/05/2016", parentContact = "+241 74 11 22 33"),
                Student(classId = class1Id, matricule = "CM1-002", lastName = "BAMBA", firstName = "Fatoumata", gender = "F", birthDate = "21/08/2016", parentContact = "+241 77 22 33 44"),
                Student(classId = class1Id, matricule = "CM1-003", lastName = "KOFFI", firstName = "Jean-Yves", gender = "M", birthDate = "03/01/2016", parentContact = "+225 07 45 88 12"),
                Student(classId = class1Id, matricule = "CM1-004", lastName = "DIALLO", firstName = "Aminata", gender = "F", birthDate = "19/11/2016", parentContact = "+221 77 55 44 33"),
                Student(classId = class1Id, matricule = "CM1-005", lastName = "TRAORÉ", firstName = "Moussa", gender = "M", birthDate = "08/04/2016", parentContact = "+223 66 12 34 56"),
                Student(classId = class1Id, matricule = "CM1-006", lastName = "MOUKAMBI", firstName = "Chimène", gender = "F", birthDate = "29/09/2016", parentContact = "+241 66 33 88 99"),
                Student(classId = class1Id, matricule = "CM1-007", lastName = "ONDO", firstName = "Grace Estelle", gender = "F", birthDate = "05/02/2016", parentContact = "+241 77 88 99 00"),
                Student(classId = class1Id, matricule = "CM1-008", lastName = "KEITA", firstName = "Sekou", gender = "M", birthDate = "17/07/2016", parentContact = "+224 62 11 00 22"),
                Student(classId = class1Id, matricule = "CM1-009", lastName = "MENSAH", firstName = "David", gender = "M", birthDate = "12/10/2016", parentContact = "+228 90 44 55 66"),
                Student(classId = class1Id, matricule = "CM1-010", lastName = "SOW", firstName = "Aïssatou", gender = "F", birthDate = "30/03/2016", parentContact = "+221 78 99 88 77"),
                Student(classId = class1Id, matricule = "CM1-011", lastName = "KAMGA", firstName = "Paul", gender = "M", birthDate = "22/06/2016", parentContact = "+237 67 00 11 22"),
                Student(classId = class1Id, matricule = "CM1-012", lastName = "TOURE", firstName = "Mariam", gender = "F", birthDate = "15/12/2016", parentContact = "+225 05 99 77 55")
            )
            dao.insertStudents(studentsCm1)

            // Seed Students for CE2 Réussite
            val studentsCe2 = listOf(
                Student(classId = class2Id, matricule = "CE2-001", lastName = "BOKO", firstName = "Christian", niveau = "CE2", gender = "M", birthDate = "10/02/2017", parentContact = "+229 97 12 34 56"),
                Student(classId = class2Id, matricule = "CE2-002", lastName = "NZAMBA", firstName = "Sylvie", niveau = "CE2", gender = "F", birthDate = "04/09/2017", parentContact = "+241 74 55 66 77"),
                Student(classId = class2Id, matricule = "CE2-003", lastName = "SYLLA", firstName = "Ousmane", niveau = "CE2", gender = "M", birthDate = "25/11/2017", parentContact = "+224 65 33 22 11"),
                Student(classId = class2Id, matricule = "CE2-004", lastName = "KOUMBA", firstName = "Priscille", niveau = "CE2", gender = "F", birthDate = "18/06/2017", parentContact = "+241 77 44 11 00"),
                Student(classId = class2Id, matricule = "CE2-005", lastName = "ILBOUDO", firstName = "Wend-Kuni", niveau = "CE2", gender = "M", birthDate = "01/01/2017", parentContact = "+226 70 88 99 00"),
                Student(classId = class2Id, matricule = "CE2-006", lastName = "KONÉ", firstName = "Salimata", niveau = "CE2", gender = "F", birthDate = "14/07/2017", parentContact = "+225 01 23 45 67")
            )
            dao.insertStudents(studentsCe2)

            // Seed today's date "2026-11-02"
            val todayStr = "2026-11-02"

            // Seed Lesson Plans (Cahier Journal) for CM1
            dao.insertLesson(
                LessonPlan(
                    classId = class1Id,
                    subject = "Français",
                    gradeLevel = "CM1",
                    title = "Grammaire : L'accord du verbe avec son sujet",
                    dateScheduled = todayStr,
                    timeSlot = "08h00 - 08h45",
                    durationMinutes = 45,
                    objectives = "Identifier le sujet dans une phrase simple et accorder correctement le verbe au présent de l'indicatif.",
                    prerequisites = "Reconnaître le verbe d'action et les pronoms personnels sujets (je, tu, il/elle, nous, vous, ils/elles).",
                    materials = "Tableau noir, craies blanche et de couleur, ardoises individuelles, cahier de cours.",
                    phaseDiscovery = "Présentation au tableau d'une phrase du quotidien africain : 'Les élèves de l'école balaient la cour.' Questionnement : Qui balaie ? Quel est le verbe ?",
                    phaseActivity = "Travail en binômes : Remplacer 'Les élèves' par 'Fatoumata' puis par 'Mamadou et son frère'. Observer le changement de terminaison (-t / -ent).",
                    phasePractice = "Exercice sur ardoise : Écrire la terminaison correcte pour 3 phrases proposées par le maître. Correction collective immédiate.",
                    phaseSynthesis = "Règle retenue : Le verbe s'accorde toujours en nombre (singulier ou pluriel) et en personne avec son sujet.",
                    evaluation = "3 phrases à compléter individuellement sur le cahier du jour.",
                    remediation = "Pour les élèves confondant singulier et pluriel, entourer d'abord le sujet au crayon bleu avant de chercher la terminaison.",
                    actualSummary = "Séance très participative. 9 élèves sur 12 maîtrisent l'accord avec sujet inversé.",
                    status = "REALISE",
                    isAiGenerated = true
                )
            )

            dao.insertLesson(
                LessonPlan(
                    classId = class1Id,
                    subject = "Mathématiques",
                    gradeLevel = "CM1",
                    title = "Numération & Calcul : La division euclidienne avec reste",
                    dateScheduled = todayStr,
                    timeSlot = "09h00 - 09h45",
                    durationMinutes = 45,
                    objectives = "Poser et effectuer une division d'un nombre à deux chiffres par un diviseur à un chiffre avec reste.",
                    prerequisites = "Maîtrise des tables de multiplication de 2 à 7 et de la soustraction posée.",
                    materials = "Bâtonnets de bois de comptage ou capsules de bouteilles, tableau, ardoises.",
                    phaseDiscovery = "Situation problème : 'Le maître a 47 cahiers à distribuer équitablement entre 5 rangées d'élèves. Combien de cahiers reçoit chaque rangée ? Combien en reste-t-il ?'",
                    phaseActivity = "Manipulation avec les bâtonnets/capsules par groupes de 4 élèves. Partage concret puis transcription arithmétique.",
                    phasePractice = "Poser la division au tableau : 47 = (5 x 9) + 2. Vérification que le reste (2) est strictement inférieur au diviseur (5).",
                    phaseSynthesis = "Formule de la division : Dividende = (Diviseur x Quotient) + Reste, avec Reste < Diviseur.",
                    evaluation = "Poser et résoudre 2 divisions sur le cahier : 58 ÷ 6 et 39 ÷ 4.",
                    remediation = "Utiliser la bande numérique ou les tables écrites pour repérer le multiple inférieur le plus proche.",
                    actualSummary = "À consolider lors de la séance de remédiation jeudi.",
                    status = "PREVU",
                    isAiGenerated = false
                )
            )

            dao.insertLesson(
                LessonPlan(
                    classId = class1Id,
                    subject = "Éveil Scientifique",
                    gradeLevel = "CM1",
                    title = "Sciences : Le cycle de l'eau et la saison des pluies",
                    dateScheduled = todayStr,
                    timeSlot = "10h15 - 11h00",
                    durationMinutes = 45,
                    objectives = "Comprendre les états de l'eau (liquide, vapeur, glace) et le schéma général du grand cycle de l'eau.",
                    prerequisites = "Notion d'évaporation observée au soleil (linge qui sèche).",
                    materials = "Casserole d'eau tiède avec couvercle froid, schéma illustré du cycle de l'eau au tableau.",
                    phaseDiscovery = "Observation de la buée sur les vitres le matin et observation de l'eau qui bout.",
                    phaseActivity = "Expérience de condensation : la vapeur monte et forme des gouttelettes sous le couvercle froid.",
                    phasePractice = "Dessin du cycle au tableau : évaporation des fleuves et océans -> nuages -> précipitations -> ruissellement.",
                    phaseSynthesis = "L'eau se renouvelle sans cesse sur Terre grâce à l'énergie du soleil.",
                    evaluation = "Légender les 4 flèches du schéma du cycle de l'eau.",
                    remediation = "Fiche simplifiée avec vignettes à remettre dans l'ordre chronologique.",
                    actualSummary = "Bien assimilé par l'ensemble des élèves.",
                    status = "PREVU",
                    isAiGenerated = false
                )
            )

            // Seed Assessment & Grades for CM1
            val assessmentId = dao.insertAssessment(
                Assessment(
                    classId = class1Id,
                    subject = "Français",
                    title = "Contrôle N°1 : Orthographe & Accord Sujet-Verbe",
                    date = "2026-10-28",
                    maxScore = 20.0,
                    coefficient = 2,
                    period = "Trimestre 1"
                )
            )

            // Seed Homework
            dao.insertHomework(
                HomeworkAssignment(
                    classId = class1Id,
                    subject = "Mathématiques",
                    title = "Exercices N°3 et 4 page 42",
                    instructions = "Poser et vérifier 4 divisions euclidiennes sur le cahier de devoirs.",
                    assignedDate = todayStr,
                    dueDate = "2026-11-04",
                    isDone = false
                )
            )
            dao.insertHomework(
                HomeworkAssignment(
                    classId = class1Id,
                    subject = "Français",
                    title = "Apprendre la leçon de grammaire",
                    instructions = "Recopier la règle et mémoriser les 3 exemples sur l'accord du verbe.",
                    assignedDate = todayStr,
                    dueDate = "2026-11-03",
                    isDone = true
                )
            )
        }
    }
}
