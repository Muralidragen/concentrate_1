package com.concentrate.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrate.app.data.model.JeeSubject
import com.concentrate.app.ui.theme.BorderSubtle
import com.concentrate.app.ui.theme.SurfaceDark
import com.concentrate.app.ui.theme.SurfaceElevated
import com.concentrate.app.ui.theme.TextMuted
import com.concentrate.app.ui.theme.TextPrimary

@Composable
fun SubjectBadgeSelector(
    selectedSubject: JeeSubject,
    onSubjectSelected: (JeeSubject) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JeeSubject.values().forEach { subject ->
            val isSelected = selectedSubject == subject
            val subjectColor = subject.color

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) SurfaceElevated else SurfaceDark)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) subjectColor else BorderSubtle,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSubjectSelected(subject) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = subject.iconSymbol,
                        fontSize = 12.sp
                    )
                    Text(
                        text = subject.displayName,
                        color = if (isSelected) subjectColor else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
