package com.credtracker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object UpiPaymentHelper {

    fun launchUpiPayment(
        context: Context,
        billerVpa: String?,
        payeeName: String,
        amount: Double,
        note: String = "Credit Card Bill Payment",
        specificPackage: String? = null
    ) {
        val vpa = if (!billerVpa.isNullOrBlank()) billerVpa else "paytm@upi"
        val uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", vpa)
            .appendQueryParameter("pn", payeeName)
            .appendQueryParameter("am", String.format(java.util.Locale.US, "%.2f", amount))
            .appendQueryParameter("cu", "INR")
            .appendQueryParameter("tn", note)
            .build()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (specificPackage != null) {
            intent.setPackage(specificPackage)
        }

        try {
            val chooser = Intent.createChooser(intent, "Pay via UPI App")
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "No UPI app found on device", Toast.LENGTH_SHORT).show()
        }
    }
}
