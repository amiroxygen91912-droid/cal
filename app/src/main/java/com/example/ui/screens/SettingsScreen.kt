package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CalendarViewModel
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val themeSetting by viewModel.theme.collectAsState()
        val useTraditionalDays by viewModel.useTraditionalDays.collectAsState()
        val useHistoricalMonths by viewModel.useHistoricalMonths.collectAsState()
        val fontSizeSetting by viewModel.fontSize.collectAsState()

        val scale = when (fontSizeSetting) {
            "small" -> 0.85f
            "large" -> 1.15f
            else -> 1.0f
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
                text = "تنظیمات تقویم شاهنشاهی",
                color = PersianGold,
                fontSize = (22 * scale).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "شخصی‌سازی زبان، نمایش، پوسته و رویدادهای اعلان تقویم باستانی",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = (11 * scale).sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // General Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "تنظیمات عمومی",
                        color = PersianGold,
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Theme Selector Row
                    SettingsItemRow(
                        title = "پوسته نرم‌افزار",
                        subtitle = "تغییر حالت بین تیره، روشن و هماهنگ با سیستم",
                        icon = Icons.Default.Palette
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(2.dp)
                        ) {
                            listOf("auto" to "خودکار", "light" to "روشن", "dark" to "تیره").forEach { (key, label) ->
                                val isSelected = (themeSetting == key)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) PersianGold else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { viewModel.updateTheme(key) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) MaterialTheme.colorScheme.background else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                    // Font Size Selector Row
                    SettingsItemRow(
                        title = "اندازه قلم",
                        subtitle = "تنظیم میزان خوانایی متن‌های دایرةالمعارف و تاریخ",
                        icon = Icons.Default.FormatSize
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(2.dp)
                        ) {
                            listOf("small" to "کوچک", "medium" to "متوسط", "large" to "بزرگ").forEach { (key, label) ->
                                val isSelected = (fontSizeSetting == key)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) PersianGold else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { viewModel.updateFontSize(key) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) MaterialTheme.colorScheme.background else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                    // Traditional Weekdays Switch
                    SettingsItemRow(
                        title = "نام‌های باستانی روزها",
                        subtitle = "نمایش روزها به صورت کیوان‌شید، مهرشید، مهشید و...",
                        icon = Icons.Default.History
                    ) {
                        Switch(
                            checked = useTraditionalDays,
                            onCheckedChange = { viewModel.updateTraditionalDays(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.background,
                                checkedTrackColor = PersianGold,
                                uncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                    // Avestan Month Switch
                    SettingsItemRow(
                        title = "نام‌های باستانی اوستایی ماه‌ها",
                        subtitle = "نمایش ماه‌ها به صورت فرورتی، اردوهشت و فلسفه آنها",
                        icon = Icons.Default.AutoStories
                    ) {
                        Switch(
                            checked = useHistoricalMonths,
                            onCheckedChange = { viewModel.updateHistoricalMonths(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.background,
                                checkedTrackColor = PersianGold,
                                uncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }

            // Notification preferences card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "یادآور جشن‌ها و مناسبت‌های ملی",
                        color = PersianGold,
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    listOf(
                        "Nowruz" to "جشن نوروز باستانی",
                        "Mehregan" to "جشن پاییزه مهرگان",
                        "Tirgan" to "جشن آب‌پاشان تیرگان",
                        "Yalda" to "جشن شب چله (یلدا)",
                        "Sadeh" to "جشن پیدایش آتش سده",
                        "Sepandarmazgan" to "جشن زمین و روز عشق اسفندگان"
                    ).forEachIndexed { index, (key, label) ->
                        var isEnabled by remember { mutableStateOf(viewModel.isNotificationEnabled(key)) }
                        
                        SettingsItemRow(
                            title = label,
                            subtitle = "یادآوری و اعلان کاملا آفلاین مناسبت در موعد مقرر",
                            icon = Icons.Default.NotificationsActive
                        ) {
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = {
                                    isEnabled = it
                                    viewModel.toggleNotification(key, it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.background,
                                    checkedTrackColor = PersianGold,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                )
                            )
                        }

                        if (index < 5) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItemRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PersianGold,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
        control()
    }
}
