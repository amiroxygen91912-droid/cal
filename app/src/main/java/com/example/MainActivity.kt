package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.CalendarViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.NavyDeep

class MainActivity : ComponentActivity() {

    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permissions for Android 13+
        requestNotificationPermission()

        setContent {
            val themeSetting by viewModel.theme.collectAsState()

            MyApplicationTheme(themeSetting = themeSetting) {
                // Ensure RTL Layout for Farsi UI
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: "today"

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (currentRoute != "search") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.background)
                                        .statusBarsPadding()
                                        .padding(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = when(currentRoute) {
                                                "today" -> "گاهشمار امروز"
                                                "calendar" -> "تقویم شاهنشاهی"
                                                "converter" -> "مبدل تاریخ"
                                                "history" -> "دانشنامه تاریخ ایران"
                                                else -> "تنظیمات تقویم"
                                            },
                                            color = PersianGold,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        IconButton(
                                            onClick = { navController.navigate("search") },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "جستجو",
                                                tint = PersianGold
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = NavyDeep,
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp)),
                                tonalElevation = 8.dp
                            ) {
                                listOf(
                                    Triple("today", "امروز", Icons.Default.Home),
                                    Triple("calendar", "تقویم", Icons.Default.DateRange),
                                    Triple("converter", "تبدیل", Icons.Default.SwapHoriz),
                                    Triple("history", "تاریخ", Icons.Default.AutoStories),
                                    Triple("settings", "تنظیمات", Icons.Default.Settings)
                                ).forEach { (route, label, icon) ->
                                    val isSelected = currentRoute == route
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            if (currentRoute != route) {
                                                navController.navigate(route) {
                                                    popUpTo("today") { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = label,
                                                tint = if (isSelected) PersianGold else Color.White.copy(alpha = 0.4f)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) PersianGold else Color.White.copy(alpha = 0.4f)
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = PersianGold.copy(alpha = 0.15f)
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "today",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("today") {
                                TodayScreen(
                                    viewModel = viewModel,
                                    onNavigateToHistory = { navController.navigate("history") }
                                )
                            }
                            composable("calendar") {
                                CalendarScreen(viewModel = viewModel)
                            }
                            composable("converter") {
                                ConverterScreen(viewModel = viewModel)
                            }
                            composable("history") {
                                HistoryScreen(viewModel = viewModel)
                            }
                            composable("settings") {
                                SettingsScreen(viewModel = viewModel)
                            }
                            composable("search") {
                                SearchScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 101)
            }
        }
    }
}
