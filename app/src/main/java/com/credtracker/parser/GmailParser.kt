package com.credtracker.parser

import android.content.Context
import com.credtracker.data.model.CardNetwork
import com.credtracker.data.model.CardTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GmailStatementResult(
    val sender: String,
    val subject: String,
    val bodySnippet: String,
    val receivedDate: Long
)

object GmailParser {

    val GMAIL_QUERY_FILTER = """
        (subject:("credit card statement" OR "e-statement" OR "eStatement" OR "card statement" OR "bill alert" OR "total amount due") OR from:(hdfcbank.net OR sbicard.com OR icicibank.com OR axisbank.com OR americanexpress.com OR kotak.com OR onecard.co.in OR rblbank.com))
    """.trimIndent()

    suspend fun parseEmailContent(email: GmailStatementResult): ParsedBillResult? = withContext(Dispatchers.Default) {
        val combinedText = "${email.subject} \n ${email.bodySnippet}"
        val parsed = SmsParser.parseSms(email.sender, combinedText, email.receivedDate)
        if (parsed is ParsedBillResult) {
            return@withContext parsed
        }
        null
    }
}
