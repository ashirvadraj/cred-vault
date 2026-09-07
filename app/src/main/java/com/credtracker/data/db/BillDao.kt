package com.credtracker.data.db

import androidx.room.*
import com.credtracker.data.model.BillStatement
import com.credtracker.data.model.BillStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bill_statements ORDER BY dueDate ASC")
    fun getAllBills(): Flow<List<BillStatement>>

    @Query("SELECT * FROM bill_statements WHERE status = 'PENDING' OR status = 'OVERDUE' ORDER BY dueDate ASC")
    fun getPendingBills(): Flow<List<BillStatement>>

    @Query("SELECT * FROM bill_statements WHERE cardId = :cardId ORDER BY dueDate DESC")
    fun getBillsForCard(cardId: String): Flow<List<BillStatement>>

    @Query("SELECT * FROM bill_statements WHERE cardId = :cardId AND (status = 'PENDING' OR status = 'OVERDUE') ORDER BY dueDate ASC LIMIT 1")
    suspend fun getLatestPendingBillForCard(cardId: String): BillStatement?

    @Query("SELECT * FROM bill_statements WHERE id = :billId LIMIT 1")
    suspend fun getBillById(billId: String): BillStatement?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBill(bill: BillStatement)

    @Query("UPDATE bill_statements SET status = :status, paidAmount = :paidAmount, paidDate = :paidDate, updatedAt = :updatedAt WHERE id = :billId")
    suspend fun updatePaymentStatus(billId: String, status: BillStatus, paidAmount: Double, paidDate: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE bill_statements SET status = 'OVERDUE' WHERE dueDate < :currentTime AND status = 'PENDING'")
    suspend fun markOverdueBills(currentTime: Long = System.currentTimeMillis())

    @Query("SELECT SUM(totalAmountDue - paidAmount) FROM bill_statements WHERE status = 'PENDING' OR status = 'OVERDUE'")
    fun getTotalOutstandingDue(): Flow<Double?>

    @Delete
    suspend fun deleteBill(bill: BillStatement)
}
