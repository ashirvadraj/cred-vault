package com.credtracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.credtracker.CredTrackerApp
import com.credtracker.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NotificationHelper.ACTION_MARK_PAID) {
            val billId = intent.getStringExtra(NotificationHelper.EXTRA_BILL_ID) ?: return

            CoroutineScope(Dispatchers.IO).launch {
                val repository = CredTrackerApp.instance.repository
                repository.markBillAsPaid(billId)

                // Cancel the notification
                NotificationManagerCompat.from(context).cancel(billId.hashCode())

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Bill marked as PAID! 🎉", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
