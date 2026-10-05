package com.concentrate.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Warden", Icons.Default.Timer)
    object Market : Screen("market", "Market", Icons.Default.ShoppingCart)
    object Architect : Screen("architect", "Architect", Icons.Default.AutoAwesome)
    object Formula : Screen("formula", "Formulas", Icons.Default.MenuBook)
    object Permissions : Screen("permissions", "Shields", Icons.Default.Shield)
}

val BottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Market,
    Screen.Architect,
    Screen.Formula
)
