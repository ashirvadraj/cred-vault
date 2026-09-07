package com.credtracker.parser

import com.credtracker.data.model.CardNetwork
import com.credtracker.data.model.CardTheme

data class BankMetadata(
    val bankName: String,
    val senderPrefixes: List<String>,
    val defaultUpiBillerVpa: String,
    val defaultNetwork: CardNetwork,
    val defaultTheme: CardTheme
)

object BankRegexPatterns {

    val SUPPORTED_BANKS = listOf(
        BankMetadata(
            bankName = "HDFC Bank",
            senderPrefixes = listOf("HDFCBK", "HDFC", "HDFCCC"),
            defaultUpiBillerVpa = "hdfcbank@upi",
            defaultNetwork = CardNetwork.VISA,
            defaultTheme = CardTheme.NEON_CYAN
        ),
        BankMetadata(
            bankName = "SBI Card",
            senderPrefixes = listOf("SBICRD", "SBINB", "SBICARD"),
            defaultUpiBillerVpa = "sbicard@upi",
            defaultNetwork = CardNetwork.VISA,
            defaultTheme = CardTheme.ELECTRIC_PURPLE
        ),
        BankMetadata(
            bankName = "ICICI Bank",
            senderPrefixes = listOf("ICICIB", "ICICIC", "ICICI"),
            defaultUpiBillerVpa = "icicibank@upi",
            defaultNetwork = CardNetwork.MASTERCARD,
            defaultTheme = CardTheme.CRIMSON_DARK
        ),
        BankMetadata(
            bankName = "Axis Bank",
            senderPrefixes = listOf("AXISBK", "AXISCR", "AXIS"),
            defaultUpiBillerVpa = "axisbank@upi",
            defaultNetwork = CardNetwork.MASTERCARD,
            defaultTheme = CardTheme.CRIMSON_DARK
        ),
        BankMetadata(
            bankName = "Kotak Bank",
            senderPrefixes = listOf("KOTAKB", "KOTAKC", "KOTAK"),
            defaultUpiBillerVpa = "kotakbank@upi",
            defaultNetwork = CardNetwork.VISA,
            defaultTheme = CardTheme.CRIMSON_DARK
        ),
        BankMetadata(
            bankName = "American Express",
            senderPrefixes = listOf("AMEXIN", "AMEX", "AMERICANEXPRESS"),
            defaultUpiBillerVpa = "amex@upi",
            defaultNetwork = CardNetwork.AMEX,
            defaultTheme = CardTheme.OBSIDIAN_GOLD
        ),
        BankMetadata(
            bankName = "OneCard",
            senderPrefixes = listOf("ONECRD", "ONECARD", "FPLABS"),
            defaultUpiBillerVpa = "onecard@upi",
            defaultNetwork = CardNetwork.VISA,
            defaultTheme = CardTheme.OBSIDIAN_GOLD
        ),
        BankMetadata(
            bankName = "RBL Bank",
            senderPrefixes = listOf("RBLBNK", "RBLCRD", "RBL"),
            defaultUpiBillerVpa = "rblbank@upi",
            defaultNetwork = CardNetwork.MASTERCARD,
            defaultTheme = CardTheme.EMERALD_GLOW
        ),
        BankMetadata(
            bankName = "IndusInd Bank",
            senderPrefixes = listOf("INDUSB", "INDUSCRD", "INDUS"),
            defaultUpiBillerVpa = "indusind@upi",
            defaultNetwork = CardNetwork.VISA,
            defaultTheme = CardTheme.OBSIDIAN_GOLD
        ),
        BankMetadata(
            bankName = "Standard Chartered",
            senderPrefixes = listOf("SCBANK", "STANCHAR"),
            defaultUpiBillerVpa = "scbank@upi",
            defaultNetwork = CardNetwork.VISA,
            defaultTheme = CardTheme.EMERALD_GLOW
        ),
        BankMetadata(
            bankName = "HSBC Bank",
            senderPrefixes = listOf("HSBCIN", "HSBC"),
            defaultUpiBillerVpa = "hsbc@upi",
            defaultNetwork = CardNetwork.VISA,
            defaultTheme = CardTheme.CRIMSON_DARK
        )
    )

    // Regex for Total Amount Due
    val TOTAL_DUE_PATTERNS = listOf(
        Regex("""(?i)(?:Total\s+Amount\s+Due|Total\s+Amt\s+Due|Total\s+Due|TAD|Bill\s+Amt|Due\s+Amt|Total\s+Due\s+is)[\s:]*(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)"""),
        Regex("""(?i)(?:statement\s+generated\s+for|bill\s+of)[\s:]*(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)"""),
        Regex("""(?i)(?:Rs\.?|INR|₹)\s*([0-9,]+(?:\.[0-9]{1,2})?)\s+(?:is\s+due|is\s+the\s+total\s+due|due\s+on)"""),
        Regex("""(?i)due\s+amount\s+is\s+(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)""")
    )

    // Regex for Minimum Amount Due
    val MIN_DUE_PATTERNS = listOf(
        Regex("""(?i)(?:Min(?:imum)?\s+(?:Amount\s+Due|Amt\s+Due|Due)|MAD)[\s:]*(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)"""),
        Regex("""(?i)(?:min\s+payment|minimum\s+due\s+of)[\s:]*(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)""")
    )

    // Regex for Due Date
    val DUE_DATE_PATTERNS = listOf(
        Regex("""(?i)(?:Payment\s+Due\s+Date|Due\s+Date|Due\s+by|Pay\s+by|Due\s+on|by)[\s:]*([0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4}|[0-9]{1,2}(?:st|nd|rd|th)?\s+[A-Za-z]{3,9}(?:\s+[0-9]{2,4})?|[A-Za-z]{3,9}\s+[0-9]{1,2}(?:st|nd|rd|th)?,?\s*[0-9]{2,4})"""),
        Regex("""(?i)due\s+on\s+([0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4}|[0-9]{1,2}-[A-Za-z]{3}-[0-9]{2,4})"""),
        Regex("""(?i)on\s+or\s+before\s+([0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4}|[0-9]{1,2}\s+[A-Za-z]{3,9}\s+[0-9]{2,4})""")
    )

    // Regex for Card Ending Digits
    val CARD_NUMBER_PATTERNS = listOf(
        Regex("""(?i)(?:card\s+(?:ending|no\.?|number)|card\s+xx|a/c\s+ending\s+with|ending\s+with|ending\s+in|ending|xx)[\s:]*([0-9]{4})"""),
        Regex("""(?i)(?:credit\s+card|card)[\s\w]*\b([0-9]{4})\b"""),
        Regex("""[xX*]{4,12}([0-9]{4})""")
    )

    // Regex for Payment Confirmation Repayment SMS
    val PAYMENT_RECEIVED_PATTERNS = listOf(
        Regex("""(?i)(?:Thank\s+you\s+for\s+(?:the\s+)?payment\s+of|Payment\s+of|Received\s+(?:payment\s+of)?)\s*(?:Rs\.?|INR|₹)?\s*([0-9,]+(?:\.[0-9]{1,2})?)\s+(?:towards|for|on)\s+(?:your\s+)?(?:[a-zA-Z\s]+)?card(?:\s+ending)?[\s:]*([0-9]{4})?"""),
        Regex("""(?i)(?:Rs\.?|INR|₹)\s*([0-9,]+(?:\.[0-9]{1,2})?)\s+(?:has\s+been\s+credited|received\s+towards\s+your\s+credit\s+card)""")
    )
}
