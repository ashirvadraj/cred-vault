package com.credtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    DEBIT,
    REPAYMENT,
    REFUND
}

@Entity(
    tableName = "card_transactions",
    foreignKeys = [
        ForeignKey(
            entity = CreditCard::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cardId"]),
        Index(value = ["timestamp"])
    ]
)
data class Transaction(
    @PrimaryKey
    val id: String,
    val cardId: String,
    val amount: Double,
    val merchant: String,
    val type: TransactionType = TransactionType.DEBIT,
    val timestamp: Long = System.currentTimeMillis(),
    val rawSmsId: Long? = null
)
