package com.credtracker.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.credtracker.R
import com.credtracker.data.model.BillStatement
import com.credtracker.parser.StatementDateExtractor
import com.credtracker.receiver.NotificationActionReceiver
import com.credtracker.ui.MainActivity
import java.text.NumberFormat
import java.util.Locale

object NotificationHelper {

    const val CHANNEL_REMINDERS_ID = "channel_bill_reminders"
    const val CHANNEL_SMS_ID = "channel_sms_detection"

    const val ACTION_MARK_PAID = "com.credtracker.ACTION_MARK_PAID"
    const val EXTRA_BILL_ID = "extra_bill_id"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS_ID,
                context.getString(R.string.channel_bill_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_bill_reminders_desc)
                enableVibration(true)
            }

            val smsChannel = NotificationChannel(
                CHANNEL_SMS_ID,
                context.getString(R.string.channel_sms_detection),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_sms_detection_desc)
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(reminderChannel)
            manager.createNotificationChannel(smsChannel)
        }
    }

    fun showBillReminderNotification(context: Context, bill: BillStatement, daysRemaining: Long) {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val formattedAmt = currencyFormatter.format(bill.totalAmountDue)
        val formattedDate = StatementDateExtractor.formatDate(bill.dueDate)

        val title = when {
            daysRemaining <= 0 -> "⚠️ Bill DUE TODAY: ${bill.bankName} (XX${bill.last4Digits})"
            daysRemaining == 1L -> "🚨 Due Tomorrow: ${bill.bankName} Card ($formattedAmt)"
            else -> "⏰ Due in $daysRemaining Days: ${bill.bankName} ($formattedAmt)"
        }

        val message = "Total amount of $formattedAmt is due by $formattedDate. Tap to pay now."

        // Content intent (opens app)
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_BILL_ID, bill.id)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            bill.id.hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // UPI Pay Intent Action
        val upiIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("upi://pay?pn=${Uri.encode(bill.bankName)}&am=${bill.totalAmountDue}&cu=INR&tn=Credit%20Card%20Bill%20Payment")
        }
        val upiPendingIntent = PendingIntent.getActivity(
            context,
            bill.id.hashCode() + 1,
            upiIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mark as Paid Action
        val markPaidIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_PAID
            putExtra(EXTRA_BILL_ID, bill.id)
        }
        val markPaidPendingIntent = PendingIntent.getBroadcast(
            context,
            bill.id.hashCode() + 2,
            markPaidIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\n\nMinimum Due: ${currencyFormatter.format(bill.minimumAmountDue)}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_send, "Pay via UPI", upiPendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, "Mark as Paid", markPaidPendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(bill.id.hashCode(), builder.build())
        } catch (_: SecurityException) {
            // Notification permission might not be granted yet
        }
    }

    fun showBillDetectedNotification(context: Context, bankName: String, amount: Double, last4: String, dueDate: Long) {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val formattedAmt = currencyFormatter.format(amount)
        val formattedDate = StatementDateExtractor.formatDate(dueDate)

        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_SMS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💳 New Statement: $bankName (XX$last4)")
            .setContentText("Bill of $formattedAmt generated. Due date: $formattedDate")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify((bankName + last4).hashCode(), builder.build())
        } catch (_: SecurityException) {}
    }
}
