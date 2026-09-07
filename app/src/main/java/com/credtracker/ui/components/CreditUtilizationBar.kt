package com.credtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.credtracker.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CreditUtilizationBar(
    totalUsed: Double,
    totalLimit: Double,
    modifier: Modifier = Modifier
) {
    if (totalLimit <= 0) return

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val utilizationRatio = (totalUsed / totalLimit).coerceIn(0.0, 1.0).toFloat()
    val utilizationPercent = (utilizationRatio * 100).toInt()

    val progressColor = when {
        utilizationPercent < 30 -> NeonEmerald
        utilizationPercent < 50 -> CyberGold
        else -> CrimsonAlert
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CREDIT UTILIZATION",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "$utilizationPercent% (Healthy < 30%)",
                    color = progressColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { utilizationRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = Color(0x33FFFFFF),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Used: ${currencyFormatter.format(totalUsed)}",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Limit: ${currencyFormatter.format(totalLimit)}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
