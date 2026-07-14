package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.CalendarEngine
import com.example.database.FestivalEntity
import com.example.ui.CalendarViewModel
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianBlue
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: CalendarViewModel,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Force RTL layout for Farsi application
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val selectedDate by viewModel.selectedSolarDate.collectAsState()
        val sYear = selectedDate.first
        val sMonth = selectedDate.second
        val sDay = selectedDate.third
        val iYear = sYear + 1180

        val useTraditionalDays by viewModel.useTraditionalDays.collectAsState()
        val useHistoricalMonths by viewModel.useHistoricalMonths.collectAsState()
        val fontSizeSetting by viewModel.fontSize.collectAsState()

        // Scaled size factor
        val scale = when (fontSizeSetting) {
            "small" -> 0.85f
            "large" -> 1.15f
            else -> 1.0f
        }

        // Gregorian details for selection
        val gDate = CalendarEngine.solarHijriToGregorian(sYear, sMonth, sDay)
        val jd = CalendarEngine.solarHijriToJulianDay(sYear, sMonth, sDay)
        val weekdayIdx = CalendarEngine.getWeekdayIndex(jd)

        val weekdayName = if (useTraditionalDays) {
            CalendarEngine.traditionalWeekdayNamesPersian[weekdayIdx]
        } else {
            CalendarEngine.standardWeekdayNamesPersian[weekdayIdx]
        }

        val weekdayEnglish = if (useTraditionalDays) {
            CalendarEngine.traditionalWeekdayNamesEnglish[weekdayIdx]
        } else {
            CalendarEngine.standardWeekdayNamesEnglish[weekdayIdx]
        }

        val monthName = if (useHistoricalMonths) {
            CalendarEngine.historicalMonthNamesPersian[sMonth - 1]
        } else {
            CalendarEngine.standardMonthNamesPersian[sMonth - 1]
        }

        val monthEnglish = CalendarEngine.standardMonthNamesEnglish[sMonth - 1]

        val dayFestivals by viewModel.selectedDayFestivals.collectAsState(initial = emptyList())

        var showDetailDialog by remember { mutableStateOf<FestivalEntity?>(null) }
        var isBookmarked by remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "تقویم شاهنشاهی ایران",
                        color = PersianGold,
                        fontSize = (20 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Iranian Imperial Calendar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = (11 * scale).sp,
                        fontWeight = FontWeight.Light
                    )
                }
                
                // Active status indicator (RTL lotus icon)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .border(1.dp, PersianGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Simple representation of lotus
                    Text(text = "💮", fontSize = 16.sp, color = PersianGold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Imperial Year Display
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$iYear",
                        color = PersianGold,
                        fontSize = (68 * scale).sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 70.sp
                    )
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "شاهنشاهی",
                            color = PersianTurquoise,
                            fontSize = (13 * scale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = monthName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = (18 * scale).sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Decorative Gold Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    PersianGold.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Detailed Day Text
                Text(
                    text = "$weekdayName، $sDay $monthName",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    fontSize = (22 * scale).sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "$weekdayEnglish, $sDay $monthEnglish",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = (13 * scale).sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Date System Cards (Recipe 1 grid style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Solar Hijri Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "خورشیدی",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = (10 * scale).sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$sYear",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = (18 * scale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Solar Hijri",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = (11 * scale).sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }

                // Gregorian Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "میلادی",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = (10 * scale).sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${gDate.first}/${gDate.second}/${gDate.third}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = (15 * scale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gregorian",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = (11 * scale).sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Active Event Section (جشن امروز)
            Text(
                text = "مناسبت و جشن امروز",
                color = PersianGold,
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (dayFestivals.isNotEmpty()) {
                dayFestivals.forEach { festival ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, PersianGold.copy(alpha = 0.25f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            PersianBlue.copy(alpha = 0.15f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                PersianGold.copy(alpha = 0.15f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "جشن ملی",
                                            color = PersianGold,
                                            fontSize = (10 * scale).sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "امروز",
                                        color = PersianTurquoise,
                                        fontSize = (12 * scale).sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = festival.name,
                                    color = Color.White,
                                    fontSize = (24 * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                )

                                Text(
                                    text = festival.description,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    fontSize = (14 * scale).sp,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showDetailDialog = festival },
                                        colors = ButtonDefaults.buttonColors(containerColor = PersianGold),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "مطالعه جزئیات بیشتر",
                                            color = MaterialTheme.colorScheme.background,
                                            fontSize = (12 * scale).sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        onClick = { isBookmarked = !isBookmarked },
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "نشان کردن",
                                            tint = PersianGold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Placeholder when no festival today (standard elegant message)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "بدون رویداد",
                            tint = PersianGold.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "امروز جشن یا مناسبت خاصی ثبت نشده است.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = (13 * scale).sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "می‌توانید برای مطالعه درباره شاهنشاهی‌های باستان به بخش تاریخ بروید.",
                            color = PersianTurquoise,
                            fontSize = (11 * scale).sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.clickable { onNavigateToHistory() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Day Name Meaning Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "معنای نام روز",
                            tint = PersianGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "معنای نام امروز ($weekdayName)",
                            color = PersianGold,
                            fontSize = (14 * scale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val weekdayMeaning = when (weekdayIdx) {
                        0 -> "«کیوان‌شید» منتسب به کیوان (سیاره زحل) است؛ در فرهنگ کهن ایران باستان، این روز سمبل عظمت، پایداری، بردباری و استواری بی‌کران قلمداد می‌شده است."
                        1 -> "«مهرشید» منتسب به مهر (خورشید تابان) است؛ نماد روشنایی، گرما، عشق، صداقت و پیمان‌های ابدی میان انسان‌ها."
                        2 -> "«مهشید» منتسب به ماه زیباست؛ مظهر آرامش شبانه، رویاهای سپید، الهام‌بخش هنرمندان و صلح درونی زندگی."
                        3 -> "«بهرام‌شید» منتسب به بهرام (سیاره مریخ) است؛ مظهر شجاعت ملی، توانمندی، پایمردی، اراده پولادین و پایداری در برابر سختی‌ها."
                        4 -> "«تیرشید» منتسب به تیر (سیاره عطارد) است؛ مظهر هوش، علم‌افزایی، هنر نویسندگی، ارتباطات سازنده و پویایی اندیشه."
                        5 -> "«هرمز‌شید» منتسب به هرمز (سیاره مشتری) است؛ مظهر دانش، دانایی، خرد کیهانی، سخاوت برکت‌بخش و راستی هستی."
                        else -> "«آرام‌شید» (منتسب به ناهید/زهره) روز آدینه و آرامش مطلق است؛ مظهر صلح همگانی، استراحت روان، تجدید قوای زندگی و سپاسگزاری از مواهب هستی."
                    }

                    Text(
                        text = weekdayMeaning,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = (13 * scale).sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Detailed Day Dialog (Recipe 15 alert style)
        if (showDetailDialog != null) {
            val festival = showDetailDialog!!
            AlertDialog(
                onDismissRequest = { showDetailDialog = null },
                confirmButton = {
                    TextButton(
                        onClick = { showDetailDialog = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = PersianGold)
                    ) {
                        Text("بستن", fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = festival.name,
                        color = PersianGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = (18 * scale).sp,
                        fontFamily = FontFamily.Serif
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date Label
                        Text(
                            text = "تاریخ رویداد: ${festival.day} ${CalendarEngine.standardMonthNamesPersian[festival.month - 1]}",
                            color = PersianTurquoise,
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * scale).sp
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        // Meaning Section
                        Text(
                            text = "فلسفه و معنای نام:",
                            color = PersianGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * scale).sp
                        )
                        Text(
                            text = festival.meaning,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            fontSize = (13 * scale).sp,
                            lineHeight = 20.sp
                        )

                        // Description Section
                        Text(
                            text = "توضیحات کلی:",
                            color = PersianGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * scale).sp
                        )
                        Text(
                            text = festival.description,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            fontSize = (13 * scale).sp,
                            lineHeight = 20.sp
                        )

                        // Historical Notes Section
                        Text(
                            text = "پیشینه تاریخی رویداد:",
                            color = PersianGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * scale).sp
                        )
                        Text(
                            text = festival.historyNotes,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            fontSize = (13 * scale).sp,
                            lineHeight = 20.sp
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }
}
