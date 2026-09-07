package com.credtracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.credtracker.CredTrackerApp
import com.credtracker.parser.StatementDateExtractor
import com.credtracker.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = CredTrackerApp.instance.repository
            val pendingBills = repository.pendingBills.first()

            for (bill in pendingBills) {
                val daysRemaining = StatementDateExtractor.getDaysRemaining(bill.dueDate)
                // Fire notification if due within 7 days, 3 days, 1 day, or overdue
                if (daysRemaining in -1..7) {
                    NotificationHelper.showBillReminderNotification(context, bill, daysRemaining)
                }
            }
        }
    }
}
