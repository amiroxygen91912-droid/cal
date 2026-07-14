package com.example.calendar

object CalendarEngine {

    // Solar Hijri Month Names
    val standardMonthNamesPersian = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val historicalMonthNamesPersian = listOf(
        "فرورتی (فَروَردین)", "اردوهشت (اُردیبهشت)", "خرداد (رسایی)",
        "تیشتر (تیر)", "امرداد (بی‌مرگی)", "شهریور (کشور برگزیده)",
        "مهر (پیمان)", "آبان (آب‌ها)", "آذر (آتش)",
        "دی (آفریدگار)", "وهمن (منش نیک)", "سپندارمد (فروتنی)"
    )

    // Weekday Names
    val standardWeekdayNamesPersian = listOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    val traditionalWeekdayNamesPersian = listOf(
        "کیوان‌شید", "مهرشید", "مهشید", "بهرام‌شید", "تیرشید", "هرمز‌شید", "آرام‌شید"
    )

    // English transcriptions for secondary display
    val standardMonthNamesEnglish = listOf(
        "Farvardin", "Ordibehesht", "Khordad",
        "Tir", "Mordad", "Shahrivar",
        "Mehr", "Aban", "Azar",
        "Dey", "Bahman", "Esfand"
    )

    val standardWeekdayNamesEnglish = listOf(
        "Shanbeh", "Yekshanbeh", "Doshanbeh", "Seshanbeh", "Chaharshanbeh", "Panjshanbeh", "Adineh"
    )

    val traditionalWeekdayNamesEnglish = listOf(
        "Keyvanshid", "Mehrshid", "Mahshid", "Bahramshid", "Tirshid", "Hormozshid", "Aramshid"
    )

    // Check if Solar Hijri year is leap
    fun isSolarHijriLeap(year: Int): Boolean {
        return jalCal(year).leap == 0
    }

    // Number of days in Solar Hijri Month
    fun getSolarHijriMonthLength(year: Int, month: Int): Int {
        if (month in 1..6) return 31
        if (month in 7..11) return 30
        if (month == 12) {
            return if (isSolarHijriLeap(year)) 30 else 29
        }
        return 0
    }

    // Mathematical Converter using Julian Day
    fun gregorianToJulianDay(year: Int, month: Int, day: Int): Long {
        val gy = year
        val gm = month
        val gd = day
        val div1 = (gm - 8) / 6
        val d = ((gy + div1 + 100100) * 1461) / 4 +
                (153 * ((gm + 9) % 12) + 2) / 5 +
                gd - 34840408
        val dAdjusted = d - (((gy + 100100 + div1) / 100) * 3) / 4 + 752
        return dAdjusted.toLong()
    }

    fun julianDayToGregorian(jd: Long): Triple<Int, Int, Int> {
        val jdn = jd.toInt()
        var j = 4 * jdn + 139361631
        j = j + (((4 * jdn + 183187720) / 146097) * 3) / 4 * 4 - 3908
        val i = ((j % 1461) / 4) * 5 + 308
        val gd = (i % 153) / 5 + 1
        val gm = ((i / 153) % 12) + 1
        val gy = j / 1461 - 100100 + (8 - gm) / 6
        return Triple(gy, gm, gd)
    }

    data class JalCalResult(val leap: Int, val gy: Int, val march: Int)

    fun jalCal(jy: Int): JalCalResult {
        val breaks = intArrayOf(-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178)
        val bl = breaks.size
        
        var adjustedJy = jy
        var cycleShift = 0
        if (adjustedJy < -61) {
            val shift = ((-61 - adjustedJy) / 2820 + 1) * 2820
            adjustedJy += shift
            cycleShift -= shift
        } else if (adjustedJy >= 3178) {
            val shift = ((adjustedJy - 3178) / 2820 + 1) * 2820
            adjustedJy -= shift
            cycleShift += shift
        }
        
        val gy = adjustedJy + 621
        var leapJ = -14
        var jp = breaks[0]
        
        var jump = 0
        for (i in 1 until bl) {
            val jm = breaks[i]
            jump = jm - jp
            if (adjustedJy < jm) break
            leapJ += (jump / 33) * 8 + (jump % 33) / 4
            jp = jm
        }
        var n = adjustedJy - jp
        leapJ += (n / 33) * 8 + (n % 33 + 3) / 4
        if (jump % 33 == 4 && jump - n == 4) {
            leapJ += 1
        }
        val leapG = (gy / 4) - ((gy / 100 + 1) * 3) / 4 - 150
        val march = 20 + leapJ - leapG
        
        var nAdjusted = n
        if (jump - n < 6) {
            nAdjusted = n - jump + ((jump + 4) / 33) * 33
        }
        var leap = ((nAdjusted + 1) % 33 - 1) % 4
        if (leap == -1) {
            leap = 4
        }
        return JalCalResult(leap, gy + cycleShift, march)
    }

    // Solar Hijri to Julian Day
    fun solarHijriToJulianDay(year: Int, month: Int, day: Int): Long {
        val r = jalCal(year)
        return gregorianToJulianDay(r.gy, 3, r.march) + (month - 1) * 31 - (month / 7) * (month - 7) + day - 1
    }

    // Julian Day to Solar Hijri
    fun julianDayToSolarHijri(jd: Long): Triple<Int, Int, Int> {
        val jdn = jd.toInt()
        val gDate = julianDayToGregorian(jd)
        val gy = gDate.first
        var jy = gy - 621
        val r = jalCal(jy)
        val jdn1f = gregorianToJulianDay(gy, 3, r.march).toInt()
        
        var k = jdn - jdn1f
        var jm: Int
        var jday: Int
        
        if (k >= 0) {
            if (k <= 185) {
                jm = 1 + k / 31
                jday = k % 31 + 1
                return Triple(jy, jm, jday)
            } else {
                k -= 186
            }
        } else {
            jy -= 1
            k += 179
            if (r.leap == 1) {
                k += 1
            }
        }
        jm = 7 + k / 30
        jday = k % 30 + 1
        return Triple(jy, jm, jday)
    }

    // Conversions
    fun gregorianToSolarHijri(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
        val jd = gregorianToJulianDay(year, month, day)
        return julianDayToSolarHijri(jd)
    }

    fun solarHijriToGregorian(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
        val jd = solarHijriToJulianDay(year, month, day)
        return julianDayToGregorian(jd)
    }

    // Imperial Conversions
    fun imperialToSolarHijri(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
        return Triple(year - 1180, month, day)
    }

    fun solarHijriToImperial(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
        return Triple(year + 1180, month, day)
    }

    fun imperialToGregorian(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
        val sDate = imperialToSolarHijri(year, month, day)
        return solarHijriToGregorian(sDate.first, sDate.second, sDate.third)
    }

    fun gregorianToImperial(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
        val sDate = gregorianToSolarHijri(year, month, day)
        return solarHijriToImperial(sDate.first, sDate.second, sDate.third)
    }

    // Get weekday index (0 for Saturday, 6 for Friday)
    fun getWeekdayIndex(jd: Long): Int {
        // Julian Day % 7: 0 is Monday, 1 is Tuesday, 2 is Wednesday, 3 is Thursday, 4 is Friday, 5 is Saturday, 6 is Sunday
        // We want Saturday as 0, Sunday as 1, ..., Friday as 6.
        // For Saturday (e.g. March 21, 2026 with JD = 2461121), (2461121 + 2) % 7 = 0.
        // Therefore, (jd + 2) % 7 gives exactly Saturday = 0, Sunday = 1, ..., Friday = 6.
        return ((jd + 2) % 7).toInt()
    }
}
