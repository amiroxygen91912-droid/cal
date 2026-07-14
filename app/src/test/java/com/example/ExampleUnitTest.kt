package com.example

import com.example.calendar.CalendarEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testCalendarConversions() {
    // March 21, 2026 is Nowruz (Farvardin 1, 1405)
    val (jy, jm, jd) = CalendarEngine.gregorianToSolarHijri(2026, 3, 21)
    assertEquals(1405, jy)
    assertEquals(1, jm)
    assertEquals(1, jd)

    val (jyNext, jmNext, jdNext) = CalendarEngine.gregorianToSolarHijri(2026, 3, 22)
    assertEquals(1405, jyNext)
    assertEquals(1, jmNext)
    assertEquals(2, jdNext)

    val (jyPrev, jmPrev, jdPrev) = CalendarEngine.gregorianToSolarHijri(2026, 3, 20)
    assertEquals(1404, jyPrev)
    assertEquals(12, jmPrev)
    assertEquals(29, jdPrev)
  }

  @Test
  fun testLeapYears() {
    // 1403 was a leap year (Esfand has 30 days)
    assertTrue(CalendarEngine.isSolarHijriLeap(1403))
    assertEquals(30, CalendarEngine.getSolarHijriMonthLength(1403, 12))

    // 1404 is not a leap year (Esfand has 29 days)
    assertFalse(CalendarEngine.isSolarHijriLeap(1404))
    assertEquals(29, CalendarEngine.getSolarHijriMonthLength(1404, 12))
  }

  @Test
  fun testTraditionalWeekdayMapping() {
    // 2026-03-21 must be Saturday (Index 0)
    val jdMarch21 = CalendarEngine.gregorianToJulianDay(2026, 3, 21)
    val idxMarch21 = CalendarEngine.getWeekdayIndex(jdMarch21)
    assertEquals(0, idxMarch21)
    assertEquals("شنبه", CalendarEngine.standardWeekdayNamesPersian[idxMarch21])

    // 1405/01/01 must be Saturday (Index 0)
    val jdNowruz = CalendarEngine.solarHijriToJulianDay(1405, 1, 1)
    val idxNowruz = CalendarEngine.getWeekdayIndex(jdNowruz)
    assertEquals(0, idxNowruz)
    assertEquals("شنبه", CalendarEngine.standardWeekdayNamesPersian[idxNowruz])

    // 23 Tir 1405 (1405/04/23) must be Tuesday (Index 3)
    val jd = CalendarEngine.solarHijriToJulianDay(1405, 4, 23)
    val idx = CalendarEngine.getWeekdayIndex(jd)
    
    assertEquals(3, idx) // Tuesday is Index 3
    assertEquals("سه‌شنبه", CalendarEngine.standardWeekdayNamesPersian[idx])
    assertEquals("بهرام‌شید", CalendarEngine.traditionalWeekdayNamesPersian[idx])
    assertEquals("Bahramshid", CalendarEngine.traditionalWeekdayNamesEnglish[idx])

    // Verify all traditional names are synchronized and correct
    val expectedTraditionalNames = listOf(
      "کیوان‌شید", "مهرشید", "مهشید", "بهرام‌شید", "تیرشید", "هرمز‌شید", "آرام‌شید"
    )
    val expectedTraditionalEnglish = listOf(
      "Keyvanshid", "Mehrshid", "Mahshid", "Bahramshid", "Tirshid", "Hormozshid", "Aramshid"
    )
    val expectedStandardNames = listOf(
      "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    for (i in 0..6) {
      assertEquals(expectedStandardNames[i], CalendarEngine.standardWeekdayNamesPersian[i])
      assertEquals(expectedTraditionalNames[i], CalendarEngine.traditionalWeekdayNamesPersian[i])
      assertEquals(expectedTraditionalEnglish[i], CalendarEngine.traditionalWeekdayNamesEnglish[i])
    }
  }

  @Test
  fun testWeekdayComparisonWithJavaTime() {
    for (year in 2020..2030) {
      for (month in 1..12) {
        val daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth()
        for (day in 1..daysInMonth) {
          val localDate = java.time.LocalDate.of(year, month, day)
          val dayOfWeek = localDate.dayOfWeek
          val expectedIndex = when (dayOfWeek) {
            java.time.DayOfWeek.SATURDAY -> 0
            java.time.DayOfWeek.SUNDAY -> 1
            java.time.DayOfWeek.MONDAY -> 2
            java.time.DayOfWeek.TUESDAY -> 3
            java.time.DayOfWeek.WEDNESDAY -> 4
            java.time.DayOfWeek.THURSDAY -> 5
            java.time.DayOfWeek.FRIDAY -> 6
            else -> throw IllegalStateException()
          }
          
          val jd = CalendarEngine.gregorianToJulianDay(year, month, day)
          val actualIndex = CalendarEngine.getWeekdayIndex(jd)
          
          assertEquals("Failed at $year-$month-$day (LocalDate is $dayOfWeek)", expectedIndex, actualIndex)
        }
      }
    }
  }
}



