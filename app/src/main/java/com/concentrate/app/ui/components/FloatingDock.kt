package com.concentrate.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrate.app.data.model.SessionMode
import com.concentrate.app.ui.theme.AccentAmber
import com.concentrate.app.ui.theme.AccentCyan
import com.concentrate.app.ui.theme.BorderSubtle
import com.concentrate.app.ui.theme.SurfaceDark
import com.concentrate.app.ui.theme.SurfaceElevated
import com.concentrate.app.ui.theme.TextMuted
import com.concentrate.app.ui.theme.TextPrimary

@Composable
fun FloatingDock(
    currentMode: SessionMode,
    onModeSelected: (SessionMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(CircleShape)
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle, CircleShape)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockItem(
                title = "VAULT",
                isSelected = currentMode == SessionMode.VAULT,
                activeColor = AccentAmber,
                onClick = { onModeSelected(SessionMode.VAULT) }
            )
            DockItem(
                title = "SNIPER",
                isSelected = currentMode == SessionMode.SNIPER,
                activeColor = AccentCyan,
                onClick = { onModeSelected(SessionMode.SNIPER) }
            )
            DockItem(
                title = "NTA EXAM",
                isSelected = currentMode == SessionMode.NTA_EXAM,
                activeColor = AccentPurple,
                onClick = { onModeSelected(SessionMode.NTA_EXAM) }
            )
        }
    }
}

@Composable
private fun DockItem(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) SurfaceElevated else Color.Transparent,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "dockItemBg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else TextMuted,
        label = "dockItemTextColor"
    )

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}
