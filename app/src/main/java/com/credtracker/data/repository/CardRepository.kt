package com.credtracker.data.repository

import com.credtracker.data.db.BillDao
import com.credtracker.data.db.CardDao
import com.credtracker.data.db.TransactionDao
import com.credtracker.data.model.BillStatement
import com.credtracker.data.model.BillStatus
import com.credtracker.data.model.CreditCard
import com.credtracker.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class CardRepository(
    private val cardDao: CardDao,
    private val billDao: BillDao,
    private val transactionDao: TransactionDao
) {
    val allCards: Flow<List<CreditCard>> = cardDao.getAllCards()
    val pendingBills: Flow<List<BillStatement>> = billDao.getPendingBills()
    val allBills: Flow<List<BillStatement>> = billDao.getAllBills()
    val totalOutstandingDue: Flow<Double?> = billDao.getTotalOutstandingDue()

    suspend fun getCardById(cardId: String): CreditCard? = cardDao.getCardById(cardId)

    suspend fun getBillsForCard(cardId: String): Flow<List<BillStatement>> = billDao.getBillsForCard(cardId)

    suspend fun getLatestPendingBill(cardId: String): BillStatement? = billDao.getLatestPendingBillForCard(cardId)

    suspend fun saveCard(card: CreditCard) = cardDao.insertOrUpdate(card)

    suspend fun saveBill(bill: BillStatement) = billDao.insertOrUpdateBill(bill)

    suspend fun recordPayment(billId: String, amount: Double) {
        val bill = billDao.getBillById(billId) ?: return
        val newPaid = bill.paidAmount + amount
        val status = if (newPaid >= bill.totalAmountDue) BillStatus.PAID else BillStatus.PARTIALLY_PAID
        billDao.updatePaymentStatus(
            billId = billId,
            status = status,
            paidAmount = newPaid,
            paidDate = System.currentTimeMillis()
        )
    }

    suspend fun markBillAsPaid(billId: String) {
        val bill = billDao.getBillById(billId) ?: return
        billDao.updatePaymentStatus(
            billId = billId,
            status = BillStatus.PAID,
            paidAmount = bill.totalAmountDue,
            paidDate = System.currentTimeMillis()
        )
    }

    suspend fun deleteCard(card: CreditCard) = cardDao.deleteCard(card)

    suspend fun updateOverdueBills() = billDao.markOverdueBills()

    fun getTransactionsForCard(cardId: String): Flow<List<Transaction>> = transactionDao.getTransactionsForCard(cardId)

    suspend fun saveTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
}
