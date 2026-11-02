package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.ai.PedagogicalAiEngine
import com.example.data.model.SchoolClass
import com.example.ui.components.SectionHeader
import com.example.ui.theme.ChijuEmerald
import com.example.ui.theme.ChijuEmeraldContainer
import com.example.ui.theme.ChijuGold
import com.example.ui.theme.ChijuOnEmeraldContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    activeClass: SchoolClass?,
    aiResult: PedagogicalAiEngine.AiGenerationResult?,
    isGenerating: Boolean,
    onGenerate: (PedagogicalAiEngine.AssistantType, String, String, String, String) -> Unit,
    onImportToJournal: () -> Unit,
    onClearResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(PedagogicalAiEngine.AssistantType.LESSON_PREP) }
    var selectedCountry by remember { mutableStateOf("Gabon") }
    var selectedLevel by remember { mutableStateOf(activeClass?.gradeLevel ?: "CM1") }
    var selectedSubject by remember { mutableStateOf("Mathématiques") }
    var topicPrompt by remember {
        mutableStateOf("Les fractions simples : notion de demi, tiers et quart avec matériel concret (oranges et bâtonnets)")
    }

    var countryMenuExpanded by remember { mutableStateOf(false) }
    var subjectMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ChijuGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ChijuGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Assistant IA CHIJU",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ChijuEmeraldContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Programmes Francophones",
                                        color = ChijuOnEmeraldContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Générateur intelligent adapté aux réalités du terrain en Afrique",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Assistant Modes Selector Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(PedagogicalAiEngine.AssistantType.values()) { type ->
                            val isSelected = selectedType == type
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) ChijuEmerald else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { selectedType = type }
                                    .testTag("ai_type_${type.name.lowercase()}")
                            ) {
                                Text(
                                    text = type.title,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Generator Form
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Paramètres de la fiche : ${selectedType.title}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // Row: Country & Level
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Country Selector
                        ExposedDropdownMenuBox(
                            expanded = countryMenuExpanded,
                            onExpandedChange = { countryMenuExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedCountry,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Pays / Programme") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryMenuExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = countryMenuExpanded,
                                onDismissRequest = { countryMenuExpanded = false }
                            ) {
                                PedagogicalAiEngine.supportedCountries.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text(country) },
                                        onClick = {
                                            selectedCountry = country
                                            countryMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Level Chips
                        ExposedDropdownMenuBox(
                            expanded = subjectMenuExpanded,
                            onExpandedChange = { subjectMenuExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedSubject,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Discipline") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectMenuExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = subjectMenuExpanded,
                                onDismissRequest = { subjectMenuExpanded = false }
                            ) {
                                PedagogicalAiEngine.supportedSubjects.forEach { sub ->
                                    DropdownMenuItem(
                                        text = { Text(sub) },
                                        onClick = {
                                            selectedSubject = sub
                                            subjectMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Level selector chips (CP1 to CM2)
                    Column {
                        Text(
                            text = "Niveau scolaire : $selectedLevel",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PedagogicalAiEngine.supportedLevels.forEach { lvl ->
                                val isLvlSelected = selectedLevel == lvl
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isLvlSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedLevel = lvl }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                        Text(
                                            text = lvl,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLvlSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Subject Prompt
                    OutlinedTextField(
                        value = topicPrompt,
                        onValueChange = { topicPrompt = it },
                        label = { Text("Thème, compétence ou consigne détaillée") },
                        placeholder = { Text("Ex: Les fractions, la dictée négociée, le cycle de l'eau...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_prompt_input"),
                        minLines = 2
                    )

                    // Generation Button
                    Button(
                        onClick = {
                            onGenerate(selectedType, selectedCountry, selectedLevel, selectedSubject, topicPrompt)
                        },
                        enabled = !isGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_generate_ai")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Génération en cours...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Générer la proposition pédagogique", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // AI Generated Output View
        aiResult?.let { result ->
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
                                    text = result.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${result.gradeLevel} • ${result.subject}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("CHIJU Fiche", result.content)
                                    clipboard.setPrimaryClip(clip)
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copier la fiche", tint = ChijuEmerald)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fiche Content Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = result.content,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Import to Cahier Journal / Copy / Clear
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (result.lessonPlanTemplate != null) {
                                Button(
                                    onClick = onImportToJournal,
                                    colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_import_to_journal")
                                ) {
                                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Enregistrer au Journal", fontSize = 12.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = onClearResult,
                                modifier = Modifier.weight(0.5f)
                            ) {
                                Text("Fermer", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
