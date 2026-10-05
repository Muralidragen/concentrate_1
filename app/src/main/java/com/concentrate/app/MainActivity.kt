package com.concentrate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.concentrate.app.data.preferences.UserPreferences
import com.concentrate.app.ui.navigation.BottomNavItems
import com.concentrate.app.ui.navigation.Screen
import com.concentrate.app.ui.screens.ArchitectScreen
import com.concentrate.app.ui.screens.DashboardScreen
import com.concentrate.app.ui.screens.FormulaVaultScreen
import com.concentrate.app.ui.screens.MarketScreen
import com.concentrate.app.ui.screens.PermissionShieldScreen
import com.concentrate.app.ui.theme.AccentAmber
import com.concentrate.app.ui.theme.BorderSubtle
import com.concentrate.app.ui.theme.ConcentrateTheme
import com.concentrate.app.ui.theme.OledBlack
import com.concentrate.app.ui.theme.SurfaceDark
import com.concentrate.app.ui.theme.TextMuted
import com.concentrate.app.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = UserPreferences(this)

        setContent {
            ConcentrateTheme {
                MainAppScaffold(preferences = preferences)
            }
        }
    }
}

@Composable
fun MainAppScaffold(preferences: UserPreferences) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack),
        bottomBar = {
            if (currentRoute != Screen.Permissions.route) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .height(68.dp)
                        .border(1.dp, BorderSubtle)
                ) {
                    BottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) AccentAmber else TextMuted
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    color = if (isSelected) TextPrimary else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    preferences = preferences,
                    onNavigateToPermissions = {
                        navController.navigate(Screen.Permissions.route)
                    }
                )
            }
            composable(Screen.Market.route) {
                MarketScreen(preferences = preferences)
            }
            composable(Screen.Architect.route) {
                ArchitectScreen(preferences = preferences)
            }
            composable(Screen.Formula.route) {
                FormulaVaultScreen()
            }
            composable(Screen.Permissions.route) {
                PermissionShieldScreen(
                    onAllGranted = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
