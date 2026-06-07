package com.example.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NotionGreenLight
import com.example.ui.theme.NotionGreenDark
import com.example.ui.theme.NotionYellowLight
import com.example.ui.theme.NotionYellowDark
import com.example.ui.theme.NotionRedLight
import com.example.ui.theme.NotionRedDark

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    isComplete: Boolean = false,
    isUrgent: Boolean = false
) {
    // Dynamically calculate if theme is dark based on current theme background luminance.
    // Highly resilient approach regardless of user custom scheme transitions.
    val isDark = MaterialTheme.colorScheme.background.let {
        it.red * 0.2126f + it.green * 0.7152f + it.blue * 0.0722f < 0.5f
    }

    val activeGreen = if (isDark) NotionGreenDark else NotionGreenLight
    val activeRed = if (isDark) NotionRedDark else NotionRedLight
    val activeOrange = if (isDark) NotionYellowDark else NotionYellowLight
    val activeGrey = if (isDark) Color(0xFF9E9E9E) else Color(0xFF5F5E59)

    val backgroundColor = when {
        isComplete -> activeGreen.copy(alpha = 0.15f)
        isUrgent -> activeRed.copy(alpha = 0.15f)
        status == "در انتظار" -> activeOrange.copy(alpha = 0.15f)
        else -> activeGrey.copy(alpha = 0.15f)
    }
    
    val contentColor = when {
        isComplete -> activeGreen
        isUrgent -> activeRed
        status == "در انتظار" -> activeOrange
        else -> activeGrey
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}
