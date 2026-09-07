package com.credtracker.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.credtracker.CredTrackerApp
import com.credtracker.data.model.BillStatement
import com.credtracker.data.model.BillStatus
import com.credtracker.data.model.CreditCard
import com.credtracker.data.model.StatementSource
import com.credtracker.parser.ParsedBillResult
import com.credtracker.parser.SmsParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BillSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val repository = CredTrackerApp.instance.repository

            // 1. Update overdue status for past bills
            repository.updateOverdueBills()

            // 2. Scan SMS inbox for statements from the last 60 days
            scanSmsInbox(repository)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun scanSmsInbox(repository: com.credtracker.data.repository.CardRepository) {
        val uri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("_id", "address", "body", "date")
        val sixtyDaysAgo = System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000)
        val selection = "date > ?"
        val selectionArgs = arrayOf(sixtyDaysAgo.toString())

        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "date DESC"
        ) ?: return

        cursor.use {
            val addressIndex = it.getColumnIndexOrThrow("address")
            val bodyIndex = it.getColumnIndexOrThrow("body")
            val dateIndex = it.getColumnIndexOrThrow("date")

            while (it.moveToNext()) {
                val address = it.getString(addressIndex) ?: continue
                val body = it.getString(bodyIndex) ?: continue
                val date = it.getLong(dateIndex)

                val parseResult = SmsParser.parseSms(address, body, date)
                if (parseResult is ParsedBillResult) {
                    // Check if card exists
                    val existingCard = repository.getCardById(parseResult.cardId)
                    if (existingCard == null) {
                        repository.saveCard(
                            CreditCard(
                                id = parseResult.cardId,
                                bankName = parseResult.bankName,
                                cardNickname = "${parseResult.bankName} Card",
                                last4Digits = parseResult.last4Digits,
                                network = parseResult.network,
                                theme = parseResult.theme,
                                upiBillerVpa = parseResult.upiBillerVpa,
                                isAutoDetected = true
                            )
                        )
                    }

                    // Save Bill
                    val billId = "${parseResult.cardId}_${parseResult.dueDate}"
                    val bill = BillStatement(
                        id = billId,
                        cardId = parseResult.cardId,
                        bankName = parseResult.bankName,
                        last4Digits = parseResult.last4Digits,
                        totalAmountDue = parseResult.totalAmountDue,
                        minimumAmountDue = parseResult.minimumAmountDue,
                        dueDate = parseResult.dueDate,
                        statementDate = date,
                        status = if (parseResult.dueDate < System.currentTimeMillis()) BillStatus.OVERDUE else BillStatus.PENDING,
                        source = StatementSource.SMS,
                        rawSnippet = parseResult.rawSnippet
                    )
                    repository.saveBill(bill)
                }
            }
        }
    }
}
