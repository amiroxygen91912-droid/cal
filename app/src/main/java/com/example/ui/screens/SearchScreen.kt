package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.CalendarEngine
import com.example.database.FestivalEntity
import com.example.database.HistoryTopicEntity
import com.example.ui.CalendarViewModel
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val query by viewModel.searchQuery.collectAsState()
        val festivalResults by viewModel.searchFestivalResults.collectAsState(initial = emptyList())
        val historyResults by viewModel.searchHistoryResults.collectAsState(initial = emptyList())
        val fontSizeSetting by viewModel.fontSize.collectAsState()

        val scale = when (fontSizeSetting) {
            "small" -> 0.85f
            "large" -> 1.15f
            else -> 1.0f
        }

        var showFestivalDetail by remember { mutableStateOf<FestivalEntity?>(null) }
        var showHistoryDetail by remember { mutableStateOf<HistoryTopicEntity?>(null) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Text(
                text = "جستجوی جامع آفلاین",
                color = PersianGold,
                fontSize = (22 * scale).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "جستجوی سریع میان مناسبت‌ها، روزها، شاهان و رویدادهای تاریخی بدون نیاز به اینترنت",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = (11 * scale).sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Search Bar Input
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("نام جشن، پادشاه، سلسله یا نماد را وارد کنید...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "جستجو", tint = PersianGold) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = PersianGold,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            if (query.isEmpty()) {
                // Initial informational screen
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "جستجو کنید",
                            color = PersianGold,
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "می‌توانید کلماتی چون «نوروز»، «تیرگان»، «کوروش»، «لوتوس»، «هخامنشی» و یا نام ماه‌ها را بنویسید تا نتایج فوراً نمایان شوند.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = (12 * scale).sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                // Show categorized results
                // Category 1: Festivals
                if (festivalResults.isNotEmpty()) {
                    Text(
                        text = "جشن‌ها و مناسبت‌های ملی (${festivalResults.size})",
                        color = PersianGold,
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                    )

                    festivalResults.forEach { festival ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showFestivalDetail = festival }
                                .padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
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
                                        text = "${festival.day} ${CalendarEngine.standardMonthNamesPersian[festival.month - 1]}",
                                        color = PersianTurquoise,
                                        fontSize = (11 * scale).sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = festival.description,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = (12 * scale).sp,
                                        maxLines = 1
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "مشاهده",
                                    tint = PersianGold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category 2: History References
                if (historyResults.isNotEmpty()) {
                    Text(
                        text = "دانشنامه و تاریخ باستان (${historyResults.size})",
                        color = PersianGold,
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    historyResults.forEach { topic ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showHistoryDetail = topic }
                                .padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = topic.title,
                                        color = Color.White,
                                        fontSize = (15 * scale).sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = topic.summary,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = (12 * scale).sp,
                                        maxLines = 1
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "مشاهده",
                                    tint = PersianGold
                                )
                            }
                        }
                    }
                }

                if (festivalResults.isEmpty() && historyResults.isEmpty()) {
                    // No Results
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "نتیجه‌ای یافت نشد. لطفاً واژه‌های دیگری جستجو کنید.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = (12 * scale).sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Festival Detail Dialog
        if (showFestivalDetail != null) {
            val festival = showFestivalDetail!!
            AlertDialog(
                onDismissRequest = { showFestivalDetail = null },
                confirmButton = {
                    TextButton(
                        onClick = { showFestivalDetail = null },
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

        // History Detail Dialog
        if (showHistoryDetail != null) {
            val topic = showHistoryDetail!!
            AlertDialog(
                onDismissRequest = { showHistoryDetail = null },
                confirmButton = {
                    TextButton(
                        onClick = { showHistoryDetail = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = PersianGold)
                    ) {
                        Text("بستن", fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = topic.title,
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PersianTurquoise.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = topic.summary,
                                color = PersianTurquoise,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = (12 * scale).sp,
                                lineHeight = 18.sp
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Text(
                            text = "شرح تاریخی کامل:",
                            color = PersianGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * scale).sp
                        )
                        Text(
                            text = topic.content,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            fontSize = (13 * scale).sp,
                            lineHeight = 22.sp
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }
}
