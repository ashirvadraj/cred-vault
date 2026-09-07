package com.credtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.credtracker.data.model.CardNetwork
import com.credtracker.data.model.CardTheme
import com.credtracker.data.model.CreditCard
import com.credtracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    onBackClick: () -> Unit,
    onSaveCard: (CreditCard) -> Unit
) {
    var bankName by remember { mutableStateOf("HDFC Bank") }
    var cardNickname by remember { mutableStateOf("") }
    var last4Digits by remember { mutableStateOf("") }
    var totalLimit by remember { mutableStateOf("") }
    var statementDay by remember { mutableStateOf("15") }
    var selectedNetwork by remember { mutableStateOf(CardNetwork.VISA) }
    var selectedTheme by remember { mutableStateOf(CardTheme.NEON_CYAN) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Credit Card", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgObsidian)
            )
        },
        containerColor = BgObsidian
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CARD DETAILS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            // Bank Name Input
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name (e.g. HDFC, SBI, ICICI)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Card Nickname
            OutlinedTextField(
                value = cardNickname,
                onValueChange = { cardNickname = it },
                label = { Text("Card Nickname (e.g. Regalia Gold, Infinia)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Last 4 Digits
            OutlinedTextField(
                value = last4Digits,
                onValueChange = { if (it.length <= 4) last4Digits = it },
                label = { Text("Last 4 Digits (e.g. 4821)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Total Limit
            OutlinedTextField(
                value = totalLimit,
                onValueChange = { totalLimit = it },
                label = { Text("Total Credit Limit (₹)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Statement Day
            OutlinedTextField(
                value = statementDay,
                onValueChange = { statementDay = it },
                label = { Text("Statement Generation Day (1 - 31)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Card Theme Selection
            Text(
                text = "CARD THEME ACCENT",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    CardTheme.NEON_CYAN to NeonCyan,
                    CardTheme.ELECTRIC_PURPLE to ElectricPurple,
                    CardTheme.OBSIDIAN_GOLD to CyberGold,
                    CardTheme.EMERALD_GLOW to NeonEmerald,
                    CardTheme.CRIMSON_DARK to CrimsonAlert
                ).forEach { (theme, color) ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (selectedTheme == theme) 3.dp else 1.dp,
                                color = if (selectedTheme == theme) TextPrimary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedTheme = theme }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val last4 = if (last4Digits.isNotBlank()) last4Digits else "0000"
                    val cardId = "${bankName.replace(" ", "_").uppercase()}_$last4"
                    val newCard = CreditCard(
                        id = cardId,
                        bankName = bankName.trim(),
                        cardNickname = if (cardNickname.isNotBlank()) cardNickname.trim() else "$bankName Card",
                        last4Digits = last4,
                        network = selectedNetwork,
                        totalLimit = totalLimit.toDoubleOrNull() ?: 100000.0,
                        availableLimit = totalLimit.toDoubleOrNull() ?: 100000.0,
                        statementDayOfMonth = statementDay.toIntOrNull() ?: 15,
                        theme = selectedTheme,
                        isAutoDetected = false
                    )
                    onSaveCard(newCard)
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = BgObsidian
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "SAVE CREDIT CARD",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
