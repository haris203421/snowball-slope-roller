package com.flurry.snowballslope.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.flurry.snowballslope.core.theme.PureSnow
import kotlin.math.sin
import kotlin.random.Random

private data class Flake(
    val startX: Float,
    val phase: Float,
    val radius: Float,
    val alpha: Float,
    val sway: Float
)

@Composable
fun SnowfallCanvas(
    modifier: Modifier = Modifier,
    flakeCount: Int = 18,
    speedMillis: Int = 5200,
    color: Color = PureSnow
) {
    val flakes = remember(flakeCount) {
        val random = Random(flakeCount * 31)
        List(flakeCount) {
            Flake(
                startX = random.nextFloat(),
                phase = random.nextFloat(),
                radius = 3f + random.nextFloat() * 4f,
                alpha = 0.25f + random.nextFloat() * 0.35f,
                sway = 0.4f + random.nextFloat() * 1.6f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "snowfall")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = speedMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    )

    Canvas(modifier = modifier) {
        flakes.forEach { flake ->
            val travel = (drift + flake.phase) % 1f
            val y = travel * (size.height + 80f) - 40f
            val swayPx = sin((travel + flake.phase) * 6.28f * flake.sway) * 14f
            drawCircle(
                color = color.copy(alpha = flake.alpha),
                radius = flake.radius,
                center = Offset(flake.startX * size.width + swayPx, y)
            )
        }
    }
}
