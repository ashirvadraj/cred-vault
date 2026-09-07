package com.credtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.credtracker.data.model.BillStatement
import com.credtracker.data.model.BillStatus
import com.credtracker.data.model.CardTheme
import com.credtracker.data.model.CreditCard
import com.credtracker.parser.StatementDateExtractor
import com.credtracker.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CreditCardView(
    card: CreditCard,
    pendingBill: BillStatement?,
    onCardClick: () -> Unit,
    onPayClick: (BillStatement) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    val gradientBrush = when (card.theme) {
        CardTheme.OBSIDIAN_GOLD -> CredGoldGradient
        CardTheme.NEON_CYAN -> CredCyanGradient
        CardTheme.ELECTRIC_PURPLE -> CredPurpleGradient
        CardTheme.EMERALD_GLOW -> CredEmeraldGradient
        CardTheme.CRIMSON_DARK -> CredCrimsonGradient
    }

    val glowBorderColor = when (card.theme) {
        CardTheme.OBSIDIAN_GOLD -> CyberGold.copy(alpha = 0.4f)
        CardTheme.NEON_CYAN -> NeonCyan.copy(alpha = 0.4f)
        CardTheme.ELECTRIC_PURPLE -> ElectricPurple.copy(alpha = 0.4f)
        CardTheme.EMERALD_GLOW -> NeonEmerald.copy(alpha = 0.4f)
        CardTheme.CRIMSON_DARK -> CrimsonAlert.copy(alpha = 0.4f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(215.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp), spotColor = glowBorderColor)
            .clip(RoundedCornerShape(20.dp))
            .border(1.2.dp, glowBorderColor, RoundedCornerShape(20.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Bank Name + Chip + Network Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = card.bankName.uppercase(),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = card.cardNickname,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Card Network / Chip Icon
                    Box(
                        modifier = Modifier
                            .background(Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                            .border(0.8.dp, Color(0x66FFFFFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = card.network.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Middle Row: Chip graphic & Masked Card Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Metallic Chip Emulation
                    Box(
                        modifier = Modifier
                            .size(34.dp, 26.dp)
                            .background(
                                Brush.linearGradient(listOf(CyberGold, Color(0xFF8C6414))),
                                RoundedCornerShape(4.dp)
                            )
                            .border(0.5.dp, Color(0xAAFFFFFF), RoundedCornerShape(4.dp))
                    )

                    Text(
                        text = "••••  ••••  ••••  ${card.last4Digits}",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )
                }

                // Bottom Row: Due Amount Info & Pay Action / Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (pendingBill != null && pendingBill.status != BillStatus.PAID) {
                        Column {
                            Text(
                                text = "TOTAL DUE",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = currencyFormatter.format(pendingBill.totalAmountDue),
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Due on ${StatementDateExtractor.formatDate(pendingBill.dueDate)}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { onPayClick(pendingBill) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = BgObsidian
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PAY NOW",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else {
                        Column {
                            Text(
                                text = "BILL STATUS",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "No Dues Pending",
                                color = NeonEmerald,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        NeonBadge(
                            status = BillStatus.PAID,
                            dueTimestamp = System.currentTimeMillis()
                        )
                    }
                }
            }
        }
    }
}
