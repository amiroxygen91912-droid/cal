package com.example.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("imperial_calendar_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "theme" // "light", "dark", "auto"
        private const val KEY_TRADITIONAL_DAYS = "traditional_days" // boolean
        private const val KEY_HISTORICAL_MONTHS = "historical_months" // boolean
        private const val KEY_FONT_SIZE = "font_size" // "small", "medium", "large"
        private const val KEY_NOTIF_PREFIX = "notif_event_" // boolean prefix for each event
    }

    var theme: String
        get() = prefs.getString(KEY_THEME, "auto") ?: "auto"
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    var useTraditionalDays: Boolean
        get() = prefs.getBoolean(KEY_TRADITIONAL_DAYS, false)
        set(value) = prefs.edit().putBoolean(KEY_TRADITIONAL_DAYS, value).apply()

    var useHistoricalMonths: Boolean
        get() = prefs.getBoolean(KEY_HISTORICAL_MONTHS, false)
        set(value) = prefs.edit().putBoolean(KEY_HISTORICAL_MONTHS, value).apply()

    var fontSize: String
        get() = prefs.getString(KEY_FONT_SIZE, "medium") ?: "medium"
        set(value) = prefs.edit().putString(KEY_FONT_SIZE, value).apply()

    fun isNotificationEnabled(eventKey: String): Boolean {
        // Events: "Nowruz", "Mehregan", "Tirgan", "Yalda", "Sadeh", "Sepandarmazgan"
        return prefs.getBoolean(KEY_NOTIF_PREFIX + eventKey, true)
    }

    fun setNotificationEnabled(eventKey: String, enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIF_PREFIX + eventKey, enabled).apply()
    }
}
