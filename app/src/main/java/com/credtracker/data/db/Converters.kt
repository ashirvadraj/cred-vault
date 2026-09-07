package com.credtracker.data.db

import androidx.room.TypeConverter
import com.credtracker.data.model.BillStatus
import com.credtracker.data.model.CardNetwork
import com.credtracker.data.model.CardTheme
import com.credtracker.data.model.StatementSource
import com.credtracker.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromCardNetwork(value: CardNetwork): String = value.name

    @TypeConverter
    fun toCardNetwork(value: String): CardNetwork = try {
        CardNetwork.valueOf(value)
    } catch (e: Exception) {
        CardNetwork.OTHER
    }

    @TypeConverter
    fun fromCardTheme(value: CardTheme): String = value.name

    @TypeConverter
    fun toCardTheme(value: String): CardTheme = try {
        CardTheme.valueOf(value)
    } catch (e: Exception) {
        CardTheme.OBSIDIAN_GOLD
    }

    @TypeConverter
    fun fromBillStatus(value: BillStatus): String = value.name

    @TypeConverter
    fun toBillStatus(value: String): BillStatus = try {
        BillStatus.valueOf(value)
    } catch (e: Exception) {
        BillStatus.PENDING
    }

    @TypeConverter
    fun fromStatementSource(value: StatementSource): String = value.name

    @TypeConverter
    fun toStatementSource(value: String): StatementSource = try {
        StatementSource.valueOf(value)
    } catch (e: Exception) {
        StatementSource.SMS
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = try {
        TransactionType.valueOf(value)
    } catch (e: Exception) {
        TransactionType.DEBIT
    }
}
