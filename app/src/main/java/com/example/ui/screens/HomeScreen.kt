package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.HomeworkAssignment
import com.example.data.model.LessonPlan
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.SubscriptionInfo
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.SubjectBadge
import com.example.ui.theme.ChijuAirtelRed
import com.example.ui.theme.ChijuEmerald
import com.example.ui.theme.ChijuEmeraldContainer
import com.example.ui.theme.ChijuGold
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusPresent
import com.example.ui.viewmodel.ChijuScreen

@Composable
fun HomeScreen(
    activeClass: SchoolClass?,
    students: List<Student>,
    attendanceRecords: List<AttendanceRecord>,
    lessons: List<LessonPlan>,
    homework: List<HomeworkAssignment>,
    subscription: SubscriptionInfo?,
    todayDate: String,
    onNavigate: (ChijuScreen) -> Unit,
    onMarkAllPresent: () -> Unit,
    onToggleHomework: (HomeworkAssignment) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalStudents = students.size
    val presentCount = attendanceRecords.count { it.status == "PRESENT" }
    val absentCount = attendanceRecords.count { it.status == "ABSENT" }
    val lateCount = attendanceRecords.count { it.status == "RETARD" }

    val rate = if (totalStudents > 0) {
        val accounted = if (attendanceRecords.isNotEmpty()) presentCount else totalStudents
        (accounted.toDouble() / totalStudents * 100).toInt()
    } else 100

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card with Date & Class
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_welcome_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bonjour, Enseignant",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Lundi 2 Novembre 2026 • Premier Trimestre",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ChijuEmeraldContainer
                        ) {
                            Text(
                                text = activeClass?.gradeLevel ?: "Primaire",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ChijuEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Subscription reminder banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(ChijuScreen.SUBSCRIPTION) }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(ChijuAirtelRed)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Airtel Money (+241 77793074) • 10 000 FCFA/an",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ChijuAirtelRed
                                )
                            }
                            Text(
                                text = "${subscription?.daysRemaining ?: 312}j",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Quick Action Row
        item {
            Column {
                SectionHeader(title = "Accès Rapides")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = "Faire l'appel",
                        icon = Icons.Default.FactCheck,
                        color = ChijuEmerald,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_attendance"),
                        onClick = { onNavigate(ChijuScreen.MY_CLASS) }
                    )
                    QuickActionTile(
                        title = "Assistant IA",
                        icon = Icons.Default.AutoAwesome,
                        color = ChijuGold,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_ai"),
                        onClick = { onNavigate(ChijuScreen.AI_ASSISTANT) }
                    )
                    QuickActionTile(
                        title = "Cahier Journal",
                        icon = Icons.Default.MenuBook,
                        color = Color(0xFF0284C7),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_journal"),
                        onClick = { onNavigate(ChijuScreen.LESSON_PLANNER) }
                    )
                    QuickActionTile(
                        title = "Saisir Notes",
                        icon = Icons.Default.Grade,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_grades"),
                        onClick = { onNavigate(ChijuScreen.GRADES) }
                    )
                }
            }
        }

        // Daily Attendance Stats
        item {
            Column {
                SectionHeader(
                    title = "Présences du jour",
                    actionText = "Tous Présents (1 clic)",
                    onActionClick = onMarkAllPresent
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Inscrits",
                        value = totalStudents.toString(),
                        icon = Icons.Default.People,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Présents",
                        value = if (attendanceRecords.isNotEmpty()) presentCount.toString() else "$totalStudents*",
                        icon = Icons.Default.CheckCircle,
                        color = StatusPresent,
                        modifier = Modifier.weight(1f),
                        subtitle = "$rate%"
                    )
                    StatCard(
                        title = "Absents",
                        value = absentCount.toString(),
                        icon = Icons.Outlined.Close,
                        color = StatusAbsent,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Retards",
                        value = lateCount.toString(),
                        icon = Icons.Default.Schedule,
                        color = StatusLate,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Today's Timetable & Lessons in Cahier Journal
        item {
            SectionHeader(
                title = "Séances du Cahier Journal (Aujourd'hui)",
                actionText = "+ Ajouter",
                onActionClick = { onNavigate(ChijuScreen.LESSON_PLANNER) }
            )
        }

        if (lessons.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucune séance enregistrée pour aujourd'hui",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onNavigate(ChijuScreen.AI_ASSISTANT) },
                            colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Générer une leçon avec l'IA")
                        }
                    }
                }
            }
        } else {
            items(lessons) { lesson ->
                LessonScheduleItem(
                    lesson = lesson,
                    onClick = { onNavigate(ChijuScreen.LESSON_PLANNER) }
                )
            }
        }

        // Homework & Reminders
        item {
            SectionHeader(
                title = "Devoirs & Travaux de maison",
                actionText = "Gérer",
                onActionClick = { onNavigate(ChijuScreen.LESSON_PLANNER) }
            )
        }

        if (homework.isEmpty()) {
            item {
                Text(
                    text = "Aucun devoir à rendre cette semaine.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(homework) { hw ->
                HomeworkRowItem(
                    homework = hw,
                    onToggle = { onToggleHomework(hw) }
                )
            }
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LessonScheduleItem(
    lesson: LessonPlan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(70.dp)
            ) {
                Text(
                    text = lesson.timeSlot.substringBefore(" - "),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${lesson.durationMinutes} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SubjectBadge(subject = lesson.subject)
                    Spacer(modifier = Modifier.width(6.dp))
                    if (lesson.isAiGenerated) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ChijuGold.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ChijuGold,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("IA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ChijuGold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status chip
            val (statusText, statusBg, statusColor) = when (lesson.status) {
                "REALISE" -> Triple("Réalisé", Color(0xFFDCFCE7), Color(0xFF15803D))
                "REPORTE" -> Triple("Reporté", Color(0xFFFEE2E2), Color(0xFFB91C1C))
                else -> Triple("Prévu", Color(0xFFE0F2FE), Color(0xFF0369A1))
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusBg
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HomeworkRowItem(
    homework: HomeworkAssignment,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (homework.isDone) Icons.Default.CheckCircle else Icons.Outlined.Check,
                    contentDescription = "Marquer fait",
                    tint = if (homework.isDone) StatusPresent else Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SubjectBadge(subject = homework.subject)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pour le ${homework.dueDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = homework.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
