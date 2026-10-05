package com.concentrate.app.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrate.app.data.model.SessionMode
import com.concentrate.app.data.preferences.UserPreferences
import com.concentrate.app.service.WardenService
import com.concentrate.app.ui.components.AppPickerSheet
import com.concentrate.app.ui.components.BreathingZenRing
import com.concentrate.app.ui.components.FloatingDock
import com.concentrate.app.ui.components.MasterKillSwitchDialog
import com.concentrate.app.ui.components.ProblemCountDialog
import com.concentrate.app.ui.components.QuickStrikeCards
import com.concentrate.app.ui.components.SubjectBadgeSelector
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
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    preferences: UserPreferences,
    onNavigateToPermissions: () -> Unit
) {
    val context = LocalContext.current
    val isLockdownActive by preferences.isLockdownActive.collectAsState()
    val lockdownExpiresAt by preferences.lockdownExpiresAt.collectAsState()
    val currentMode by preferences.sessionMode.collectAsState()
    val currentSubject by preferences.currentSubject.collectAsState()
    val wallet by preferences.wallet.collectAsState()
    val whitelist by preferences.whitelist.collectAsState()
    val blacklist by preferences.blacklist.collectAsState()
    val activePass by preferences.activePass.collectAsState()

    var selectedMinutes by remember { mutableIntStateOf(25) }
    var remainingSeconds by remember { mutableLongStateOf(25 * 60L) }
    var totalSeconds by remember { mutableLongStateOf(25 * 60L) }

    var showAppPicker by remember { mutableStateOf(false) }
    var showProblemDialog by remember { mutableStateOf(false) }
    var showKillSwitchDialog by remember { mutableStateOf(false) }
    var logoTapCount by remember { mutableIntStateOf(0) }

    // Live countdown ticker
    LaunchedEffect(isLockdownActive, lockdownExpiresAt) {
        if (isLockdownActive) {
            val total = ((lockdownExpiresAt - System.currentTimeMillis()) / 1000L).coerceAtLeast(1L)
            totalSeconds = total
            while (isLockdownActive) {
                val rem = ((lockdownExpiresAt - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
                remainingSeconds = rem
                if (rem <= 0L) {
                    showProblemDialog = true
                    break
                }
                delay(500)
            }
        } else {
            remainingSeconds = selectedMinutes * 60L
            totalSeconds = selectedMinutes * 60L
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Top Header: Streak, Logo (with secret 5-tap kill switch), and Wallet
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceDark)
                    .border(1.dp, AccentAmber.copy(alpha = 0.5f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🔥 ${wallet.streakDays} DAYS",
                    color = AccentAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // App Logo with 5-tap Developer Backdoor
            Text(
                text = "CONCENTRATE",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                modifier = Modifier.clickable {
                    logoTapCount++
                    if (logoTapCount >= 5) {
                        logoTapCount = 0
                        showKillSwitchDialog = true
                    }
                }
            )

            // Wallet Points & Diamonds
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceDark)
                    .border(1.dp, BorderSubtle, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${wallet.points} P",
                    color = AccentEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "💎 ${wallet.diamonds}",
                    color = DiamondBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Active Pass Banner (if user bought a pass)
        if (activePass != null && !activePass!!.isExpired) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentCyan.copy(alpha = 0.15f))
                    .border(1.dp, AccentCyan, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⏳ ACTIVE PASS: ${activePass!!.title.uppercase()}",
                        color = AccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${activePass!!.remainingSeconds / 60}m ${activePass!!.remainingSeconds % 60}s",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 2. Breathing Zen Ring Component
        BreathingZenRing(
            remainingSeconds = remainingSeconds,
            totalSeconds = totalSeconds,
            isActive = isLockdownActive,
            mode = currentMode,
            subject = currentSubject
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 3. Floating Dock (Switch between Vault and Sniper)
        AnimatedVisibility(visible = !isLockdownActive) {
            Column {
                FloatingDock(
                    currentMode = currentMode,
                    onModeSelected = { mode ->
                        preferences.setSessionMode(mode)
                        if (mode == SessionMode.NTA_EXAM) {
                            selectedMinutes = 180
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 4. Quick-Strike Presets
        AnimatedVisibility(visible = !isLockdownActive && currentMode != SessionMode.NTA_EXAM) {
            Column {
                QuickStrikeCards(
                    selectedMinutes = selectedMinutes,
                    onMinutesSelected = { mins ->
                        selectedMinutes = mins
                        remainingSeconds = mins * 60L
                        totalSeconds = mins * 60L
                    }
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // 5. JEE Subject Badge Selector
        AnimatedVisibility(visible = !isLockdownActive) {
            Column {
                SubjectBadgeSelector(
                    selectedSubject = currentSubject,
                    onSubjectSelected = { sub -> preferences.setCurrentSubject(sub) }
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // 6. Whitelist / Blacklist Configuration Button
        AnimatedVisibility(visible = !isLockdownActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .clickable { showAppPicker = true }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (currentMode == SessionMode.VAULT) "WHITELISTED APPS" else "BLACK-LISTED APPS",
                            color = if (currentMode == SessionMode.VAULT) AccentAmber else AccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (currentMode == SessionMode.VAULT) {
                                "${whitelist.size}/4 permitted • Tap to configure"
                            } else {
                                "${blacklist.size} distractions targeted • Tap to configure"
                            },
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "CONFIGURE →",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 7. Primary Action Button (ARM THE VAULT / DEPLOY SNIPER / FINISH)
        val primaryColor = when (currentMode) {
            SessionMode.VAULT -> AccentAmber
            SessionMode.SNIPER -> AccentCyan
            SessionMode.NTA_EXAM -> Color(0xFFBF5AF2)
        }

        Button(
            onClick = {
                if (!isLockdownActive) {
                    preferences.startLockdown(selectedMinutes, currentMode, currentSubject)
                    WardenService.start(context)
                } else {
                    showProblemDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isLockdownActive) DangerRed else primaryColor)
        ) {
            Text(
                text = if (isLockdownActive) "END SESSION & LOG PROBLEMS" else when (currentMode) {
                    SessionMode.VAULT -> "ARM THE VAULT ($selectedMinutes MIN)"
                    SessionMode.SNIPER -> "DEPLOY THE SNIPER ($selectedMinutes MIN)"
                    SessionMode.NTA_EXAM -> "START NTA EXAM SLOT (180 MIN)"
                },
                color = if (isLockdownActive) TextPrimary else OledBlack,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    }

    // Modal Sheets & Dialogs
    if (showAppPicker) {
        AppPickerSheet(
            mode = currentMode,
            whitelist = whitelist,
            blacklist = blacklist,
            onToggleWhitelist = { pkg -> preferences.toggleWhitelistApp(pkg) },
            onToggleBlacklist = { pkg -> preferences.toggleBlacklistApp(pkg) },
            onDismiss = { showAppPicker = false }
        )
    }

    if (showProblemDialog) {
        ProblemCountDialog(
            onDismiss = { showProblemDialog = false },
            onSubmit = { problemsSolved ->
                showProblemDialog = false
                preferences.endLockdown(completedSuccessfully = true, durationMinutes = selectedMinutes, problemsSolved = problemsSolved)
                WardenService.stop(context)
            }
        )
    }

    if (showKillSwitchDialog) {
        MasterKillSwitchDialog(
            onDismiss = { showKillSwitchDialog = false },
            onAbortConfirmed = {
                showKillSwitchDialog = false
                preferences.endLockdown(completedSuccessfully = false)
                WardenService.stop(context)
            }
        )
    }
}
