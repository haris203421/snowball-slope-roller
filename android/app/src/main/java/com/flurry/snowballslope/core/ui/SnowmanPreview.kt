package com.flurry.snowballslope.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.domain.model.DecorationItem
import com.flurry.snowballslope.domain.model.DecorationKind
import com.flurry.snowballslope.domain.model.SnowAnchor
import com.flurry.snowballslope.domain.model.Snowman

@Composable
fun SnowmanPreview(
    snowman: Snowman?,
    anchors: List<SnowAnchor>,
    height: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    showMound: Boolean = true,
    extraDecorations: List<DecorationItem> = emptyList()
) {
    BoxWithConstraints(
        modifier = modifier
            .size(width = height * 0.72f, height = height),
        contentAlignment = Alignment.BottomCenter
    ) {
        val stageWidth = maxWidth
        val stageHeight = maxHeight
        val base = snowman?.baseDp ?: 72f
        val middle = snowman?.middleDp ?: 54f
        val head = snowman?.headDp ?: 38f
        val total = (base + middle + head).coerceAtLeast(1f)
        val usable = stageHeight * 0.88f
        val baseSize = usable * (base / total)
        val middleSize = usable * (middle / total)
        val headSize = usable * (head / total)

        if (showMound) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(width = stageWidth * 0.86f, height = stageHeight * 0.08f)
                    .background(PureSnow.copy(alpha = 0.85f), CircleShape)
            )
        }

        val baseBottom = stageHeight * 0.05f
        val middleBottom = baseBottom + baseSize * 0.82f
        val headBottom = middleBottom + middleSize * 0.82f

        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -baseBottom)
                .size(baseSize)
        )
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -middleBottom)
                .size(middleSize)
        )
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -headBottom)
                .size(headSize)
        )

        val placed = (snowman?.decorations.orEmpty() + extraDecorations)
        placed.forEach { item ->
            val anchor = anchors.firstOrNull { it.id == item.anchorId } ?: return@forEach
            val tile = when (item.kind) {
                DecorationKind.Scarf -> middleSize * 0.78f
                DecorationKind.Hat -> headSize * 0.92f
                DecorationKind.Button -> headSize * 0.2f
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        x = stageWidth * anchor.x - tile / 2f,
                        y = stageHeight * anchor.y - tile / 2f
                    )
                    .size(tile),
                contentAlignment = Alignment.Center
            ) {
                when (item.kind) {
                    DecorationKind.Scarf -> Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(tile)
                    )

                    DecorationKind.Hat -> Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(tile)
                    )

                    DecorationKind.Button -> Box(
                        modifier = Modifier
                            .size(tile)
                            .background(ScarfRed, CircleShape)
                    )
                }
            }
        }
    }
}
