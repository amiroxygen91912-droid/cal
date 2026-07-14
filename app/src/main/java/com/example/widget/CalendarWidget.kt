package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import com.example.CalendarApplication
import com.example.calendar.CalendarEngine
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class CalendarWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as CalendarApplication
        val database = app.database

        val today = Calendar.getInstance()
        val gYear = today.get(Calendar.YEAR)
        val gMonth = today.get(Calendar.MONTH) + 1
        val gDay = today.get(Calendar.DAY_OF_MONTH)

        val sDate = CalendarEngine.gregorianToSolarHijri(gYear, gMonth, gDay)
        val sYear = sDate.first
        val sMonth = sDate.second
        val sDay = sDate.third
        val iYear = sYear + 1180

        val jd = CalendarEngine.gregorianToJulianDay(gYear, gMonth, gDay)
        val weekdayIdx = CalendarEngine.getWeekdayIndex(jd)
        
        val useTraditional = app.settingsManager.useTraditionalDays
        val weekdayName = if (useTraditional) {
            CalendarEngine.traditionalWeekdayNamesPersian[weekdayIdx]
        } else {
            CalendarEngine.standardWeekdayNamesPersian[weekdayIdx]
        }

        val useHistoricalMonths = app.settingsManager.useHistoricalMonths
        val monthName = if (useHistoricalMonths) {
            CalendarEngine.historicalMonthNamesPersian[sMonth - 1]
        } else {
            CalendarEngine.standardMonthNamesPersian[sMonth - 1]
        }

        var festivalText = ""
        try {
            val festivals = database.festivalDao().getFestivalsForDay(sMonth, sDay).firstOrNull()
            if (!festivals.isNullOrEmpty()) {
                festivalText = festivals.first().name
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        provideContent {
            WidgetContent(
                iYear = iYear,
                sYear = sYear,
                gYear = gYear,
                monthName = monthName,
                day = sDay,
                weekdayName = weekdayName,
                festivalText = festivalText
            )
        }
    }

    @Composable
    private fun WidgetContent(
        iYear: Int,
        sYear: Int,
        gYear: Int,
        monthName: String,
        day: Int,
        weekdayName: String,
        festivalText: String
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFF050B18)))
                .padding(12.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = "تقویم شاهنشاهی ایران",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFFC5A059)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = GlanceModifier.height(4.dp))

            // Main Imperial Year
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "$iYear شاهنشاهی",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFFC5A059)),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(2.dp))

            // Date
            Text(
                text = "$weekdayName، $day $monthName",
                style = TextStyle(
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFFFFF)),
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Other dates row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = "$sYear خورشیدی  |  $gYear میلادی",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF8A99AD)),
                        fontSize = 10.sp
                    )
                )
            }

            if (festivalText.isNotEmpty()) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "جشن امروز: $festivalText",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF00A591)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()
}
