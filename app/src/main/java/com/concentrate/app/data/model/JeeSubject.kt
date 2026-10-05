package com.concentrate.app.data.model

import androidx.compose.ui.graphics.Color
import com.concentrate.app.ui.theme.AccentAmber
import com.concentrate.app.ui.theme.AccentCyan
import com.concentrate.app.ui.theme.AccentEmerald
import com.concentrate.app.ui.theme.AccentPurple

enum class JeeSubject(
    val displayName: String,
    val color: Color,
    val iconSymbol: String
) {
    PHYSICS(
        displayName = "Physics",
        color = AccentCyan,
        iconSymbol = "⚡"
    ),
    CHEMISTRY(
        displayName = "Chemistry",
        color = AccentAmber,
        iconSymbol = "⚗️"
    ),
    MATHEMATICS(
        displayName = "Mathematics",
        color = AccentEmerald,
        iconSymbol = "📐"
    ),
    MOCK_TEST(
        displayName = "Mock Test",
        color = AccentPurple,
        iconSymbol = "🎯"
    )
}
