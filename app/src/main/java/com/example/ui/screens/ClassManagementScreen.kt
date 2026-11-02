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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.AttendanceRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.ui.components.SectionHeader
import com.example.ui.theme.ChijuEmerald
import com.example.ui.theme.ChijuEmeraldContainer
import com.example.ui.theme.ChijuGold
import com.example.ui.theme.ChijuOnEmeraldContainer
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusJustified
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusPresent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassManagementScreen(
    activeClass: SchoolClass?,
    allClasses: List<SchoolClass>,
    students: List<Student>,
    attendanceRecords: List<AttendanceRecord>,
    todayDate: String,
    onMarkAllPresent: () -> Unit,
    onSetAttendance: (Long, String) -> Unit,
    onAddStudent: (String, String, String, String, String, String) -> Unit,
    onCreateClass: (String, String, String, String) -> Unit,
    onDeleteStudent: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showCreateClassDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Selector: 0: Appel & Présences, 1: Élèves & Classes
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Faire l'appel", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_attendance")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Élèves (${students.size})", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_students")
            )
        }

        if (selectedTab == 0) {
            // Roll Call View
            AttendanceRollCallView(
                activeClass = activeClass,
                students = students,
                attendanceRecords = attendanceRecords,
                todayDate = todayDate,
                onMarkAllPresent = onMarkAllPresent,
                onSetAttendance = onSetAttendance
            )
        } else {
            // Student List View
            StudentsListView(
                activeClass = activeClass,
                students = students,
                onOpenAddStudent = { showAddStudentDialog = true },
                onOpenCreateClass = { showCreateClassDialog = true },
                onDeleteStudent = onDeleteStudent
            )
        }
    }

    if (showAddStudentDialog) {
        AddStudentDialog(
            gradeLevel = activeClass?.gradeLevel ?: "CM1",
            onDismiss = { showAddStudentDialog = false },
            onConfirm = { fn, ln, gen, bd, contact, niv ->
                onAddStudent(fn, ln, gen, bd, contact, niv)
                showAddStudentDialog = false
            }
        )
    }

    if (showCreateClassDialog) {
        CreateClassDialog(
            onDismiss = { showCreateClassDialog = false },
            onConfirm = { name, level, room, school ->
                onCreateClass(name, level, room, school)
                showCreateClassDialog = false
            }
        )
    }
}

@Composable
fun AttendanceRollCallView(
    activeClass: SchoolClass?,
    students: List<Student>,
    attendanceRecords: List<AttendanceRecord>,
    todayDate: String,
    onMarkAllPresent: () -> Unit,
    onSetAttendance: (Long, String) -> Unit
) {
    val presentCount = attendanceRecords.count { it.status == "PRESENT" }
    val absentCount = attendanceRecords.count { it.status == "ABSENT" }
    val lateCount = attendanceRecords.count { it.status == "RETARD" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Action Banner: 1-Click Roll Call
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ChijuEmeraldContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Registre d'appel journalier",
                            fontWeight = FontWeight.Bold,
                            color = ChijuOnEmeraldContainer,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "$todayDate • Séance du Matin",
                            fontSize = 12.sp,
                            color = ChijuOnEmeraldContainer.copy(alpha = 0.8f)
                        )
                    }

                    Button(
                        onClick = onMarkAllPresent,
                        colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald),
                        modifier = Modifier.testTag("mark_all_present_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tous Présents", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Stats summary chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusMiniChip(label = "Présents", count = presentCount, color = StatusPresent, modifier = Modifier.weight(1f))
                StatusMiniChip(label = "Absents", count = absentCount, color = StatusAbsent, modifier = Modifier.weight(1f))
                StatusMiniChip(label = "Retards", count = lateCount, color = StatusLate, modifier = Modifier.weight(1f))
            }
        }

        item {
            SectionHeader(title = "Appel nominal (${students.size} élèves)")
        }

        if (students.isEmpty()) {
            item {
                Text(
                    text = "Aucun élève inscrit dans cette classe. Ajoutez des élèves dans l'onglet 'Élèves'.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        } else {
            items(students) { student ->
                val record = attendanceRecords.find { it.studentId == student.id }
                val currentStatus = record?.status ?: "PRESENT"

                AttendanceStudentRow(
                    student = student,
                    currentStatus = currentStatus,
                    onStatusChange = { newStatus -> onSetAttendance(student.id, newStatus) }
                )
            }
        }
    }
}

@Composable
fun AttendanceStudentRow(
    student: Student,
    currentStatus: String,
    onStatusChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (student.gender == "F") Color(0xFFFCE7F3) else Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.gender,
                        fontWeight = FontWeight.Bold,
                        color = if (student.gender == "F") Color(0xFFBE185D) else Color(0xFF0369A1),
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = student.fullName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "N° ${student.matricule}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Status Buttons: P (Présent), A (Absent), R (Retard), J (Justifié)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AttendanceStatusButton(
                    label = "P",
                    isSelected = currentStatus == "PRESENT",
                    activeColor = StatusPresent,
                    onClick = { onStatusChange("PRESENT") }
                )
                AttendanceStatusButton(
                    label = "A",
                    isSelected = currentStatus == "ABSENT",
                    activeColor = StatusAbsent,
                    onClick = { onStatusChange("ABSENT") }
                )
                AttendanceStatusButton(
                    label = "R",
                    isSelected = currentStatus == "RETARD",
                    activeColor = StatusLate,
                    onClick = { onStatusChange("RETARD") }
                )
                AttendanceStatusButton(
                    label = "J",
                    isSelected = currentStatus == "JUSTIFIE",
                    activeColor = StatusJustified,
                    onClick = { onStatusChange("JUSTIFIE") }
                )
            }
        }
    }
}

@Composable
fun AttendanceStatusButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .size(34.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatusMiniChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
            Text(text = count.toString(), fontSize = 14.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StudentsListView(
    activeClass: SchoolClass?,
    students: List<Student>,
    onOpenAddStudent: () -> Unit,
    onOpenCreateClass: () -> Unit,
    onDeleteStudent: (Student) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenAddStudent,
                    colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_student_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ajouter un élève")
                }
                Button(
                    onClick = onOpenCreateClass,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nouvelle classe")
                }
            }
        }

        item {
            SectionHeader(title = "Effectif de la classe (${students.size} inscrits)")
        }

        items(students) { student ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ChijuEmeraldContainer
                                ) {
                                    Text(
                                        text = student.niveau,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ChijuEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Matricule: ${student.matricule} • Né(e) le: ${student.birthDate.ifBlank { "N/C" }}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (student.parentContact.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = null,
                                        tint = ChijuEmerald,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = student.parentContact,
                                        fontSize = 11.sp,
                                        color = ChijuEmerald
                                    )
                                }
                            }
                        }
                    }

                    TextButton(onClick = { onDeleteStudent(student) }) {
                        Text("Retirer", color = StatusAbsent, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddStudentDialog(
    gradeLevel: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var niveau by remember { mutableStateOf(gradeLevel) }
    var gender by remember { mutableStateOf("M") }
    var birthDate by remember { mutableStateOf("") }
    var parentContact by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Inscrire un nouvel élève") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nom de famille (ex: KOUAMÉ)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Prénom(s) (ex: Yao Jean)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = niveau,
                    onValueChange = { niveau = it },
                    label = { Text("Niveau scolaire (CP1, CP2, CE1, CE2, CM1, CM2)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Genre : ", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { gender = "M" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (gender == "M") ChijuEmerald else Color.LightGray
                        )
                    ) { Text("Garçon (M)") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { gender = "F" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (gender == "F") Color(0xFFBE185D) else Color.LightGray
                        )
                    ) { Text("Fille (F)") }
                }
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("Date de naissance (JJ/MM/AAAA)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = parentContact,
                    onValueChange = { parentContact = it },
                    label = { Text("Téléphone du parent / tuteur") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (lastName.isNotBlank() && firstName.isNotBlank()) {
                        onConfirm(firstName, lastName, gender, birthDate, parentContact, niveau)
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
fun CreateClassDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("CM1") }
    var room by remember { mutableStateOf("Salle 01") }
    var school by remember { mutableStateOf("École Publique Principale") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Créer une nouvelle classe") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la classe (ex: CM2 Bilingue)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = level,
                    onValueChange = { level = it },
                    label = { Text("Niveau (CP1, CP2, CE1, CE2, CM1, CM2)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Salle ou bâtiment") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("Établissement") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, level, room, school)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald)
            ) {
                Text("Créer la classe")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
