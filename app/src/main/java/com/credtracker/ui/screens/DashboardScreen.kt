package com.credtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.credtracker.data.model.BillStatement
import com.credtracker.data.model.CreditCard
import com.credtracker.ui.components.*
import com.credtracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    cards: List<CreditCard>,
    pendingBills: List<BillStatement>,
    totalOutstanding: Double,
    onCardClick: (CreditCard) -> Unit,
    onPayClick: (BillStatement, CreditCard?) -> Unit,
    onAddCardClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPermissionsClick: () -> Unit,
    onSyncClick: () -> Unit
) {
    val totalLimit = cards.sumOf { it.totalLimit }
    val pendingBillMap = remember(pendingBills) {
        pendingBills.associateBy { it.cardId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CRED VAULT",
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Personal Bill & Card Tracker",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onPermissionsClick) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Permissions",
                            tint = NeonCyan
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgObsidian
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCardClick,
                containerColor = NeonCyan,
                contentColor = BgObsidian,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Card"
                )
            }
        },
        containerColor = BgObsidian
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Summary Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                BillSummaryCard(
                    totalOutstanding = totalOutstanding,
                    pendingCardCount = pendingBills.size,
                    onSyncClick = onSyncClick,
                    onPayAllClick = {
                        if (pendingBills.isNotEmpty()) {
                            val first = pendingBills.first()
                            val card = cards.find { it.id == first.cardId }
                            onPayClick(first, card)
                        }
                    }
                )
            }

            // Credit Utilization Widget
            if (totalLimit > 0) {
                item {
                    CreditUtilizationBar(
                        totalUsed = totalOutstanding,
                        totalLimit = totalLimit
                    )
                }
            }

            // Section Header: My Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR CREDIT CARDS (${cards.size})",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (cards.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No cards added yet",
                                color = TextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Grant SMS permission to auto-sync or tap + to add manually",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(cards, key = { it.id }) { card ->
                    val pendingBill = pendingBillMap[card.id]
                    CreditCardView(
                        card = card,
                        pendingBill = pendingBill,
                        onCardClick = { onCardClick(card) },
                        onPayClick = { bill -> onPayClick(bill, card) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
