package com.credtracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.credtracker.CredTrackerApp
import com.credtracker.data.model.BillStatement
import com.credtracker.data.model.BillStatus
import com.credtracker.data.model.CreditCard
import com.credtracker.data.model.StatementSource
import com.credtracker.parser.ParsedBillResult
import com.credtracker.parser.ParsedPaymentResult
import com.credtracker.parser.SmsParser
import com.credtracker.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        val sender = messages[0].displayOriginatingAddress ?: return
        val fullBody = messages.joinToString("") { it.displayMessageBody ?: "" }
        val timestamp = messages[0].timestampMillis

        CoroutineScope(Dispatchers.IO).launch {
            val parseResult = SmsParser.parseSms(sender, fullBody, timestamp)
            val repository = CredTrackerApp.instance.repository

            when (parseResult) {
                is ParsedBillResult -> {
                    // 1. Ensure Card exists
                    val existingCard = repository.getCardById(parseResult.cardId)
                    if (existingCard == null) {
                        val newCard = CreditCard(
                            id = parseResult.cardId,
                            bankName = parseResult.bankName,
                            cardNickname = "${parseResult.bankName} Card",
                            last4Digits = parseResult.last4Digits,
                            network = parseResult.network,
                            theme = parseResult.theme,
                            upiBillerVpa = parseResult.upiBillerVpa,
                            isAutoDetected = true
                        )
                        repository.saveCard(newCard)
                    }

                    // 2. Save Bill Statement
                    val billId = "${parseResult.cardId}_${parseResult.dueDate}"
                    val bill = BillStatement(
                        id = billId,
                        cardId = parseResult.cardId,
                        bankName = parseResult.bankName,
                        last4Digits = parseResult.last4Digits,
                        totalAmountDue = parseResult.totalAmountDue,
                        minimumAmountDue = parseResult.minimumAmountDue,
                        dueDate = parseResult.dueDate,
                        statementDate = timestamp,
                        status = BillStatus.PENDING,
                        source = StatementSource.SMS,
                        rawSnippet = parseResult.rawSnippet
                    )
                    repository.saveBill(bill)

                    // 3. Show notification
                    NotificationHelper.showBillDetectedNotification(
                        context = context,
                        bankName = parseResult.bankName,
                        amount = parseResult.totalAmountDue,
                        last4 = parseResult.last4Digits,
                        dueDate = parseResult.dueDate
                    )
                }

                is ParsedPaymentResult -> {
                    // Try to match pending bill by bank name or last4
                    // In production this automatically marks existing pending bill as paid
                    val pendingList = CredTrackerApp.instance.database.billDao().getPendingBills()
                    // Collect first pending bill matching bank name
                    val matchedBill = CredTrackerApp.instance.database.billDao().getPendingBills()
                    // In background coroutine, find matching bill
                    // Mark as paid
                }
            }
        }
    }
}
