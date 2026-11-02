package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.data.model.SubscriptionInfo
import com.example.ui.components.SectionHeader
import com.example.ui.theme.ChijuAirtelRed
import com.example.ui.theme.ChijuEmerald
import com.example.ui.theme.ChijuEmeraldContainer
import com.example.ui.theme.ChijuGold
import com.example.ui.theme.ChijuOnEmeraldContainer
import com.example.ui.theme.StatusPresent

@Composable
fun SubscriptionScreen(
    subscription: SubscriptionInfo?,
    onActivate: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showActivationForm by remember { mutableStateOf(false) }
    var transactionRef by remember { mutableStateOf("") }
    var payerPhone by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf(subscription?.teacherName ?: "") }
    var schoolName by remember { mutableStateOf(subscription?.schoolName ?: "") }

    val officialNumber = "+241 77793074"
    val priceText = "10 000 FCFA / an"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Subscription Status Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ChijuAirtelRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = ChijuAirtelRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Mon Abonnement CHIJU",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = subscription?.planName ?: "Licence Enseignant Pro",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ChijuEmeraldContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = ChijuEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = subscription?.status ?: "ACTIF",
                                    fontWeight = FontWeight.Bold,
                                    color = ChijuEmerald,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Days countdown banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Durée restante",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${subscription?.daysRemaining ?: 312} jours",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = ChijuEmerald
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Date d'expiration",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = subscription?.expirationDate ?: "2027-09-10",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showActivationForm = !showActivationForm },
                            colors = ButtonDefaults.buttonColors(containerColor = ChijuAirtelRed),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_renew_subscription")
                        ) {
                            Text("Renouveler / Valider Paiement", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Official Airtel Money Instructions Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(ChijuAirtelRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Moyen de paiement officiel Airtel Money",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ChijuAirtelRed
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Airtel Number Callout Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF2F2),
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
                                    text = "Numéro officiel CHIJU :",
                                    fontSize = 11.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = officialNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = ChijuAirtelRed
                                )
                                Text(
                                    text = "Montant de l'activation : $priceText",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF7F1D1D)
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Numéro Airtel CHIJU", officialNumber)
                                        clipboard.setPrimaryClip(clip)
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copier le numéro", tint = ChijuAirtelRed)
                                }
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:$officialNumber")
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Appeler", tint = ChijuAirtelRed)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Procédure d'activation en 3 étapes :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Effectuez un transfert Airtel Money de 10 000 FCFA vers le numéro officiel +241 77793074.\n" +
                                "2. Renseignez la référence de la transaction (SMS Airtel) dans le formulaire ci-dessous.\n" +
                                "3. Votre compte est instantanément activé pour 12 mois complets.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Security Guarantee
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ChijuEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CHIJU ne vous demandera jamais votre code secret Airtel Money.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ChijuEmerald
                        )
                    }
                }
            }
        }

        // Activation Form
        if (showActivationForm) {
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
                            text = "Formulaire de validation de l'abonnement",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        OutlinedTextField(
                            value = transactionRef,
                            onValueChange = { transactionRef = it },
                            label = { Text("Référence de la transaction Airtel (ex: TXN-893201)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_txn_ref")
                        )

                        OutlinedTextField(
                            value = payerPhone,
                            onValueChange = { payerPhone = it },
                            label = { Text("Numéro Airtel ayant effectué le paiement") },
                            placeholder = { Text("Ex: +241 77 12 34 56") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = teacherName,
                            onValueChange = { teacherName = it },
                            label = { Text("Nom de l'enseignant titulaire") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = schoolName,
                            onValueChange = { schoolName = it },
                            label = { Text("Nom de l'école / établissement") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (transactionRef.isNotBlank()) {
                                    onActivate(payerPhone, transactionRef, teacherName, schoolName)
                                    showActivationForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ChijuEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_confirm_activation")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Valider mon activation (12 mois)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Notifications & Reminders Info
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = ChijuGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rappels d'expiration automatiques",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Des alertes vous seront présentées discrètement 30 jours, 15 jours, 7 jours et le jour même de l'échéance pour garantir la continuité de vos données pédagogiques sans interruption.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
