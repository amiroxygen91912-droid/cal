package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val selectedDate by viewModel.selectedSolarDate.collectAsState()
        val sYear = selectedDate.first
        val sMonth = selectedDate.second
        val sDay = selectedDate.third
        val iYear = sYear + 1180

        val useTraditionalDays by viewModel.useTraditionalDays.collectAsState()
        val useHistoricalMonths by viewModel.useHistoricalMonths.collectAsState()
        val fontSizeSetting by viewModel.fontSize.collectAsState()

        val scale = when (fontSizeSetting) {
            "small" -> 0.85f
            "large" -> 1.15f
            else -> 1.0f
        }

        val monthName = if (useHistoricalMonths) {
            CalendarEngine.historicalMonthNamesPersian[sMonth - 1]
        } else {
            CalendarEngine.standardMonthNamesPersian[sMonth - 1]
        }

        // Selected Day festivals
        val dayFestivals by viewModel.selectedDayFestivals.collectAsState(initial = emptyList())
        // All festivals in the current month for badge drawings
        val monthFestivals by viewModel.selectedMonthFestivals.collectAsState(initial = emptyList())

        // Calculate days details
        val totalDays = CalendarEngine.getSolarHijriMonthLength(sYear, sMonth)
        val firstDayJd = CalendarEngine.solarHijriToJulianDay(sYear, sMonth, 1)
        val startWeekdayIdx = CalendarEngine.getWeekdayIndex(firstDayJd) // 0=Saturday, 6=Friday

        // List of days including leading empties
        val gridItems = remember(sYear, sMonth, startWeekdayIdx, totalDays) {
            val list = mutableListOf<Int?>()
            // Add empty padding for starting weekday offset
            for (i in 0 until startWeekdayIdx) {
                list.add(null)
            }
            // Add actual days
            for (d in 1..totalDays) {
                list.add(d)
            }
            list
        }

        var showDetailDialog by remember { mutableStateOf<FestivalEntity?>(null) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header with Month Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.selectNextMonth() },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "ماه بعدی",
                        tint = PersianGold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = monthName,
                        color = PersianGold,
                        fontSize = (22 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "$iYear شاهنشاهی  /  $sYear خورشیدی",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = (11 * scale).sp,
                        fontWeight = FontWeight.Light
                    )
                }

                IconButton(
                    onClick = { viewModel.selectPreviousMonth() },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "ماه قبلی",
                        tint = PersianGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Weekday Headers Grid
            val weekdayLabels = if (useTraditionalDays) {
                CalendarEngine.traditionalWeekdayNamesPersian
            } else {
                CalendarEngine.standardWeekdayNamesPersian
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekdayLabels.forEach { label ->
                    val shortLabel = if (label.length > 3) label.substring(0, 3) else label
                    Text(
                        text = shortLabel,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (10 * scale).sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Monthly Grid (Strictly offline calculated)
            // Use static non-lazy column with rows to avoid scrolling issues inside a scrollable column
            val rows = gridItems.chunked(7)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    rows.forEach { rowDays ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            rowDays.forEach { dayNum ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(3.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dayNum != null) {
                                        val isSelected = (dayNum == sDay)
                                        val hasFestival = monthFestivals.any { it.day == dayNum }
                                        val isToday = (dayNum == viewModel.todaySolarDate.third &&
                                                       sMonth == viewModel.todaySolarDate.second &&
                                                       sYear == viewModel.todaySolarDate.first)

                                        Column(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isSelected -> PersianGold
                                                        isToday -> PersianTurquoise.copy(alpha = 0.25f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .border(
                                                    width = if (isToday && !isSelected) 1.dp else 0.dp,
                                                    color = if (isToday && !isSelected) PersianTurquoise else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable { viewModel.selectDate(sYear, sMonth, dayNum) },
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                color = when {
                                                    isSelected -> MaterialTheme.colorScheme.background
                                                    isToday -> PersianTurquoise
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                },
                                                fontSize = (14 * scale).sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                            )
                                            
                                            // Festival micro dot indicator
                                            if (hasFestival) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(
                                                            if (isSelected) MaterialTheme.colorScheme.background else PersianGold,
                                                            CircleShape
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details for selected day
            Text(
                text = "مناسبت‌های $sDay $monthName",
                color = PersianGold,
                fontSize = (15 * scale).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (dayFestivals.isNotEmpty()) {
                dayFestivals.forEach { festival ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDetailDialog = festival }
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, PersianGold.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = festival.name,
                                    color = Color.White,
                                    fontSize = (15 * scale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = festival.description,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = (12 * scale).sp,
                                    maxLines = 2
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "اطلاعات بیشتر",
                                tint = PersianGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "جشن یا مناسبتی برای این روز ثبت نشده است.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = (12 * scale).sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Traditional Weekday Detail Card (Calculated from selected date)
            val selectedJd = CalendarEngine.solarHijriToJulianDay(sYear, sMonth, sDay)
            val weekdayIdxSel = CalendarEngine.getWeekdayIndex(selectedJd)
            val weekdayNameSel = if (useTraditionalDays) {
                CalendarEngine.traditionalWeekdayNamesPersian[weekdayIdxSel]
            } else {
                CalendarEngine.standardWeekdayNamesPersian[weekdayIdxSel]
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "درباره روز $weekdayNameSel",
                        color = PersianGold,
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    val meaning = when (weekdayIdxSel) {
                        0 -> "«کیوان‌شید» مظهر زحل، بلندمرتبگی و جاودانگی تاریخی ایرانیان است."
                        1 -> "«مهرشید» مظهر خورشید مهربان، سرچشمه نور و دوستی‌های بی‌غل‌وغش است."
                        2 -> "«مهشید» مظهر ماه فروزان، الهام‌بخش صلح و آرزوهای ملایم روزگار است."
                        3 -> "«بهرام‌شید» مظهر مریخ شجاع، مظهر تلاش خستگی‌ناپذیر و اقتدار ملی است."
                        4 -> "«تیرشید» مظهر عطارد خردمند، نماد اندیشه، سخنوری، دبیری و پویایی ذهن است."
                        5 -> "«هرمز‌شید» مظهر مشتری فرزانه، نماد خردورزی، بخشندگی و گشایش امور زندگی است."
                        else -> "«آرام‌شید» (ناهیدشید) آدینه، نماد آسایش خاطر، آرامش خانواده و پیوندهای صمیمانه قلبی است."
                    }
                    Text(
                        text = meaning,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        fontSize = (12 * scale).sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Detailed Day Dialog
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
                        Text(
                            text = "تاریخ رویداد: ${festival.day} ${CalendarEngine.standardMonthNamesPersian[festival.month - 1]}",
                            color = PersianTurquoise,
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * scale).sp
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

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
