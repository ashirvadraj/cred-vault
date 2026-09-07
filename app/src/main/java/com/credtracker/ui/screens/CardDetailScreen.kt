package com.credtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.credtracker.data.model.BillStatement
import com.credtracker.data.model.CreditCard
import com.credtracker.parser.StatementDateExtractor
import com.credtracker.ui.components.CreditCardView
import com.credtracker.ui.components.NeonBadge
import com.credtracker.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    card: CreditCard,
    bills: List<BillStatement>,
    onBackClick: () -> Unit,
    onPayClick: (BillStatement) -> Unit,
    onDeleteCard: (CreditCard) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val pendingBill = bills.find { it.status != com.credtracker.data.model.BillStatus.PAID }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card.bankName, color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onDeleteCard(card) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Card",
                            tint = CrimsonAlert
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgObsidian)
            )
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
            item {
                CreditCardView(
                    card = card,
                    pendingBill = pendingBill,
                    onCardClick = {},
                    onPayClick = onPayClick
                )
            }

            // Billing Cycle Info Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(BgCard)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "BILLING DETAILS",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Credit Limit", color = TextMuted, fontSize = 12.sp)
                                Text(
                                    currencyFormatter.format(card.totalLimit),
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text("Statement Cycle", color = TextMuted, fontSize = 12.sp)
                                Text(
                                    "${card.statementDayOfMonth}th of each month",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Statement History Section
            item {
                Text(
                    text = "STATEMENT HISTORY (${bills.size})",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (bills.isEmpty()) {
                item {
                    Text(
                        text = "No statements recorded for this card yet.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                items(bills, key = { it.id }) { bill ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BgCard)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = StatementDateExtractor.formatDate(bill.statementDate),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currencyFormatter.format(bill.totalAmountDue),
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Due: ${StatementDateExtractor.formatDate(bill.dueDate)}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            NeonBadge(
                                status = bill.status,
                                dueTimestamp = bill.dueDate
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
