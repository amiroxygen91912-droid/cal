package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.CalendarEngine
import com.example.ui.CalendarViewModel
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val conversionState by viewModel.conversionState.collectAsState()
        val fontSizeSetting by viewModel.fontSize.collectAsState()

        val scale = when (fontSizeSetting) {
            "small" -> 0.85f
            "large" -> 1.15f
            else -> 1.0f
        }

        var yearInput by remember { mutableStateOf(conversionState.year) }
        var selectedMonth by remember { mutableStateOf(conversionState.month) }
        var selectedDay by remember { mutableStateOf(conversionState.day) }

        var monthExpanded by remember { mutableStateOf(false) }
        var dayExpanded by remember { mutableStateOf(false) }

        // Trigger conversion when input changes
        LaunchedEffect(yearInput, selectedMonth, selectedDay) {
            viewModel.updateConversionDateInput(yearInput, selectedMonth, selectedDay)
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Text(
                text = "تبدیل تاریخ آفلاین",
                color = PersianGold,
                fontSize = (22 * scale).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "تبدیل دقیق و بدون نیاز به اینترنت میان سیستم‌های زمانی",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = (11 * scale).sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Source Calendar Selector (Recipe 15 Tab system)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تقویم مبدأ را انتخاب کنید:",
                        color = Color.White,
                        fontSize = (13 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            Triple("imperial", "شاهنشاهی", "2585"),
                            Triple("hijri", "خورشیدی", "1405"),
                            Triple("gregorian", "میلادی", "2026")
                        ).forEach { (systemKey, label, defaultYear) ->
                            val isSelected = (conversionState.sourceSystem == systemKey)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) PersianGold else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.updateConversionSource(systemKey)
                                        yearInput = defaultYear
                                        selectedMonth = 1
                                        selectedDay = 1
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) MaterialTheme.colorScheme.background else Color.White,
                                    fontSize = (12 * scale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Inputs Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, PersianGold.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Year Input
                        OutlinedTextField(
                            value = yearInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    yearInput = it
                                }
                            },
                            label = { Text("سال", color = PersianGold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                             colors = TextFieldDefaults.colors(
                                 focusedContainerColor = Color.Transparent,
                                 unfocusedContainerColor = Color.Transparent,
                                 focusedIndicatorColor = PersianGold,
                                 unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                 focusedTextColor = Color.White,
                                 unfocusedTextColor = Color.White
                             ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        // Month Input
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .height(56.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { monthExpanded = true }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text("ماه", color = PersianGold, fontSize = 10.sp)
                                val monthLabel = if (conversionState.sourceSystem == "gregorian") {
                                    listOf("ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن", "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر")[selectedMonth - 1]
                                } else {
                                    CalendarEngine.standardMonthNamesPersian[selectedMonth - 1]
                                }
                                Text(monthLabel, color = Color.White, fontSize = (14 * scale).sp, fontWeight = FontWeight.SemiBold)
                            }

                            DropdownMenu(
                                expanded = monthExpanded,
                                onDismissRequest = { monthExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                val monthList = if (conversionState.sourceSystem == "gregorian") {
                                    listOf("ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن", "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر")
                                } else {
                                    CalendarEngine.standardMonthNamesPersian
                                }
                                monthList.forEachIndexed { index, mName ->
                                    DropdownMenuItem(
                                        text = { Text(mName, color = Color.White) },
                                        onClick = {
                                            selectedMonth = index + 1
                                            monthExpanded = false
                                            // Handle day truncation if current day is > max days of new month
                                            val maxDay = if (conversionState.sourceSystem == "gregorian") {
                                                listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)[index]
                                            } else {
                                                CalendarEngine.getSolarHijriMonthLength(yearInput.toIntOrNull() ?: 1405, index + 1)
                                            }
                                            if (selectedDay > maxDay) selectedDay = maxDay
                                        }
                                    )
                                }
                            }
                        }

                        // Day Input
                        Box(
                            modifier = Modifier
                                .weight(0.8f)
                                .height(56.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { dayExpanded = true }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text("روز", color = PersianGold, fontSize = 10.sp)
                                Text("$selectedDay", color = Color.White, fontSize = (14 * scale).sp, fontWeight = FontWeight.SemiBold)
                            }

                            DropdownMenu(
                                expanded = dayExpanded,
                                onDismissRequest = { dayExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                val maxDays = if (conversionState.sourceSystem == "gregorian") {
                                    listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)[selectedMonth - 1]
                                } else {
                                    CalendarEngine.getSolarHijriMonthLength(yearInput.toIntOrNull() ?: 1405, selectedMonth)
                                }
                                (1..maxDays).forEach { dayVal ->
                                    DropdownMenuItem(
                                        text = { Text("$dayVal", color = Color.White) },
                                        onClick = {
                                            selectedDay = dayVal
                                            dayExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Conversion Results Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "نتایج تبدیل",
                    tint = PersianGold,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "نتایج همزمان تبدیل سیستم‌های زمانی",
                    color = PersianGold,
                    fontSize = (14 * scale).sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Results Display Stack (3 systems displayed simultaneously!)
            listOf(
                Triple("شاهنشاهی ایران", conversionState.resultImperial, PersianGold),
                Triple("هجری خورشیدی", conversionState.resultHijri, PersianTurquoise),
                Triple("میلادی گرگورین", conversionState.resultGregorian, Color.White)
            ).forEach { (title, value, accentColor) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = (11 * scale).sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (value.isNotEmpty()) value else "—",
                                color = accentColor,
                                fontSize = (18 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "تقویم",
                            tint = accentColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
