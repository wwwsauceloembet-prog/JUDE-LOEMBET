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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LessonPlan
import com.example.data.model.SchoolClass
import com.example.ui.components.SectionHeader
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.ChijuEmerald
import com.example.ui.theme.ChijuEmeraldContainer
import com.example.ui.theme.ChijuGold
import com.example.ui.theme.ChijuOnEmeraldContainer
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusPresent

@Composable
fun LessonPlannerScreen(
    activeClass: SchoolClass?,
    lessons: List<LessonPlan>,
    todayDate: String,
    onSaveLesson: (LessonPlan) -> Unit,
    onUpdateStatus: (LessonPlan, String, String) -> Unit,
    onDeleteLesson: (LessonPlan) -> Unit,
    onOpenAiAssistant: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedLessonForBilan by remember { mutableStateOf<LessonPlan?>(null) }

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
                                text = "Cahier Journal Officiel",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Programme de ${activeClass?.name ?: "la classe"} • $todayDate",
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
                                text = "${lessons.size} séances",
                                color = ChijuOnEmeraldContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_new_lesson")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nouvelle séance")
                        }

                        OutlinedButton(
                            onClick = onOpenAiAssistant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ChijuGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Générer par IA", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Déroulement chronologique des séances")
        }

        if (lessons.isEmpty()) {
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
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Le cahier journal est vide pour le moment",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Ajoutez une séance manuellement ou générez une fiche complète avec l'Assistant IA.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(lessons) { lesson ->
                DetailedLessonPlanCard(
                    lesson = lesson,
                    onUpdateStatusClick = { selectedLessonForBilan = lesson },
                    onDeleteClick = { onDeleteLesson(lesson) }
                )
            }
        }
    }

    if (showAddDialog) {
        CreateLessonDialog(
            gradeLevel = activeClass?.gradeLevel ?: "CM1",
            todayDate = todayDate,
            onDismiss = { showAddDialog = false },
            onConfirm = { newLesson ->
                activeClass?.let { cls ->
                    onSaveLesson(newLesson.copy(classId = cls.id))
                }
                showAddDialog = false
            }
        )
    }

    selectedLessonForBilan?.let { lesson ->
        UpdateBilanDialog(
            lesson = lesson,
            onDismiss = { selectedLessonForBilan = null },
            onConfirm = { status, summary ->
                onUpdateStatus(lesson, status, summary)
                selectedLessonForBilan = null
            }
        )
    }
}

@Composable
fun DetailedLessonPlanCard(
    lesson: LessonPlan,
    onUpdateStatusClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Time, Subject, Title, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${lesson.timeSlot} (${lesson.durationMinutes} min)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Status Badge
                val (statusLabel, statusBg, statusColor) = when (lesson.status) {
                    "REALISE" -> Triple("RÉALISÉ", Color(0xFFDCFCE7), Color(0xFF15803D))
                    "REPORTE" -> Triple("REPORTÉ", Color(0xFFFEE2E2), Color(0xFFB91C1C))
                    else -> Triple("PRÉVU", Color(0xFFE0F2FE), Color(0xFF0369A1))
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg,
                    modifier = Modifier.clickable { onUpdateStatusClick() }
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                SubjectBadge(subject = lesson.subject)
                Spacer(modifier = Modifier.width(6.dp))
                if (lesson.isAiGenerated) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ChijuGold.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Généré par IA", color = ChijuGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (lesson.objectives.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🎯 Objectif : ${lesson.objectives}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expandable Pedagogical Content
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    if (lesson.materials.isNotBlank()) {
                        PedagogicalStepItem(label = "Matériel didactique", content = lesson.materials)
                    }
                    if (lesson.prerequisites.isNotBlank()) {
                        PedagogicalStepItem(label = "Prérequis", content = lesson.prerequisites)
                    }
                    if (lesson.phaseDiscovery.isNotBlank()) {
                        PedagogicalStepItem(label = "Phase 1 - Découverte / Accroche", content = lesson.phaseDiscovery)
                    }
                    if (lesson.phaseActivity.isNotBlank()) {
                        PedagogicalStepItem(label = "Phase 2 - Recherche & Manipulation", content = lesson.phaseActivity)
                    }
                    if (lesson.phasePractice.isNotBlank()) {
                        PedagogicalStepItem(label = "Phase 3 - Institutionnalisation & Ardoises", content = lesson.phasePractice)
                    }
                    if (lesson.phaseSynthesis.isNotBlank()) {
                        PedagogicalStepItem(label = "Phase 4 - Synthèse & Trace écrite", content = lesson.phaseSynthesis)
                    }
                    if (lesson.evaluation.isNotBlank()) {
                        PedagogicalStepItem(label = "Évaluation formative", content = lesson.evaluation)
                    }
                    if (lesson.remediation.isNotBlank()) {
                        PedagogicalStepItem(label = "Remédiation immédiate", content = lesson.remediation)
                    }
                    if (lesson.actualSummary.isNotBlank()) {
                        PedagogicalStepItem(label = "Bilan réel du maître (Après la classe)", content = lesson.actualSummary, isHighlight = true)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onUpdateStatusClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bilan réel / Statut", fontSize = 12.sp)
                        }

                        TextButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = StatusAbsent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Supprimer", color = StatusAbsent, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Expand / Collapse Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Masquer le déroulement" else "Voir la fiche pédagogique complète",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PedagogicalStepItem(label: String, content: String, isHighlight: Boolean = false) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) ChijuEmerald else MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun CreateLessonDialog(
    gradeLevel: String,
    todayDate: String,
    onDismiss: () -> Unit,
    onConfirm: (LessonPlan) -> Unit
) {
    var subject by remember { mutableStateOf("Français") }
    var title by remember { mutableStateOf("") }
    var timeSlot by remember { mutableStateOf("08h00 - 08h45") }
    var duration by remember { mutableStateOf("45") }
    var objectives by remember { mutableStateOf("") }
    var discovery by remember { mutableStateOf("") }
    var practice by remember { mutableStateOf("") }
    var synthesis by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une séance au Cahier Journal") },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Matière (Français, Mathématiques, Sciences...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de la séance (ex: La soustraction posée)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = timeSlot,
                            onValueChange = { timeSlot = it },
                            label = { Text("Horaire") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Durée (min)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = objectives,
                        onValueChange = { objectives = it },
                        label = { Text("Compétences & Objectifs visés") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = discovery,
                        onValueChange = { discovery = it },
                        label = { Text("Découverte / Activité préparatoire") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = practice,
                        onValueChange = { practice = it },
                        label = { Text("Pratique sur ardoise & Exercices") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = synthesis,
                        onValueChange = { synthesis = it },
                        label = { Text("Synthèse / Trace écrite") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val plan = LessonPlan(
                            classId = 0,
                            subject = subject,
                            gradeLevel = gradeLevel,
                            title = title,
                            dateScheduled = todayDate,
                            timeSlot = timeSlot,
                            durationMinutes = duration.toIntOrNull() ?: 45,
                            objectives = objectives,
                            prerequisites = "Matériel et connaissances préalables",
                            materials = "Tableau, craie, ardoises individuelles",
                            phaseDiscovery = discovery,
                            phaseActivity = "Travail en groupe et recherche",
                            phasePractice = practice,
                            phaseSynthesis = synthesis,
                            evaluation = "Questions de contrôle au tableau",
                            remediation = "Exercices guidés pour les élèves en difficulté",
                            actualSummary = "",
                            status = "PREVU",
                            isAiGenerated = false
                        )
                        onConfirm(plan)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald)
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun UpdateBilanDialog(
    lesson: LessonPlan,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var status by remember { mutableStateOf(lesson.status) }
    var actualSummary by remember { mutableStateOf(lesson.actualSummary) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bilan réel de la séance") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Séance : ${lesson.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Text("Statut de la séance :", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { status = "REALISE" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status == "REALISE") StatusPresent else Color.LightGray
                        )
                    ) { Text("Réalisé") }
                    Button(
                        onClick = { status = "PREVU" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status == "PREVU") Color(0xFF0284C7) else Color.LightGray
                        )
                    ) { Text("Prévu") }
                    Button(
                        onClick = { status = "REPORTE" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status == "REPORTE") StatusAbsent else Color.LightGray
                        )
                    ) { Text("Reporté") }
                }

                OutlinedTextField(
                    value = actualSummary,
                    onValueChange = { actualSummary = it },
                    label = { Text("Observations du maître & Adaptations") },
                    placeholder = { Text("Ex: 10 élèves sur 12 ont réussi l'exercice...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(status, actualSummary) },
                colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald)
            ) {
                Text("Valider le bilan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
