package com.example.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenDone
import com.example.ui.theme.GreyFuture
import com.example.ui.theme.OrangePending
import com.example.ui.theme.RedUrgent

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    isComplete: Boolean = false,
    isUrgent: Boolean = false
) {
    val backgroundColor = when {
        isComplete -> GreenDone.copy(alpha = 0.2f)
        isUrgent -> RedUrgent.copy(alpha = 0.2f)
        status == "در انتظار" -> OrangePending.copy(alpha = 0.2f)
        else -> GreyFuture.copy(alpha = 0.2f)
    }
    
    val contentColor = when {
        isComplete -> GreenDone
        isUrgent -> RedUrgent
        status == "در انتظار" -> OrangePending
        else -> Color.DarkGray
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
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
