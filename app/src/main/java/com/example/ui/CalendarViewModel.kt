package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.CalendarApplication
import com.example.calendar.CalendarEngine
import com.example.database.FestivalEntity
import com.example.database.HistoryTopicEntity
import com.example.repository.CalendarRepository
import com.example.utils.SettingsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as CalendarApplication
    private val repository: CalendarRepository = app.repository
    val settingsManager: SettingsManager = app.settingsManager

    // --- Today & Selection State ---
    private val _selectedSolarDate = MutableStateFlow(getTodaySolarHijri())
    val selectedSolarDate: StateFlow<Triple<Int, Int, Int>> = _selectedSolarDate.asStateFlow()

    fun getTodaySolarHijri(): Triple<Int, Int, Int> {
        val today = Calendar.getInstance()
        return CalendarEngine.gregorianToSolarHijri(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH) + 1,
            today.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Today's Date
    val todaySolarDate = getTodaySolarHijri()

    // Selected Date Month and Day
    val selectedMonthFestivals: Flow<List<FestivalEntity>> = _selectedSolarDate.flatMapLatest { date ->
        repository.getFestivalsForMonth(date.second)
    }

    val selectedDayFestivals: Flow<List<FestivalEntity>> = _selectedSolarDate.flatMapLatest { date ->
        repository.getFestivalsForDay(date.second, date.third)
    }

    // --- Settings State (StateFlow for Compose reactivity) ---
    private val _theme = MutableStateFlow(settingsManager.theme)
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _useTraditionalDays = MutableStateFlow(settingsManager.useTraditionalDays)
    val useTraditionalDays: StateFlow<Boolean> = _useTraditionalDays.asStateFlow()

    private val _useHistoricalMonths = MutableStateFlow(settingsManager.useHistoricalMonths)
    val useHistoricalMonths: StateFlow<Boolean> = _useHistoricalMonths.asStateFlow()

    private val _fontSize = MutableStateFlow(settingsManager.fontSize)
    val fontSize: StateFlow<String> = _fontSize.asStateFlow()

    fun updateTheme(newTheme: String) {
        settingsManager.theme = newTheme
        _theme.value = newTheme
    }

    fun updateTraditionalDays(enabled: Boolean) {
        settingsManager.useTraditionalDays = enabled
        _useTraditionalDays.value = enabled
    }

    fun updateHistoricalMonths(enabled: Boolean) {
        settingsManager.useHistoricalMonths = enabled
        _useHistoricalMonths.value = enabled
    }

    fun updateFontSize(size: String) {
        settingsManager.fontSize = size
        _fontSize.value = size
    }

    fun isNotificationEnabled(eventKey: String): Boolean {
        return settingsManager.isNotificationEnabled(eventKey)
    }

    fun toggleNotification(eventKey: String, enabled: Boolean) {
        settingsManager.setNotificationEnabled(eventKey, enabled)
    }

    fun selectDate(year: Int, month: Int, day: Int) {
        _selectedSolarDate.value = Triple(year, month, day)
    }

    fun selectPreviousMonth() {
        val current = _selectedSolarDate.value
        var m = current.second - 1
        var y = current.first
        if (m < 1) {
            m = 12
            y--
        }
        val maxDay = CalendarEngine.getSolarHijriMonthLength(y, m)
        val d = if (current.third > maxDay) maxDay else current.third
        _selectedSolarDate.value = Triple(y, m, d)
    }

    fun selectNextMonth() {
        val current = _selectedSolarDate.value
        var m = current.second + 1
        var y = current.first
        if (m > 12) {
            m = 1
            y++
        }
        val maxDay = CalendarEngine.getSolarHijriMonthLength(y, m)
        val d = if (current.third > maxDay) maxDay else current.third
        _selectedSolarDate.value = Triple(y, m, d)
    }

    // --- History Section ---
    val achaemenidTopics = repository.getHistoryTopicsByEra("achaemenid")
    val parthianTopics = repository.getHistoryTopicsByEra("parthian")
    val sasanianTopics = repository.getHistoryTopicsByEra("sasanian")
    val kingsTopics = repository.getHistoryTopicsByEra("kings")
    val symbolsTopics = repository.getHistoryTopicsByEra("symbols")

    // --- Search Section ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchFestivalResults: Flow<List<FestivalEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isEmpty()) flowOf(emptyList())
            else repository.searchFestivals(query)
        }

    val searchHistoryResults: Flow<List<HistoryTopicEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isEmpty()) flowOf(emptyList())
            else repository.searchHistoryTopics(query)
        }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Precise Date Converter Engine ---
    data class ConversionState(
        val sourceSystem: String = "imperial", // "imperial", "hijri", "gregorian"
        val targetSystem: String = "gregorian",
        val year: String = "2585",
        val month: Int = 1,
        val day: Int = 1,
        val resultImperial: String = "",
        val resultHijri: String = "",
        val resultGregorian: String = ""
    )

    private val _conversionState = MutableStateFlow(ConversionState())
    val conversionState: StateFlow<ConversionState> = _conversionState.asStateFlow()

    fun updateConversionSource(system: String) {
        val current = _conversionState.value
        val defaultYear = when(system) {
            "imperial" -> "2585"
            "hijri" -> "1405"
            else -> "2026"
        }
        _conversionState.value = current.copy(
            sourceSystem = system,
            year = defaultYear,
            month = 1,
            day = 1
        )
        performConversion()
    }

    fun updateConversionDateInput(year: String, month: Int, day: Int) {
        _conversionState.value = _conversionState.value.copy(
            year = year,
            month = month,
            day = day
        )
        performConversion()
    }

    fun performConversion() {
        val state = _conversionState.value
        val yInt = state.year.toIntOrNull() ?: return

        try {
            when (state.sourceSystem) {
                "imperial" -> {
                    val sDate = CalendarEngine.imperialToSolarHijri(yInt, state.month, state.day)
                    val gDate = CalendarEngine.solarHijriToGregorian(sDate.first, sDate.second, sDate.third)
                    _conversionState.value = state.copy(
                        resultImperial = "$yInt/${state.month}/${state.day} شاهنشاهی",
                        resultHijri = "${sDate.first}/${sDate.second}/${sDate.third} خورشیدی",
                        resultGregorian = "${gDate.first}/${gDate.second}/${gDate.third} میلادی"
                    )
                }
                "hijri" -> {
                    val iDate = CalendarEngine.solarHijriToImperial(yInt, state.month, state.day)
                    val gDate = CalendarEngine.solarHijriToGregorian(yInt, state.month, state.day)
                    _conversionState.value = state.copy(
                        resultImperial = "${iDate.first}/${state.month}/${state.day} شاهنشاهی",
                        resultHijri = "$yInt/${state.month}/${state.day} خورشیدی",
                        resultGregorian = "${gDate.first}/${gDate.second}/${gDate.third} میلادی"
                    )
                }
                "gregorian" -> {
                    val sDate = CalendarEngine.gregorianToSolarHijri(yInt, state.month, state.day)
                    val iDate = CalendarEngine.solarHijriToImperial(sDate.first, sDate.second, sDate.third)
                    _conversionState.value = state.copy(
                        resultImperial = "${iDate.first}/${sDate.second}/${sDate.third} شاهنشاهی",
                        resultHijri = "${sDate.first}/${sDate.second}/${sDate.third} خورشیدی",
                        resultGregorian = "$yInt/${state.month}/${state.day} میلادی"
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        performConversion() // run initial conversion
    }
}
