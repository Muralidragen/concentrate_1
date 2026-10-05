package com.concentrate.app.data.model

enum class AppRiskCategory {
    HIGH_RISK_DISTRACTION, // Social media, games, short-form video
    PRODUCTIVITY_TOOL,     // Calculator, notes, NCERT viewer, dictionary
    SYSTEM_ESSENTIAL       // Dialer, settings, launcher
}

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val riskCategory: AppRiskCategory = AppRiskCategory.HIGH_RISK_DISTRACTION,
    val isWhitelisted: Boolean = false,
    val isBlacklisted: Boolean = false
)
