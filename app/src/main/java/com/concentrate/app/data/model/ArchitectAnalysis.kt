package com.concentrate.app.data.model

data class HourlyWeakness(
    val hourOfDay: Int, // 0 to 23
    val failureCount: Int,
    val primaryTriggerApp: String? = null
)

data class ArchitectAnalysis(
    val dailyBriefing: String,
    val currentInflationMultiplier: Float = 1.0f,
    val passesBoughtThisWeek: Int = 1,
    val problemsSolvedThisWeek: Int = 42,
    val highRiskRelapseWindow: String = "22:00 - 23:30 (Post-Dinner Slump)",
    val mostFrequentTriggerApp: String = "Instagram Reels",
    val studyToBreakRatio: Float = 4.2f
)
