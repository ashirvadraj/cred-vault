package com.credtracker.data.db

import androidx.room.*
import com.credtracker.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM card_transactions WHERE cardId = :cardId ORDER BY timestamp DESC")
    fun getTransactionsForCard(cardId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM card_transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 20): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(transactions: List<Transaction>)
}
