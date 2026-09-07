package com.credtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.credtracker.data.model.BillStatus
import com.credtracker.parser.StatementDateExtractor
import com.credtracker.ui.theme.*

@Composable
fun NeonBadge(
    status: BillStatus,
    dueTimestamp: Long,
    modifier: Modifier = Modifier
) {
    val daysRemaining = StatementDateExtractor.getDaysRemaining(dueTimestamp)

    val (bgColor, borderColor, textColor, text, icon) = when {
        status == BillStatus.PAID -> {
            BadgeConfig(
                bgColor = Color(0x2200E676),
                borderColor = NeonEmerald,
                textColor = NeonEmerald,
                text = "PAID",
                icon = Icons.Default.CheckCircle
            )
        }
        status == BillStatus.OVERDUE || daysRemaining < 0 -> {
            BadgeConfig(
                bgColor = Color(0x33FF0055),
                borderColor = CrimsonAlert,
                textColor = CrimsonAlert,
                text = "OVERDUE",
                icon = Icons.Default.ErrorOutline
            )
        }
        daysRemaining == 0L -> {
            BadgeConfig(
                bgColor = Color(0x33FF9100),
                borderColor = AmberWarning,
                textColor = AmberWarning,
                text = "DUE TODAY",
                icon = Icons.Default.Schedule
            )
        }
        daysRemaining == 1L -> {
            BadgeConfig(
                bgColor = Color(0x33FF9100),
                borderColor = AmberWarning,
                textColor = AmberWarning,
                text = "DUE TOMORROW",
                icon = Icons.Default.Schedule
            )
        }
        else -> {
            BadgeConfig(
                bgColor = Color(0x2200F5D4),
                borderColor = NeonCyan,
                textColor = NeonCyan,
                text = "DUE IN $daysRemaining DAYS",
                icon = Icons.Default.Schedule
            )
        }
    }

    Box(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(50))
            .border(1.dp, borderColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

private data class BadgeConfig(
    val bgColor: Color,
    val borderColor: Color,
    val textColor: Color,
    val text: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
