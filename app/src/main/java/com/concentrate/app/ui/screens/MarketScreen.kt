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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrate.app.ai.TheArchitect
import com.concentrate.app.data.model.ActivePass
import com.concentrate.app.data.model.PassTemplate
import com.concentrate.app.data.preferences.UserPreferences
import com.concentrate.app.ui.theme.AccentAmber
import com.concentrate.app.ui.theme.AccentCyan
import com.concentrate.app.ui.theme.AccentEmerald
import com.concentrate.app.ui.theme.BorderSubtle
import com.concentrate.app.ui.theme.DangerRed
import com.concentrate.app.ui.theme.DiamondBlue
import com.concentrate.app.ui.theme.OledBlack
import com.concentrate.app.ui.theme.SurfaceDark
import com.concentrate.app.ui.theme.SurfaceElevated
import com.concentrate.app.ui.theme.TextMuted
import com.concentrate.app.ui.theme.TextPrimary
import com.concentrate.app.ui.theme.TextSecondary

@Composable
fun MarketScreen(preferences: UserPreferences) {
    val wallet by preferences.wallet.collectAsState()
    val activePass by preferences.activePass.collectAsState()
    var purchaseFeedback by remember { mutableStateOf<String?>(null) }

    val passesBoughtThisWeek = 2
    val inflationMultiplier = TheArchitect.calculateInflation(passesBoughtThisWeek, wallet.totalProblemsSolved)

    val passTemplates = listOf(
        PassTemplate(
            id = "pass_yt_15",
            title = "YouTube Focus Pass",
            targetPackage = "com.google.android.youtube",
            durationMinutes = 15,
            basePointCost = (80 * inflationMultiplier).toInt(),
            diamondCost = 1,
            description = "15 minutes guilt-free YouTube access. Instant Guillotine drop at 00:00."
        ),
        PassTemplate(
            id = "pass_insta_10",
            title = "Instagram Micro-Break",
            targetPackage = "com.instagram.android",
            durationMinutes = 10,
            basePointCost = (100 * inflationMultiplier).toInt(),
            diamondCost = 2,
            description = "10 minutes of Instagram access. Strict countdown enforced."
        ),
        PassTemplate(
            id = "pass_emergency_abort",
            title = "Emergency Abort Pass",
            targetPackage = null,
            durationMinutes = 0,
            basePointCost = (250 * inflationMultiplier).toInt(),
            diamondCost = 4,
            isEmergencyAbort = true,
            description = "Disarms live session instantly without penalty. Extremely costly."
        ),
        PassTemplate(
            id = "pass_custom_20",
            title = "Generic App Pass",
            targetPackage = null,
            durationMinutes = 20,
            basePointCost = (120 * inflationMultiplier).toInt(),
            diamondCost = 2,
            description = "20-minute release window for any single chosen application."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
            .padding(top = 16.dp, bottom = 80.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "THE MARKET",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Exchange Proof-of-Work output for controlled releases",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .border(1.dp, BorderSubtle, CircleShape)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "${wallet.points} P", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "💎 ${wallet.diamonds}", color = DiamondBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Dynamic Inflation Radar Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, if (inflationMultiplier > 1.2f) DangerRed else AccentEmerald, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "MARKET INFLATION INDEX",
                            color = if (inflationMultiplier > 1.2f) DangerRed else AccentEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = String.format("%.2fx", inflationMultiplier),
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (inflationMultiplier > 1.2f) {
                            "⚠️ Prices inflated by ${((inflationMultiplier - 1f) * 100).toInt()}%. Solve more JEE problems to lower market rates."
                        } else {
                            "✓ Normal market rates. High problem-solving output keeps inflation at baseline."
                        },
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (purchaseFeedback != null) {
            item {
                Text(
                    text = purchaseFeedback ?: "",
                    color = AccentCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Text(
                text = "AVAILABLE CONTROLLED RELEASES",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        items(passTemplates) { template ->
            PassCard(
                template = template,
                canAfford = wallet.points >= template.basePointCost && wallet.diamonds >= template.diamondCost,
                isActive = activePass?.templateId == template.id && !activePass!!.isExpired,
                onBuy = {
                    val exp = System.currentTimeMillis() + (template.durationMinutes * 60 * 1000L)
                    val active = ActivePass(
                        templateId = template.id,
                        title = template.title,
                        targetPackage = template.targetPackage,
                        expiresAtTimestamp = exp,
                        durationMinutes = template.durationMinutes
                    )
                    val success = preferences.activatePass(active, template.basePointCost, template.diamondCost)
                    if (success) {
                        purchaseFeedback = "✓ ${template.title} activated! Guillotine drop in ${template.durationMinutes}m."
                    } else {
                        purchaseFeedback = "❌ Insufficient balance. Solve more JEE problems."
                    }
                }
            )
        }
    }
}

@Composable
private fun PassCard(
    template: PassTemplate,
    canAfford: Boolean,
    isActive: Boolean,
    onBuy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .border(1.dp, if (isActive) AccentCyan else BorderSubtle, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = template.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${template.basePointCost} P",
                        color = AccentEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "💎 ${template.diamondCost}",
                        color = DiamondBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = template.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onBuy,
                enabled = canAfford && !isActive,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (template.isEmergencyAbort) DangerRed else AccentAmber,
                    disabledContainerColor = SurfaceElevated
                )
            ) {
                Text(
                    text = if (isActive) "PASS ACTIVATED" else if (canAfford) "PURCHASE & ARM" else "INSUFFICIENT FUNDS",
                    color = if (canAfford && !isActive) OledBlack else TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
