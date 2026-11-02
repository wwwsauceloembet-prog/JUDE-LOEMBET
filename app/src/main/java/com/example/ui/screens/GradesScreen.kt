package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Assessment
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.StudentGrade
import com.example.data.repository.AssessmentStats
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.ChijuEmerald
import com.example.ui.theme.ChijuEmeraldContainer
import com.example.ui.theme.ChijuGold
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusPresent
import kotlinx.coroutines.flow.Flow

@Composable
fun GradesScreen(
    activeClass: SchoolClass?,
    assessments: List<Assessment>,
    students: List<Student>,
    todayDate: String,
    onAddAssessment: (String, String, String, Double, Int, String) -> Unit,
    onDeleteAssessment: (Assessment) -> Unit,
    getGradesForAssessment: (Long) -> Flow<List<StudentGrade>>,
    onSaveGrade: (Long, Long, Double?, Boolean, String) -> Unit,
    computeStats: (List<StudentGrade>, Double) -> AssessmentStats,
    modifier: Modifier = Modifier
) {
    var selectedAssessment by remember { mutableStateOf<Assessment?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    if (selectedAssessment != null) {
        // Detailed Grades Entry Screen
        AssessmentGradesEntryView(
            assessment = selectedAssessment!!,
            students = students,
            gradesFlow = getGradesForAssessment(selectedAssessment!!.id),
            onBack = { selectedAssessment = null },
            onSaveGrade = { studentId, score, isAbsent, comment ->
                onSaveGrade(selectedAssessment!!.id, studentId, score, isAbsent, comment)
            },
            computeStats = computeStats
        )
    } else {
        // Assessments List View
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Carnet d'Évaluations & Notes",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Classe : ${activeClass?.name ?: "Primaire"} • Calcul déterministe",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ChijuEmeraldContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${assessments.size} contrôles",
                                    color = ChijuEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_create_assessment")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Créer une évaluation (Contrôle / Composition)")
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "Évaluations du trimestre")
            }

            if (assessments.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Grade, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Aucune évaluation enregistrée pour le moment", fontWeight = FontWeight.Medium)
                            Text("Créez votre première évaluation pour saisir les notes des élèves.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(assessments) { assessment ->
                    AssessmentListItem(
                        assessment = assessment,
                        onClick = { selectedAssessment = assessment },
                        onDelete = { onDeleteAssessment(assessment) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateAssessmentDialog(
            todayDate = todayDate,
            onDismiss = { showCreateDialog = false },
            onConfirm = { subject, title, date, maxScore, coeff, period ->
                onAddAssessment(subject, title, date, maxScore, coeff, period)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun AssessmentListItem(
    assessment: Assessment,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SubjectBadge(subject = assessment.subject)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Barème: /${assessment.maxScore.toInt()} • Coeff: ${assessment.coefficient}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = assessment.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Date : ${assessment.date} • ${assessment.period}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ChijuEmeraldContainer,
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = "Saisir notes",
                        color = ChijuEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = StatusAbsent, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AssessmentGradesEntryView(
    assessment: Assessment,
    students: List<Student>,
    gradesFlow: Flow<List<StudentGrade>>,
    onBack: () -> Unit,
    onSaveGrade: (Long, Double?, Boolean, String) -> Unit,
    computeStats: (List<StudentGrade>, Double) -> AssessmentStats
) {
    val grades by gradesFlow.collectAsState(initial = emptyList())
    val stats = remember(grades, assessment.maxScore) {
        computeStats(grades, assessment.maxScore)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Header with Back button
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = assessment.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${assessment.subject} • Barème : /${assessment.maxScore.toInt()} (Coeff. ${assessment.coefficient})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Deterministic Statistics Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Moyenne de classe", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "%.2f / %.0f".format(stats.average, assessment.maxScore),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (stats.average >= assessment.maxScore / 2) ChijuEmerald else StatusAbsent
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Taux de réussite", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "%.1f%%".format(stats.successRate),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (stats.successRate >= 50.0) ChijuEmerald else StatusAbsent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Note la plus basse : %.1f".format(stats.min), fontSize = 11.sp)
                        Text(text = "Note la plus haute : %.1f".format(stats.max), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Saisies : ${grades.count { it.score != null }}/${students.size}", fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Saisie des notes par élève")
        }

        items(students) { student ->
            val existingGrade = grades.find { it.studentId == student.id }
            StudentGradeInputRow(
                student = student,
                maxScore = assessment.maxScore,
                existingScore = existingGrade?.score,
                isAbsent = existingGrade?.isAbsent ?: false,
                onSave = { score, isAbs ->
                    onSaveGrade(student.id, score, isAbs, "")
                }
            )
        }
    }
}

@Composable
fun StudentGradeInputRow(
    student: Student,
    maxScore: Double,
    existingScore: Double?,
    isAbsent: Boolean,
    onSave: (Double?, Boolean) -> Unit
) {
    var scoreText by remember(existingScore) {
        mutableStateOf(existingScore?.toString() ?: "")
    }
    var absentState by remember(isAbsent) {
        mutableStateOf(isAbsent)
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "N° ${student.matricule}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Absent toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = absentState,
                        onCheckedChange = { checked ->
                            absentState = checked
                            if (checked) {
                                scoreText = ""
                                onSave(null, true)
                            } else {
                                onSave(scoreText.toDoubleOrNull(), false)
                            }
                        }
                    )
                    Text("Abs", fontSize = 11.sp, color = if (absentState) StatusAbsent else Color.Gray)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Score Input Field
                OutlinedTextField(
                    value = if (absentState) "" else scoreText,
                    onValueChange = { input ->
                        if (!absentState) {
                            scoreText = input
                            val num = input.replace(',', '.').toDoubleOrNull()
                            if (num != null && num <= maxScore && num >= 0) {
                                onSave(num, false)
                            }
                        }
                    },
                    enabled = !absentState,
                    placeholder = { Text("/${maxScore.toInt()}") },
                    modifier = Modifier
                        .width(78.dp)
                        .testTag("grade_input_${student.id}"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun CreateAssessmentDialog(
    todayDate: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Double, Int, String) -> Unit
) {
    var subject by remember { mutableStateOf("Français") }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(todayDate) }
    var maxScoreStr by remember { mutableStateOf("20") }
    var coeffStr by remember { mutableStateOf("1") }
    var period by remember { mutableStateOf("Trimestre 1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle Évaluation") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Matière (ex: Français, Mathématiques...)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre du contrôle (ex: Dictée & Questions N°2)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = maxScoreStr,
                        onValueChange = { maxScoreStr = it },
                        label = { Text("Barème (/10 ou /20)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = coeffStr,
                        onValueChange = { coeffStr = it },
                        label = { Text("Coefficient") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = period,
                    onValueChange = { period = it },
                    label = { Text("Période (Trimestre 1, 2 ou 3)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val maxScore = maxScoreStr.toDoubleOrNull() ?: 20.0
                        val coeff = coeffStr.toIntOrNull() ?: 1
                        onConfirm(subject, title, date, maxScore, coeff, period)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald)
            ) {
                Text("Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
