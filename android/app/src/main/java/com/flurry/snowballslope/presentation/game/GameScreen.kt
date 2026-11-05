package com.flurry.snowballslope.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.core.di.AppViewModelFactory
import com.flurry.snowballslope.core.di.ScreenArgs
import com.flurry.snowballslope.core.theme.FrostSurface
import com.flurry.snowballslope.core.theme.FrostSurfaceDeep
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.ui.SizeMeter
import com.flurry.snowballslope.presentation.dialog.PauseDialog
import com.flurry.snowballslope.domain.model.ControlMode
import com.flurry.snowballslope.domain.model.ObstacleKind
import com.flurry.snowballslope.domain.model.SnowmanPart
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameRoute(
    hillId: Int,
    token: Long,
    onFinished: (GameResult) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(
        key = "game-$hillId-$token",
        factory = AppViewModelFactory(ScreenArgs(hillId = hillId))
    )
    val state by gameViewModel.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                GameEvent.Bump -> haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                is GameEvent.Finished -> onFinished(event.result)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameScreen(
        state = state,
        onFieldMeasured = gameViewModel::onFieldMeasured,
        onDrag = gameViewModel::onDrag,
        onPause = gameViewModel::pause,
        onResume = gameViewModel::resume,
        onMenu = onMenu
    )
}

@Composable
private fun GameScreen(
    state: GameUiState,
    onFieldMeasured: (Float, Float) -> Unit,
    onDrag: (Float) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onMenu: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val fieldWidth = maxWidth
        val fieldHeight = maxHeight
        val widthValue = fieldWidth.value
        val heightValue = fieldHeight.value

        LaunchedEffect(widthValue, heightValue) {
            onFieldMeasured(widthValue, heightValue)
        }

        val margin = GameConfig.TRACK_MARGIN_DP.dp
        val ballCenterY = fieldHeight * GameConfig.BALL_SCREEN_RATIO
        val ballSize by animateDpAsState(
            targetValue = state.run.ballSizeDp.dp,
            animationSpec = tween(
                durationMillis = GameConfig.SIZE_STEP_MS,
                easing = FastOutSlowInEasing
            ),
            label = "ballSize"
        )
        val scroll = if (heightValue > 0f) state.run.traveledDp % heightValue else 0f

        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(fieldHeight)
                .offset(y = scroll.dp - fieldHeight)
        )
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(fieldHeight)
                .offset(y = scroll.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PureSnow.copy(alpha = 0.2f),
                            PureSnow.copy(alpha = 0.0f),
                            NightSlope.copy(alpha = 0.1f)
                        )
                    )
                )
                .pointerInput(state.controlMode) {
                    if (state.controlMode == ControlMode.Drag) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val span = size.width.toFloat().coerceAtLeast(1f)
                            onDrag(dragAmount.x / span)
                        }
                    }
                }
        )

        state.run.obstacles.forEach { obstacle ->
            val screenY = ballCenterY.value - (obstacle.worldY - state.run.traveledDp)
            if (screenY > -120f && screenY < heightValue + 120f) {
                val size = obstacle.sizeDp.dp
                val lane = fieldWidth - margin * 2 - size
                Image(
                    painter = painterResource(spriteOf(obstacle.kind)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(
                            x = margin + lane * obstacle.normX,
                            y = screenY.dp - size / 2
                        )
                        .size(size)
                )
            }
        }

        val ballLane = fieldWidth - margin * 2 - ballSize
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_snowball),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(
                    x = margin + ballLane * state.run.ballNormX,
                    y = ballCenterY - ballSize / 2
                )
                .size(ballSize)
                .graphicsLayer { rotationZ = state.run.ballRotation }
        )

        EffectLayer(effects = state.effects, margin = margin)

        GameHud(
            state = state,
            onPause = onPause
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ControlBar(state = state)
        }

        if (state.phase == GamePhase.Ready) {
            Countdown(value = state.countdown)
        }

        if (state.paused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NightSlope.copy(alpha = 0.45f))
            )
            PauseDialog(
                onResume = onResume,
                onMenu = onMenu
            )
        }
    }
}

@Composable
private fun GameHud(
    state: GameUiState,
    onPause: () -> Unit
) {
    val partName = when (state.part) {
        SnowmanPart.Base -> stringResource(R.string.part_base)
        SnowmanPart.Middle -> stringResource(R.string.part_middle)
        SnowmanPart.Head -> stringResource(R.string.part_head)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .safeDrawingPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PureSnow.copy(alpha = 0.85f))
                    .clickable { onPause() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = stringResource(R.string.cd_pause),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PureSnow.copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.hill_badge,
                        state.hillId,
                        state.runIndex
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = NightSlope
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        SizeMeter(
            fraction = state.sizeFraction,
            thresholds = remember { meterThresholds() },
            partName = partName
        )
    }
}

@Composable
private fun ControlBar(state: GameUiState) {
    Box(
        modifier = Modifier
            .widthIn(max = 320.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(PureSnow.copy(alpha = 0.8f))
            .border(1.dp, FrostSurfaceDeep.copy(alpha = 0.7f), RoundedCornerShape(28.dp))
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        if (state.controlMode == ControlMode.Drag) {
            Text(
                text = stringResource(R.string.drag_hint),
                style = MaterialTheme.typography.labelLarge,
                color = NightSlope,
                textAlign = TextAlign.Center
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(14.dp)
                        .clip(CircleShape)
                        .background(FrostSurface),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val travel: Dp = 166.dp * ((1f - state.tiltLevel) / 2f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .offset(x = travel)
                            .size(14.dp)
                            .background(GlacierPrimary, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.tilt_hint),
                    style = MaterialTheme.typography.labelMedium,
                    color = NightSlope
                )
            }
        }
    }
}

@Composable
private fun Countdown(value: Int) {
    val transition = rememberInfiniteTransition(label = "countdownLoop")
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "countdownPulse"
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(PureSnow.copy(alpha = 0.75f), CircleShape)
                .scale(pulse),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = GlacierPrimary
            )
        }
    }
}

@Composable
private fun EffectLayer(effects: List<FieldEffect>, margin: Dp) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val marginPx = margin.toPx()
        effects.forEach { effect ->
            val lane = (size.width - marginPx * 2).coerceAtLeast(1f)
            val cx = marginPx + lane * effect.normX
            val cy = effect.screenYDp.dp.toPx()
            val fade = (1f - effect.progress).coerceIn(0f, 1f)
            when (effect.kind) {
                EffectKind.Spray -> {
                    repeat(8) { index ->
                        val angle = index * 0.785f
                        val radius = (10f + effect.progress * 42f)
                        drawCircle(
                            color = PureSnow.copy(alpha = fade * 0.9f),
                            radius = 5f * fade + 1f,
                            center = Offset(
                                cx + cos(angle) * radius,
                                cy + sin(angle) * radius
                            )
                        )
                    }
                }

                EffectKind.Puff -> drawCircle(
                    color = PureSnow.copy(alpha = fade * 0.8f),
                    radius = 30.dp.toPx() * (0.6f + effect.progress * 0.8f),
                    center = Offset(cx, cy)
                )

                EffectKind.Sparkle -> drawCircle(
                    color = GlacierPrimary.copy(alpha = fade * 0.7f),
                    radius = 18.dp.toPx() * (0.5f + effect.progress),
                    center = Offset(cx, cy),
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}

private fun meterThresholds(): List<Float> {
    val span = GameConfig.MAX_SIZE_DP - GameConfig.MIN_SIZE_DP
    return listOf(
        (GameConfig.MIDDLE_THRESHOLD_DP - GameConfig.MIN_SIZE_DP) / span,
        (GameConfig.BASE_THRESHOLD_DP - GameConfig.MIN_SIZE_DP) / span
    )
}

private fun spriteOf(kind: ObstacleKind): Int = when (kind) {
    ObstacleKind.Patch -> R.drawable.sprite_3
    ObstacleKind.Pine -> R.drawable.sprite_2
    ObstacleKind.Scarf -> R.drawable.sprite_4
    ObstacleKind.Hat -> R.drawable.sprite_5
}
