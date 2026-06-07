package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedProgressBar(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    color: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    backgroundColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp),
        shape = RoundedCornerShape(3.dp),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .height(6.dp),
            contentAlignment = androidx.compose.ui.Alignment.CenterStart
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(6.dp),
                shape = RoundedCornerShape(3.dp),
                color = color
            ) {}
        }
    }
}
