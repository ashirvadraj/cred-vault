package com.credtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.credtracker.data.model.BillStatement
import com.credtracker.ui.theme.*
import com.credtracker.util.UpiPaymentHelper
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPayModal(
    bill: BillStatement,
    billerVpa: String?,
    onDismiss: () -> Unit,
    onMarkAsPaid: (BillStatement) -> Unit
) {
    val context = LocalContext.current
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    var payFullAmount by remember { mutableStateOf(true) }

    val amountToPay = if (payFullAmount) bill.totalAmountDue else bill.minimumAmountDue

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BgCardElevated,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = BorderGlowing)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "PAY CREDIT CARD BILL",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${bill.bankName} (XX${bill.last4Digits})",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Option 1: Total Amount Due
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (payFullAmount) Color(0x2200F5D4) else BgCard)
                    .border(1.dp, if (payFullAmount) NeonCyan else BorderSubtle, RoundedCornerShape(14.dp))
                    .clickable { payFullAmount = true }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Amount Due",
                        color = if (payFullAmount) NeonCyan else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Recommended (Avoids interest)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = currencyFormatter.format(bill.totalAmountDue),
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Minimum Amount Due
            if (bill.minimumAmountDue > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (!payFullAmount) Color(0x2200F5D4) else BgCard)
                        .border(1.dp, if (!payFullAmount) NeonCyan else BorderSubtle, RoundedCornerShape(14.dp))
                        .clickable { payFullAmount = false }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Minimum Amount Due",
                            color = if (!payFullAmount) NeonCyan else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Keeps account in good standing",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = currencyFormatter.format(bill.minimumAmountDue),
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Pay via UPI App Action
            Button(
                onClick = {
                    UpiPaymentHelper.launchUpiPayment(
                        context = context,
                        billerVpa = billerVpa,
                        payeeName = bill.bankName,
                        amount = amountToPay,
                        note = "Credit Card Bill ${bill.bankName} XX${bill.last4Digits}"
                    )
                    onDismiss()
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
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PAY ${currencyFormatter.format(amountToPay)} VIA UPI",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mark as Paid Button
            OutlinedButton(
                onClick = {
                    onMarkAsPaid(bill)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = NeonEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MARK AS ALREADY PAID",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
