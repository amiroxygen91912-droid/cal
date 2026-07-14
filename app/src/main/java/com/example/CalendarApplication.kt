package com.example

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.database.AppDatabase
import com.example.repository.CalendarRepository
import com.example.utils.SettingsManager
import com.example.notifications.CalendarNotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.TimeUnit

class CalendarApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { CalendarRepository(database.festivalDao(), database.historyTopicDao()) }
    val settingsManager by lazy { SettingsManager(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Setup WorkManager to schedule daily notifications check
        setupDailyNotificationCheck()
    }

    private fun setupDailyNotificationCheck() {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<CalendarNotificationWorker>(
                1, TimeUnit.DAYS
            )
            .setConstraints(constraints)
            .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "CalendarDailyNotification",
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWorkRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        lateinit var instance: CalendarApplication
            private set
    }
}
