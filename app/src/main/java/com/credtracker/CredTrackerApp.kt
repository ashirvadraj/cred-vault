package com.credtracker

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.credtracker.data.db.AppDatabase
import com.credtracker.data.repository.CardRepository
import com.credtracker.util.NotificationHelper
import com.credtracker.worker.BillSyncWorker
import java.util.concurrent.TimeUnit

class CredTrackerApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: CardRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getDatabase(this)
        repository = CardRepository(
            cardDao = database.cardDao(),
            billDao = database.billDao(),
            transactionDao = database.transactionDao()
        )

        // Create system notification channels
        NotificationHelper.createNotificationChannels(this)

        // Setup background periodic bill sync worker (every 6 hours)
        schedulePeriodicSync()
    }

    private fun schedulePeriodicSync() {
        val syncRequest = PeriodicWorkRequestBuilder<BillSyncWorker>(6, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BillSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    companion object {
        lateinit var instance: CredTrackerApp
            private set
    }
}
