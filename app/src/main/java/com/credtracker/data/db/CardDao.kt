package com.credtracker.data.db

import androidx.room.*
import com.credtracker.data.model.CreditCard
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM credit_cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<CreditCard>>

    @Query("SELECT * FROM credit_cards WHERE id = :cardId LIMIT 1")
    suspend fun getCardById(cardId: String): CreditCard?

    @Query("SELECT * FROM credit_cards WHERE bankName = :bankName AND last4Digits = :last4 LIMIT 1")
    suspend fun getCardByBankAndLast4(bankName: String, last4: String): CreditCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(card: CreditCard)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNew(card: CreditCard): Long

    @Delete
    suspend fun deleteCard(card: CreditCard)

    @Query("UPDATE credit_cards SET availableLimit = :availableLimit WHERE id = :cardId")
    suspend fun updateAvailableLimit(cardId: String, availableLimit: Double)

    @Query("UPDATE credit_cards SET totalLimit = :totalLimit WHERE id = :cardId")
    suspend fun updateTotalLimit(cardId: String, totalLimit: Double)
}
