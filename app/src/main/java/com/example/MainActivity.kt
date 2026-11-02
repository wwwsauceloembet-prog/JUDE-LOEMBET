package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChijuTopAppBar
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.ClassManagementScreen
import com.example.ui.screens.GradesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LessonPlannerScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ChijuEmerald
import com.example.ui.theme.ChijuEmeraldContainer
import com.example.ui.theme.ChijuTheme
import com.example.ui.viewmodel.ChijuScreen
import com.example.ui.viewmodel.ChijuViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ChijuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChijuTheme {
                ChijuApp(viewModel = viewModel)
            }
        }
    }
}

data class NavTabItem(
    val screen: ChijuScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun ChijuApp(viewModel: ChijuViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val classes by viewModel.classes.collectAsState()
    val activeClass by viewModel.activeClass.collectAsState()
    val students by viewModel.students.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val lessons by viewModel.lessons.collectAsState()
    val assessments by viewModel.assessments.collectAsState()
    val homework by viewModel.homework.collectAsState()
    val subscription by viewModel.subscription.collectAsState()
    val todayDate by viewModel.todayDate.collectAsState()

    val aiResult by viewModel.aiResult.collectAsState()
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissUserMessage()
        }
    }

    // Predictive Back Handling: Return to Home if on subscreen
    BackHandler(enabled = currentScreen != ChijuScreen.HOME) {
        viewModel.navigateTo(ChijuScreen.HOME)
    }

    val bottomNavItems = listOf(
        NavTabItem(ChijuScreen.HOME, "Accueil", Icons.Default.Home, Icons.Outlined.Home, "nav_home"),
        NavTabItem(ChijuScreen.MY_CLASS, "Ma classe", Icons.Default.Groups, Icons.Outlined.Groups, "nav_my_class"),
        NavTabItem(ChijuScreen.LESSON_PLANNER, "Journal", Icons.Default.MenuBook, Icons.Outlined.MenuBook, "nav_journal"),
        NavTabItem(ChijuScreen.AI_ASSISTANT, "Assistant IA", Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_ai"),
        NavTabItem(ChijuScreen.GRADES, "Résultats", Icons.Default.Grade, Icons.Outlined.Grade, "nav_grades")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChijuTopAppBar(
                activeClass = activeClass,
                allClasses = classes,
                onSelectClass = { viewModel.selectClass(it) },
                onOpenSubscription = { viewModel.navigateTo(ChijuScreen.SUBSCRIPTION) },
                onOpenSettings = { viewModel.navigateTo(ChijuScreen.SETTINGS) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.screen) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ChijuEmerald,
                            selectedTextColor = ChijuEmerald,
                            indicatorColor = ChijuEmeraldContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ChijuScreen.HOME -> HomeScreen(
                    activeClass = activeClass,
                    students = students,
                    attendanceRecords = attendanceRecords,
                    lessons = lessons,
                    homework = homework,
                    subscription = subscription,
                    todayDate = todayDate,
                    onNavigate = { viewModel.navigateTo(it) },
                    onMarkAllPresent = { viewModel.markAllPresent() },
                    onToggleHomework = { viewModel.toggleHomework(it) }
                )

                ChijuScreen.MY_CLASS -> ClassManagementScreen(
                    activeClass = activeClass,
                    allClasses = classes,
                    students = students,
                    attendanceRecords = attendanceRecords,
                    todayDate = todayDate,
                    onMarkAllPresent = { viewModel.markAllPresent() },
                    onSetAttendance = { studentId, status -> viewModel.setStudentAttendance(studentId, status) },
                    onAddStudent = { fn, ln, gen, bd, contact, niv -> viewModel.addStudent(fn, ln, gen, bd, contact, niv) },
                    onCreateClass = { name, level, room, school -> viewModel.createClass(name, level, room, school) },
                    onDeleteStudent = { viewModel.deleteStudent(it) }
                )

                ChijuScreen.LESSON_PLANNER -> LessonPlannerScreen(
                    activeClass = activeClass,
                    lessons = lessons,
                    todayDate = todayDate,
                    onSaveLesson = { viewModel.saveLessonPlan(it) },
                    onUpdateStatus = { lesson, status, summary -> viewModel.updateLessonStatus(lesson, status, summary) },
                    onDeleteLesson = { viewModel.deleteLesson(it) },
                    onOpenAiAssistant = { viewModel.navigateTo(ChijuScreen.AI_ASSISTANT) }
                )

                ChijuScreen.AI_ASSISTANT -> AiAssistantScreen(
                    activeClass = activeClass,
                    aiResult = aiResult,
                    isGenerating = isGeneratingAi,
                    onGenerate = { type, country, level, subject, topic ->
                        viewModel.generateAi(type, country, level, subject, topic)
                    },
                    onImportToJournal = { viewModel.importAiLessonToJournal() },
                    onClearResult = { viewModel.clearAiResult() }
                )

                ChijuScreen.GRADES -> GradesScreen(
                    activeClass = activeClass,
                    assessments = assessments,
                    students = students,
                    todayDate = todayDate,
                    onAddAssessment = { subject, title, date, maxScore, coeff, period ->
                        viewModel.addAssessment(subject, title, date, maxScore, coeff, period)
                    },
                    onDeleteAssessment = { viewModel.deleteAssessment(it) },
                    getGradesForAssessment = { assessmentId -> viewModel.getGradesForAssessment(assessmentId) },
                    onSaveGrade = { assessmentId, studentId, score, isAbsent, comment ->
                        viewModel.saveStudentGrade(assessmentId, studentId, score, isAbsent, comment)
                    },
                    computeStats = { grades, maxScore -> viewModel.computeStats(grades, maxScore) }
                )

                ChijuScreen.SUBSCRIPTION -> SubscriptionScreen(
                    subscription = subscription,
                    onActivate = { phone, ref, teacher, school ->
                        viewModel.activateSubscriptionWithAirtel(phone, ref, teacher, school)
                    }
                )

                ChijuScreen.REPORTS -> ReportsScreen(
                    activeClass = activeClass,
                    students = students,
                    attendanceRecords = attendanceRecords,
                    lessons = lessons,
                    todayDate = todayDate
                )

                ChijuScreen.SETTINGS -> SettingsScreen(
                    subscription = subscription,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    }
}
