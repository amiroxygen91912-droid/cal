package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.HistoryTopicEntity
import com.example.ui.CalendarViewModel
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val fontSizeSetting by viewModel.fontSize.collectAsState()

        val scale = when (fontSizeSetting) {
            "small" -> 0.85f
            "large" -> 1.15f
            else -> 1.0f
        }

        var activeTab by remember { mutableStateOf(0) } // 0=Empires, 1=Kings, 2=Symbols

        // Collect topics
        val achaemenidTopics by viewModel.achaemenidTopics.collectAsState(initial = emptyList())
        val parthianTopics by viewModel.parthianTopics.collectAsState(initial = emptyList())
        val sasanianTopics by viewModel.sasanianTopics.collectAsState(initial = emptyList())
        val kingsTopics by viewModel.kingsTopics.collectAsState(initial = emptyList())
        val symbolsTopics by viewModel.symbolsTopics.collectAsState(initial = emptyList())

        var showDetailDialog by remember { mutableStateOf<HistoryTopicEntity?>(null) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Text(
                text = "دایرةالمعارف تاریخ ایران باستان",
                color = PersianGold,
                fontSize = (22 * scale).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "دانشنامه آفلاین امپراتوری‌ها، مفاخر و نمادهای دوران شکوه ملی",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = (11 * scale).sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Tabs Row (Segmented style)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("شاهنشاهی‌ها", "پادشاهان و مفاخر", "نمادهای ملی").forEachIndexed { index, label ->
                        val isSelected = (activeTab == index)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) PersianGold else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                        .clickable { activeTab = index }
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

            Spacer(modifier = Modifier.height(24.dp))

            // Topics list based on selected tab
            val activeTopics = when (activeTab) {
                0 -> achaemenidTopics + parthianTopics + sasanianTopics
                1 -> kingsTopics
                else -> symbolsTopics
            }

            activeTopics.forEach { topic ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDetailDialog = topic }
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        PersianBlue.copy(alpha = 0.1f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Visual Emblem
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PersianGold.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when(topic.era) {
                                                "symbols" -> Icons.Default.Star
                                                "kings" -> Icons.Default.HistoryEdu
                                                else -> Icons.Default.AutoStories
                                            },
                                            contentDescription = "نماد",
                                            tint = PersianGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = topic.title,
                                        color = Color.White,
                                        fontSize = (16 * scale).sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Serif
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "باز کردن",
                                    tint = PersianGold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = topic.summary,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = (13 * scale).sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Full Detailed History Dialog
        if (showDetailDialog != null) {
            val topic = showDetailDialog!!
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
