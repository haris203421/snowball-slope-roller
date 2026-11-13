package com.flurry.snowballslope.presentation.decorate

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.core.di.AppViewModelFactory
import com.flurry.snowballslope.core.theme.FrostSurfaceDeep
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.core.theme.SlateText
import com.flurry.snowballslope.core.theme.SnowBackground
import com.flurry.snowballslope.core.theme.SnowBackgroundDeep
import com.flurry.snowballslope.core.ui.PrimaryButton
import com.flurry.snowballslope.core.ui.SecondaryButton
import com.flurry.snowballslope.core.ui.SnowmanPreview
import com.flurry.snowballslope.domain.model.DecorationKind
import kotlin.math.roundToInt

@Composable
fun DecorateRoute(
    hillId: Int,
    onPhoto: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val decorateViewModel: DecorateViewModel = viewModel(factory = AppViewModelFactory())
    val state by decorateViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(hillId) {
        decorateViewModel.load(hillId)
    }

    DecorateScreen(
        state = state,
        onDrop = decorateViewModel::drop,
        onClear = decorateViewModel::clear,
        onPhoto = { onPhoto(state.hillId) },
        onMenu = onMenu
    )
}

@Composable
private fun DecorateScreen(
    state: DecorateUiState,
    onDrop: (DecorationKind, Float, Float) -> Unit,
    onClear: () -> Unit,
    onPhoto: () -> Unit,
    onMenu: () -> Unit
) {
    var stageRect by remember { mutableStateOf(Rect.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SnowBackground.copy(alpha = 0.94f),
                            SnowBackgroundDeep.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.decorate),
                style = MaterialTheme.typography.headlineMedium,
                color = NightSlope
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = state.hillName,
                style = MaterialTheme.typography.labelLarge,
                color = SlateText
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                SnowmanPreview(
                    snowman = state.snowman,
                    anchors = state.anchors,
                    height = 240.dp,
                    contentDescription = stringResource(R.string.cd_snowman),
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        stageRect = coordinates.boundsInRoot()
                    }
                )
            }

            if (state.tray.isEmpty()) {
                Text(
                    text = stringResource(R.string.tray_empty),
                    style = MaterialTheme.typography.labelLarge,
                    color = SlateText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.tray.forEach { entry ->
                        TrayTile(
                            kind = entry.kind,
                            count = entry.count,
                            onDropped = { center ->
                                if (stageRect.width > 0f && stageRect.height > 0f) {
                                    val normX = (center.x - stageRect.left) / stageRect.width
                                    val normY = (center.y - stageRect.top) / stageRect.height
                                    onDrop(entry.kind, normX, normY)
                                }
                            }
                        )
                    }
                }
            }

            PrimaryButton(
                text = stringResource(R.string.save_photo),
                onClick = onPhoto,
                enabled = state.canSave
            )
            Spacer(modifier = Modifier.height(8.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu
            )
            TextButton(onClick = onClear, enabled = state.canClear) {
                Text(
                    text = stringResource(R.string.clear),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (state.canClear) ScarfRed else SlateText
                )
            }
        }
    }
}

@Composable
private fun TrayTile(
    kind: DecorationKind,
    count: Int,
    onDropped: (Offset) -> Unit
) {
    var origin by remember { mutableStateOf(Offset.Zero) }
    var drag by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }
    val lift by animateFloatAsState(
        targetValue = if (dragging) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "lift"
    )
    val tile = GameConfig.DECORATION_TILE_DP.dp
    val halfPx = with(LocalDensity.current) { tile.toPx() } / 2f

    Box(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                if (!dragging) origin = coordinates.positionInRoot()
            }
            .offset { IntOffset(drag.x.roundToInt(), drag.y.roundToInt()) }
            .zIndex(if (dragging) 4f else 0f)
            .scale(lift)
            .pointerInput(kind) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDrag = { change, amount ->
                        change.consume()
                        drag += amount
                    },
                    onDragEnd = {
                        onDropped(
                            Offset(origin.x + drag.x + halfPx, origin.y + drag.y + halfPx)
                        )
                        drag = Offset.Zero
                        dragging = false
                    },
                    onDragCancel = {
                        drag = Offset.Zero
                        dragging = false
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier.size(tile),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, FrostSurfaceDeep),
            colors = CardDefaults.cardColors(containerColor = PureSnow),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (dragging) 10.dp else 4.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (kind) {
                    DecorationKind.Scarf -> Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = stringResource(R.string.cd_scarf),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )

                    DecorationKind.Hat -> Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_hat),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )

                    DecorationKind.Button -> Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(ScarfRed, CircleShape)
                    )
                }
                Text(
                    text = stringResource(R.string.count_value, count),
                    style = MaterialTheme.typography.labelMedium,
                    color = NightSlope,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                )
            }
        }
    }
}
