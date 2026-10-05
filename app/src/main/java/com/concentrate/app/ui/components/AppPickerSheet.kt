package com.concentrate.app.ui.components

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrate.app.ai.TheArchitect
import com.concentrate.app.data.model.AppRiskCategory
import com.concentrate.app.data.model.InstalledApp
import com.concentrate.app.data.model.SessionMode
import com.concentrate.app.ui.theme.AccentAmber
import com.concentrate.app.ui.theme.AccentCyan
import com.concentrate.app.ui.theme.BorderSubtle
import com.concentrate.app.ui.theme.DangerRed
import com.concentrate.app.ui.theme.SurfaceDark
import com.concentrate.app.ui.theme.SurfaceElevated
import com.concentrate.app.ui.theme.TextMuted
import com.concentrate.app.ui.theme.TextPrimary
import com.concentrate.app.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerSheet(
    mode: SessionMode,
    whitelist: Set<String>,
    blacklist: Set<String>,
    onToggleWhitelist: (String) -> Boolean,
    onToggleBlacklist: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val appsList = remember { mutableStateListOf<InstalledApp>() }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var warningMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val filtered = packages.filter {
                (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || it.packageName.contains("dialer") || it.packageName.contains("calc")
            }.map { appInfo ->
                val name = pm.getApplicationLabel(appInfo).toString()
                val risk = TheArchitect.classifyApp(appInfo.packageName, name)
                InstalledApp(
                    packageName = appInfo.packageName,
                    appName = name,
                    riskCategory = risk
                )
            }.sortedBy { it.appName }

            withContext(Dispatchers.Main) {
                appsList.clear()
                appsList.addAll(filtered)
                isLoading = false
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (mode == SessionMode.VAULT) "VAULT WHITELIST (MAX 4)" else "SNIPER BLACKLIST",
                        color = if (mode == SessionMode.VAULT) AccentAmber else AccentCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (mode == SessionMode.VAULT) {
                            "Selected: ${whitelist.size} / 4 apps allowed"
                        } else {
                            "Selected: ${blacklist.size} distractions targeted"
                        },
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                }
            }

            if (warningMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = warningMessage ?: "",
                    color = DangerRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search installed applications...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (mode == SessionMode.VAULT) AccentAmber else AccentCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentAmber)
                }
            } else {
                val displayedApps = appsList.filter {
                    it.appName.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedApps, key = { it.packageName }) { app ->
                        val isSelected = if (mode == SessionMode.VAULT) {
                            whitelist.contains(app.packageName)
                        } else {
                            blacklist.contains(app.packageName)
                        }

                        AppRowItem(
                            app = app,
                            isSelected = isSelected,
                            mode = mode,
                            onClick = {
                                if (mode == SessionMode.VAULT) {
                                    val success = onToggleWhitelist(app.packageName)
                                    if (!success) {
                                        warningMessage = "⚠️ HARD LIMIT: The Vault permits only 4 apps."
                                    } else {
                                        warningMessage = null
                                    }
                                } else {
                                    onToggleBlacklist(app.packageName)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppRowItem(
    app: InstalledApp,
    isSelected: Boolean,
    mode: SessionMode,
    onClick: () -> Unit
) {
    val activeColor = if (mode == SessionMode.VAULT) AccentAmber else AccentCyan

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SurfaceElevated else Color.Transparent)
            .border(1.dp, if (isSelected) activeColor else BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.appName,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = when (app.riskCategory) {
                    AppRiskCategory.HIGH_RISK_DISTRACTION -> "⚡ HIGH RISK DISTRACTION"
                    AppRiskCategory.PRODUCTIVITY_TOOL -> "🛠️ PRODUCTIVITY TOOL"
                    AppRiskCategory.SYSTEM_ESSENTIAL -> "🔒 SYSTEM ESSENTIAL"
                },
                color = when (app.riskCategory) {
                    AppRiskCategory.HIGH_RISK_DISTRACTION -> DangerRed
                    AppRiskCategory.PRODUCTIVITY_TOOL -> AccentCyan
                    AppRiskCategory.SYSTEM_ESSENTIAL -> TextMuted
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) activeColor else Color.Transparent)
                .border(2.dp, if (isSelected) activeColor else BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Text(text = "✓", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
