package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.CalendarApplication
import com.example.MainActivity
import com.example.calendar.CalendarEngine
import kotlinx.coroutines.flow.first
import java.util.Calendar

class CalendarNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as CalendarApplication
        val repository = app.repository
        val settings = app.settingsManager

        // Get today's Gregorian date
        val today = Calendar.getInstance()
        val gYear = today.get(Calendar.YEAR)
        val gMonth = today.get(Calendar.MONTH) + 1 // 0-based
        val gDay = today.get(Calendar.DAY_OF_MONTH)

        // Convert to Solar Hijri
        val sDate = CalendarEngine.gregorianToSolarHijri(gYear, gMonth, gDay)
        val sMonth = sDate.second
        val sDay = sDate.third

        return try {
            val database = app.database
            // Read first list emission from Flow
            val festivals = database.festivalDao().getFestivalsForDay(sMonth, sDay).first()
            
            festivals.forEach { festival ->
                // Map festival name to keys for settings check
                // Events: "Nowruz", "Mehregan", "Tirgan", "Yalda", "Sadeh", "Sepandarmazgan"
                val eventKey = when {
                    festival.name.contains("نوروز") -> "Nowruz"
                    festival.name.contains("مهرگان") -> "Mehregan"
                    festival.name.contains("تیرگان") -> "Tirgan"
                    festival.name.contains("یلدا") -> "Yalda"
                    festival.name.contains("سده") -> "Sadeh"
                    festival.name.contains("اسفندگان") || festival.name.contains("سپندارمزگان") -> "Sepandarmazgan"
                    else -> "Other"
                }

                if (settings.isNotificationEnabled(eventKey)) {
                    sendNotification(festival.name, festival.description)
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun sendNotification(title: String, body: String) {
        val channelId = "iranian_imperial_calendar_notifications"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "رویدادها و جشن‌های ملی",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "اعلان جشن‌ها و مناسبت‌های تقویم شاهنشاهی ایران"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // simple system icon for compatibility
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(title.hashCode(), notification)
    }
}
