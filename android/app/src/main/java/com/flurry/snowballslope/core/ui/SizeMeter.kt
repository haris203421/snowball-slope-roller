package com.flurry.snowballslope.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.theme.FrostSurfaceDeep
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.IceEdge
import com.flurry.snowballslope.core.theme.PureSnow

@Composable
fun SizeMeter(
    fraction: Float,
    thresholds: List<Float>,
    partName: String,
    modifier: Modifier = Modifier
) {
    val clamped = fraction.coerceIn(0f, 1f)
    BoxWithConstraints(
        modifier = modifier
            .width(200.dp)
            .height(26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(PureSnow.copy(alpha = 0.85f))
            .border(BorderStroke(1.dp, FrostSurfaceDeep), RoundedCornerShape(13.dp))
            .semantics {
                stateDescription = partName
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        val track = maxWidth
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(track * clamped)
                .background(
                    Brush.horizontalGradient(listOf(IceEdge, GlacierPrimary))
                )
        )
        thresholds.forEach { mark ->
            Box(
                modifier = Modifier
                    .offset(x = track * mark.coerceIn(0f, 1f))
                    .width(2.dp)
                    .fillMaxHeight()
                    .padding(vertical = 7.dp)
                    .background(FrostSurfaceDeep)
            )
        }
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (track - 22.dp) * clamped + 1.dp)
                .size(20.dp)
        )
    }
}
