package com.ten.day.tracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ten.day.tracker.ui.screens.dashboard.DashboardScreen
import com.ten.day.tracker.ui.screens.period.PeriodDetailScreen
import com.ten.day.tracker.ui.screens.settings.SettingsScreen

@Composable
fun TenDayTrackerApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onPeriodClick = { periodId ->
                    navController.navigate("period/$periodId")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("period/{periodId}") { backStackEntry ->
            val periodId = backStackEntry.arguments?.getString("periodId")?.toIntOrNull() ?: 0
            PeriodDetailScreen(
                periodId = periodId,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}