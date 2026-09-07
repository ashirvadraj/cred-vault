package com.credtracker.parser

import org.junit.Assert.*
import org.junit.Test

class SmsParserTest {

    @Test
    fun testHdfcBankSmsParsing() {
        val sender = "AD-HDFCBK"
        val body = "Total Amount Due for your HDFC Bank Credit Card ending 4821 is Rs 34,500.20. Payment Due Date: 15-Sep-2026. Min Due: Rs 1,725.00."
        val result = SmsParser.parseSms(sender, body)

        assertTrue(result is ParsedBillResult)
        val bill = result as ParsedBillResult
        assertEquals("HDFC Bank", bill.bankName)
        assertEquals("4821", bill.last4Digits)
        assertEquals(34500.20, bill.totalAmountDue, 0.01)
        assertEquals(1725.00, bill.minimumAmountDue, 0.01)
        assertNotNull(bill.dueDate)
    }

    @Test
    fun testSbiCardSmsParsing() {
        val sender = "VM-SBICRD"
        val body = "Statement for your SBI Card ending 9012 is generated. Total Amt Due: INR 18,250.00, Min Amt Due: INR 950.00, Pay by 22-Oct-2026."
        val result = SmsParser.parseSms(sender, body)

        assertTrue(result is ParsedBillResult)
        val bill = result as ParsedBillResult
        assertEquals("SBI Card", bill.bankName)
        assertEquals("9012", bill.last4Digits)
        assertEquals(18250.00, bill.totalAmountDue, 0.01)
        assertEquals(950.00, bill.minimumAmountDue, 0.01)
    }

    @Test
    fun testIciciBankSmsParsing() {
        val sender = "VK-ICICIB"
        val body = "Dear Customer, Total Due for your ICICI Bank Credit Card XX3344 is ₹45,100.00 due on 05-Nov-2026. Min Due ₹2,255.00."
        val result = SmsParser.parseSms(sender, body)

        assertTrue(result is ParsedBillResult)
        val bill = result as ParsedBillResult
        assertEquals("ICICI Bank", bill.bankName)
        assertEquals("3344", bill.last4Digits)
        assertEquals(45100.00, bill.totalAmountDue, 0.01)
    }

    @Test
    fun testAxisBankSmsParsing() {
        val sender = "AXISBK"
        val body = "Your Axis Bank Credit Card ending with 6712 bill is generated. Total Amount Due: Rs. 12,890.00, Due Date: 28-Sep-2026. Min Due: Rs. 650.00."
        val result = SmsParser.parseSms(sender, body)

        assertTrue(result is ParsedBillResult)
        val bill = result as ParsedBillResult
        assertEquals("Axis Bank", bill.bankName)
        assertEquals("6712", bill.last4Digits)
        assertEquals(12890.00, bill.totalAmountDue, 0.01)
    }

    @Test
    fun testOneCardSmsParsing() {
        val sender = "ONECRD"
        val body = "Your OneCard bill for card ending 5511 of Rs. 8,420.00 is due on 02-Oct-2026."
        val result = SmsParser.parseSms(sender, body)

        assertTrue(result is ParsedBillResult)
        val bill = result as ParsedBillResult
        assertEquals("OneCard", bill.bankName)
        assertEquals("5511", bill.last4Digits)
        assertEquals(8420.00, bill.totalAmountDue, 0.01)
    }

    @Test
    fun testPaymentConfirmationSms() {
        val sender = "AD-HDFCBK"
        val body = "Thank you for the payment of Rs. 34,500.00 towards your HDFC Bank Credit Card ending 4821 received on 14-Sep-2026."
        val result = SmsParser.parseSms(sender, body)

        assertTrue(result is ParsedPaymentResult)
        val payment = result as ParsedPaymentResult
        assertEquals("HDFC Bank", payment.bankName)
        assertEquals("4821", payment.last4Digits)
        assertEquals(34500.00, payment.paidAmount, 0.01)
    }

    @Test
    fun testDateParsingFormats() {
        val epoch1 = StatementDateExtractor.parseDate("15-Sep-2026")
        assertNotNull(epoch1)

        val epoch2 = StatementDateExtractor.parseDate("15/09/2026")
        assertNotNull(epoch2)

        val epoch3 = StatementDateExtractor.parseDate("15th September 2026")
        assertNotNull(epoch3)
    }
}
