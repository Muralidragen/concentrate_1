package com.concentrate.app.data.model

enum class JeeSubject(
    val displayName: String,
    val hexColor: Long,
    val iconSymbol: String
) {
    PHYSICS(
        displayName = "Physics",
        hexColor = 0xFF64D2FF, // Cyan
        iconSymbol = "⚡"
    ),
    CHEMISTRY(
        displayName = "Chemistry",
        hexColor = 0xFFFF9F0A, // Amber
        iconSymbol = "⚗️"
    ),
    MATHEMATICS(
        displayName = "Mathematics",
        hexColor = 0xFF30D158, // Emerald
        iconSymbol = "📐"
    ),
    MOCK_TEST(
        displayName = "Mock Test",
        hexColor = 0xFFBF5AF2, // Purple
        iconSymbol = "🎯"
    )
}
