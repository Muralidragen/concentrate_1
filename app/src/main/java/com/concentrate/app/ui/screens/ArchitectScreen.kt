package com.concentrate.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrate.app.ai.TheArchitect
import com.concentrate.app.data.preferences.UserPreferences
import com.concentrate.app.ui.theme.AccentAmber
import com.concentrate.app.ui.theme.AccentCyan
import com.concentrate.app.ui.theme.AccentEmerald
import com.concentrate.app.ui.theme.BorderSubtle
import com.concentrate.app.ui.theme.DangerRed
import com.concentrate.app.ui.theme.OledBlack
import com.concentrate.app.ui.theme.SurfaceDark
import com.concentrate.app.ui.theme.TextMuted
import com.concentrate.app.ui.theme.TextPrimary
import com.concentrate.app.ui.theme.TextSecondary

@Composable
fun ArchitectScreen(preferences: UserPreferences) {
    val wallet by preferences.wallet.collectAsState()
    val analysis = TheArchitect.generateDailyBriefing(wallet, passesBought = 2)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(top = 16.dp, bottom = 80.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "THE ARCHITECT",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "AI analytical surveillance & behavioral weakness targeting",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Daily Accountability Briefing
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDark)
                    .border(1.dp, AccentAmber.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "🧠 DAILY DEBRIEFING",
                        color = AccentAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analysis.dailyBriefing,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Bento Grid Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoCard(
                    title = "TOTAL FOCUS",
                    value = "${wallet.totalFocusMinutes / 60}h ${wallet.totalFocusMinutes % 60}m",
                    subtitle = "Surviving Vault sprints",
                    accent = AccentCyan,
                    modifier = Modifier.weight(1f)
                )
                BentoCard(
                    title = "PROBLEMS SOLVED",
                    value = "${wallet.totalProblemsSolved}",
                    subtitle = "Notebook grind output",
                    accent = AccentEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoCard(
                    title = "STREAK MULTIPLIER",
                    value = String.format("%.1fx", wallet.streakMultiplier),
                    subtitle = "${wallet.streakDays} consecutive days",
                    accent = AccentAmber,
                    modifier = Modifier.weight(1f)
                )
                BentoCard(
                    title = "POINTS SLASHED",
                    value = "-${wallet.totalSlashedPoints} P",
                    subtitle = "Burned for indiscipline",
                    accent = DangerRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weakness Radar & Risk Profiling
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "RADAR: WEAKNESS PROFILING",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    WeaknessRow(
                        label = "Peak Relapse Risk Window",
                        value = analysis.highRiskRelapseWindow,
                        isAlert = true
                    )

                    WeaknessRow(
                        label = "Primary Distraction Vector",
                        value = analysis.mostFrequentTriggerApp,
                        isAlert = false
                    )

                    WeaknessRow(
                        label = "Study-to-Break Ratio",
                        value = String.format("%.1f : 1 (Target: 5:1)", analysis.studyToBreakRatio),
                        isAlert = false
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoCard(
    title: String,
    value: String,
    subtitle: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = accent,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun WeaknessRow(
    label: String,
    value: String,
    isAlert: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(
            text = value,
            color = if (isAlert) DangerRed else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
