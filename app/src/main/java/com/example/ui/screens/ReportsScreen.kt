package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.LessonPlan
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.ui.components.SectionHeader
import com.example.ui.theme.ChijuEmerald
import com.example.ui.theme.ChijuEmeraldContainer
import com.example.ui.theme.ChijuOnEmeraldContainer

@Composable
fun ReportsScreen(
    activeClass: SchoolClass?,
    students: List<Student>,
    attendanceRecords: List<AttendanceRecord>,
    lessons: List<LessonPlan>,
    todayDate: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedReportType by remember { mutableStateOf("JOURNAL") }

    val reportText = remember(selectedReportType, activeClass, students, attendanceRecords, lessons) {
        generateOfficialDocumentText(
            type = selectedReportType,
            schoolClass = activeClass,
            students = students,
            attendance = attendanceRecords,
            lessons = lessons,
            todayDate = todayDate
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Générateur de Rapports & Bulletins Officiels",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Édition conforme pour direction, inspection et parents",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReportTypeButton(
                            title = "Bilan Journal",
                            isSelected = selectedReportType == "JOURNAL",
                            modifier = Modifier.weight(1f),
                            onClick = { selectedReportType = "JOURNAL" }
                        )
                        ReportTypeButton(
                            title = "Appel & Absences",
                            isSelected = selectedReportType == "PRESENCE",
                            modifier = Modifier.weight(1f),
                            onClick = { selectedReportType = "PRESENCE" }
                        )
                        ReportTypeButton(
                            title = "Bulletin Type",
                            isSelected = selectedReportType == "BULLETIN",
                            modifier = Modifier.weight(1f),
                            onClick = { selectedReportType = "BULLETIN" }
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aperçu du document officiel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Row {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Rapport Scolaire CHIJU", reportText)
                                    clipboard.setPrimaryClip(clip)
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = ChijuEmerald)
                            }
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, reportText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Partager le rapport CHIJU")
                                    context.startActivity(shareIntent)
                                }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Partager", tint = ChijuEmerald)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = reportText,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportTypeButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) ChijuEmerald else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun generateOfficialDocumentText(
    type: String,
    schoolClass: SchoolClass?,
    students: List<Student>,
    attendance: List<AttendanceRecord>,
    lessons: List<LessonPlan>,
    todayDate: String
): String {
    val school = schoolClass?.schoolName ?: "École Publique Principale"
    val clsName = schoolClass?.name ?: "CM1 Excellence"
    val year = schoolClass?.schoolYear ?: "2026-2027"

    return when (type) {
        "JOURNAL" -> buildString {
            appendLine("==================================================")
            appendLine("           CAHIER JOURNAL — CHIJU APP")
            appendLine(" Établissement : $school")
            appendLine(" Classe        : $clsName | Année : $year")
            appendLine(" Date          : $todayDate")
            appendLine("==================================================")
            appendLine()
            appendLine("I. SÉANCES PRÉVUES & DÉROULEMENT DU JOUR")
            if (lessons.isEmpty()) {
                appendLine(" Aucune séance saisie pour cette date.")
            } else {
                lessons.forEachIndexed { i, l ->
                    appendLine(" [${i + 1}] ${l.timeSlot} — ${l.subject.uppercase()}")
                    appendLine("     Titre      : ${l.title}")
                    appendLine("     Objectifs  : ${l.objectives.take(80)}...")
                    appendLine("     Statut     : ${l.status}")
                    if (l.actualSummary.isNotBlank()) {
                        appendLine("     Bilan Réel : ${l.actualSummary}")
                    }
                    appendLine("--------------------------------------------------")
                }
            }
            appendLine()
            appendLine("II. VISA DE L'ENSEIGNANT :")
            appendLine(" Fait et arrêté à la date du $todayDate.")
            appendLine(" Signature de l'Enseignant Titulaire : ____________")
        }

        "PRESENCE" -> buildString {
            val presents = attendance.count { it.status == "PRESENT" }
            val absents = attendance.count { it.status == "ABSENT" }
            val lates = attendance.count { it.status == "RETARD" }

            appendLine("==================================================")
            appendLine("       REGISTRE D'APPEL & RELEVÉ D'ABSENCES")
            appendLine(" Établissement : $school")
            appendLine(" Classe        : $clsName | Effectif Total : ${students.size}")
            appendLine(" Date d'appel  : $todayDate")
            appendLine("==================================================")
            appendLine()
            appendLine("STATISTIQUES DE FRÉQUENTATION :")
            appendLine(" - Élèves Présents  : $presents")
            appendLine(" - Élèves Absents   : $absents")
            appendLine(" - Élèves en Retard : $lates")
            val rate = if (students.isNotEmpty()) (presents.toDouble() / students.size * 100).toInt() else 100
            appendLine(" - Taux de présence : $rate%")
            appendLine()
            appendLine("DÉTAIL NOMINATIF :")
            students.forEachIndexed { idx, s ->
                val rec = attendance.find { it.studentId == s.id }
                val st = rec?.status ?: "PRESENT"
                appendLine(" %02d. %-24s [%s]".format(idx + 1, s.fullName, st))
            }
            appendLine("==================================================")
            appendLine("Visa de la Direction de l'Établissement : ________")
        }

        else -> buildString {
            // BULLETIN TYPE
            val sampleStudent = students.firstOrNull()?.fullName ?: "KOUAMÉ Jean-Yves"
            appendLine("==================================================")
            appendLine("       BULLETIN SCOLAIRE PÉRIODIQUE")
            appendLine(" Établissement : $school")
            appendLine(" Année Scolaire: $year | Période : 1er Trimestre")
            appendLine("==================================================")
            appendLine(" Élève      : $sampleStudent")
            appendLine(" Classe     : $clsName | Matricule : 2026-004")
            appendLine("--------------------------------------------------")
            appendLine(" DISCIPLINE          | COEFF | NOTE/20 | APPRÉCIATION")
            appendLine(" Français / Dictée   |   2   |  15.5   | Très bien")
            appendLine(" Mathématiques       |   2   |  16.0   | Bien maîtrisé")
            appendLine(" Éveil Scientifique  |   1   |  14.0   | Bon travail")
            appendLine(" Histoire-Géo        |   1   |  13.5   | Satisfaisant")
            appendLine(" Éducation Civique   |   1   |  17.0   | Exemplaire")
            appendLine("--------------------------------------------------")
            appendLine(" MOYENNE GÉNÉRALE : 15.35 / 20")
            appendLine(" RANG : 2e sur ${students.size} élèves")
            appendLine(" TABLEAU D'HONNEUR & FÉLICITATIONS DU CONSEIL")
            appendLine("==================================================")
            appendLine(" Le Maître : __________     La Direction : _______")
        }
    }
}
