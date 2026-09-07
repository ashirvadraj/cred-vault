package com.credtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class BillStatus {
    PENDING,
    PAID,
    OVERDUE,
    PARTIALLY_PAID
}

enum class StatementSource {
    SMS,
    GMAIL,
    MANUAL
}

@Entity(
    tableName = "bill_statements",
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
        Index(value = ["dueDate"]),
        Index(value = ["status"])
    ]
)
data class BillStatement(
    @PrimaryKey
    val id: String, // e.g. "HDFC_4821_2026_09"
    val cardId: String,
    val bankName: String,
    val last4Digits: String,
    val totalAmountDue: Double,
    val minimumAmountDue: Double = 0.0,
    val statementDate: Long = System.currentTimeMillis(),
    val dueDate: Long,
    val status: BillStatus = BillStatus.PENDING,
    val paidAmount: Double = 0.0,
    val paidDate: Long? = null,
    val source: StatementSource = StatementSource.SMS,
    val rawSnippet: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
