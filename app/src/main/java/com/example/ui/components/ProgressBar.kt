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
import com.example.ui.theme.GreenDone

@Composable
fun AnimatedProgressBar(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    color: Color = GreenDone,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.5f)
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp),
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .height(8.dp),
            contentAlignment = androidx.compose.ui.Alignment.CenterStart
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(8.dp),
                shape = RoundedCornerShape(4.dp),
                color = color
            ) {}
        }
    }
}
