package com.credtracker.parser

import com.credtracker.data.model.*
import java.util.UUID

data class ParsedBillResult(
    val cardId: String,
    val bankName: String,
    val last4Digits: String,
    val totalAmountDue: Double,
    val minimumAmountDue: Double,
    val dueDate: Long,
    val network: CardNetwork,
    val theme: CardTheme,
    val upiBillerVpa: String,
    val rawSnippet: String
)

data class ParsedPaymentResult(
    val bankName: String,
    val last4Digits: String?,
    val paidAmount: Double,
    val timestamp: Long
)

object SmsParser {

    fun parseSms(sender: String, messageBody: String, timestamp: Long = System.currentTimeMillis()): Any? {
        val upperSender = sender.uppercase()
        val upperBody = messageBody.uppercase()

        // 1. Check if this is from a known bank
        val matchedBank = BankRegexPatterns.SUPPORTED_BANKS.find { bank ->
            bank.senderPrefixes.any { prefix -> upperSender.contains(prefix) } ||
                    upperBody.contains(bank.bankName.uppercase())
        } ?: inferBankFromSender(sender)

        // 2. Check for Payment Confirmation first
        val paymentResult = parsePaymentConfirmation(messageBody, matchedBank?.bankName ?: "Unknown Bank", timestamp)
        if (paymentResult != null) {
            return paymentResult
        }

        // 3. Extract Card Last 4 Digits
        val last4 = extractLast4Digits(messageBody) ?: "0000"

        // 4. Extract Total Amount Due
        val totalDue = extractTotalDue(messageBody) ?: return null

        // 5. Extract Minimum Amount Due
        val minDue = extractMinDue(messageBody) ?: (totalDue * 0.05) // Default 5% MAD

        // 6. Extract Due Date
        val dueDate = extractDueDate(messageBody) ?: return null

        val bankName = matchedBank?.bankName ?: "Credit Card"
        val network = matchedBank?.defaultNetwork ?: CardNetwork.VISA
        val theme = matchedBank?.defaultTheme ?: CardTheme.OBSIDIAN_GOLD
        val upiVpa = matchedBank?.defaultUpiBillerVpa ?: "upi"
        val cardId = "${bankName.replace(" ", "_").uppercase()}_$last4"

        return ParsedBillResult(
            cardId = cardId,
            bankName = bankName,
            last4Digits = last4,
            totalAmountDue = totalDue,
            minimumAmountDue = minDue,
            dueDate = dueDate,
            network = network,
            theme = theme,
            upiBillerVpa = upiVpa,
            rawSnippet = messageBody
        )
    }

    private fun inferBankFromSender(sender: String): BankMetadata? {
        val clean = sender.replace(Regex("""^[A-Za-z]{2}-"""), "").uppercase()
        return BankRegexPatterns.SUPPORTED_BANKS.find { bank ->
            bank.senderPrefixes.any { clean.contains(it) }
        }
    }

    fun extractTotalDue(body: String): Double? {
        for (pattern in BankRegexPatterns.TOTAL_DUE_PATTERNS) {
            val match = pattern.find(body)
            if (match != null && match.groupValues.size > 1) {
                val rawAmt = match.groupValues[1].replace(",", "").trim()
                val parsed = rawAmt.toDoubleOrNull()
                if (parsed != null && parsed > 0) return parsed
            }
        }
        return null
    }

    fun extractMinDue(body: String): Double? {
        for (pattern in BankRegexPatterns.MIN_DUE_PATTERNS) {
            val match = pattern.find(body)
            if (match != null && match.groupValues.size > 1) {
                val rawAmt = match.groupValues[1].replace(",", "").trim()
                val parsed = rawAmt.toDoubleOrNull()
                if (parsed != null && parsed > 0) return parsed
            }
        }
        return null
    }

    fun extractDueDate(body: String): Long? {
        for (pattern in BankRegexPatterns.DUE_DATE_PATTERNS) {
            val match = pattern.find(body)
            if (match != null && match.groupValues.size > 1) {
                val dateStr = match.groupValues[1]
                val epoch = StatementDateExtractor.parseDate(dateStr)
                if (epoch != null) return epoch
            }
        }
        return null
    }

    fun extractLast4Digits(body: String): String? {
        for (pattern in BankRegexPatterns.CARD_NUMBER_PATTERNS) {
            val match = pattern.find(body)
            if (match != null && match.groupValues.size > 1) {
                val digits = match.groupValues[1]
                if (digits.length == 4) return digits
            }
        }
        return null
    }

    fun parsePaymentConfirmation(body: String, bankName: String, timestamp: Long): ParsedPaymentResult? {
        for (pattern in BankRegexPatterns.PAYMENT_RECEIVED_PATTERNS) {
            val match = pattern.find(body)
            if (match != null && match.groupValues.size > 1) {
                val rawAmt = match.groupValues[1].replace(",", "").trim()
                val amount = rawAmt.toDoubleOrNull() ?: continue
                val last4 = if (match.groupValues.size > 2) match.groupValues[2].ifEmpty { null } else null
                return ParsedPaymentResult(
                    bankName = bankName,
                    last4Digits = last4,
                    paidAmount = amount,
                    timestamp = timestamp
                )
            }
        }
        return null
    }
}
