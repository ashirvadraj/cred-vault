package com.credtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CardNetwork {
    VISA,
    MASTERCARD,
    RUPAY,
    AMEX,
    DINERS,
    OTHER
}

enum class CardTheme {
    OBSIDIAN_GOLD,
    NEON_CYAN,
    ELECTRIC_PURPLE,
    EMERALD_GLOW,
    CRIMSON_DARK
}

@Entity(tableName = "credit_cards")
data class CreditCard(
    @PrimaryKey
    val id: String, // Typically "BANK_LAST4" e.g. "HDFC_4821"
    val bankName: String,
    val cardNickname: String,
    val last4Digits: String,
    val network: CardNetwork = CardNetwork.VISA,
    val totalLimit: Double = 0.0,
    val availableLimit: Double = 0.0,
    val statementDayOfMonth: Int = 1,
    val dueDayOfMonth: Int = 20,
    val theme: CardTheme = CardTheme.OBSIDIAN_GOLD,
    val upiBillerVpa: String? = null,
    val isAutoDetected: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
