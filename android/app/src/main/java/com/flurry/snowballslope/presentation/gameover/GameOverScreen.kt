package com.flurry.snowballslope.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.core.di.AppViewModelFactory
import com.flurry.snowballslope.core.theme.FrostSurface
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.IceEdge
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.core.theme.SnowBackground
import com.flurry.snowballslope.core.ui.PrimaryButton
import com.flurry.snowballslope.core.ui.SecondaryButton
import com.flurry.snowballslope.core.ui.SnowCard
import com.flurry.snowballslope.core.ui.SnowfallCanvas
import com.flurry.snowballslope.core.ui.StatCard
import com.flurry.snowballslope.domain.model.SnowmanPart
import kotlin.math.roundToInt

@Composable
fun GameOverRoute(
    hillId: Int,
    runIndex: Int,
    finalSizeDp: Float,
    patches: Int,
    items: Int,
    onRollAgain: (Int) -> Unit,
    onDecorate: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory())
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(hillId, runIndex, finalSizeDp) {
        gameOverViewModel.load(hillId, runIndex, finalSizeDp, patches, items)
    }

    GameOverScreen(
        state = state,
        onRollAgain = {
            onRollAgain(if (state.hillComplete) state.nextHillId else state.hillId)
        },
        onDecorate = { onDecorate(state.hillId) },
        onMenu = onMenu
    )
}

@Composable
private fun GameOverScreen(
    state: GameOverUiState,
    onRollAgain: () -> Unit,
    onDecorate: () -> Unit,
    onMenu: () -> Unit
) {
    val partLabel = when (state.part) {
        SnowmanPart.Base -> stringResource(R.string.part_base)
        SnowmanPart.Middle -> stringResource(R.string.part_middle)
        SnowmanPart.Head -> stringResource(R.string.part_head)
    }
    val heroSize by animateDpAsState(
        targetValue = state.finalSizeDp.coerceIn(56f, 160f).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "hero"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.16f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            GlacierPrimary.copy(alpha = 0.92f),
                            FrostSurface.copy(alpha = 0.92f),
                            SnowBackground
                        )
                    )
                )
        )
        SnowfallCanvas(modifier = Modifier.fillMaxSize(), flakeCount = 12, speedMillis = 6400)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.nice_roll),
                style = MaterialTheme.typography.displaySmall,
                color = PureSnow,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.run_of_total,
                    state.runIndex,
                    state.runsPerSnowman
                ),
                style = MaterialTheme.typography.labelLarge,
                color = PureSnow.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier.size(170.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(PureSnow.copy(alpha = 0.35f), PureSnow.copy(alpha = 0f))
                            ),
                            CircleShape
                        )
                )
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_snowball),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(heroSize)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            SnowCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14.dp) {
                Text(
                    text = stringResource(R.string.part_verdict, partLabel),
                    style = MaterialTheme.typography.titleMedium,
                    color = NightSlope,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.size_cm, state.finalSizeDp.roundToInt()),
                    style = MaterialTheme.typography.headlineSmall,
                    color = GlacierPrimary
                )
            }

            if (state.showStats) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = Icons.Default.Star,
                        value = stringResource(R.string.count_value, state.patches),
                        label = stringResource(R.string.stat_patches),
                        accent = GlacierPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Default.Favorite,
                        value = stringResource(R.string.count_value, state.items),
                        label = stringResource(R.string.stat_items),
                        accent = ScarfRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            BuilderStrip(state = state)

            Spacer(modifier = Modifier.weight(1f))

            if (state.hillComplete) {
                PrimaryButton(
                    text = stringResource(R.string.decorate),
                    onClick = onDecorate
                )
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = stringResource(R.string.roll_again),
                    onClick = onRollAgain
                )
                TextButton(onClick = onMenu) {
                    Text(
                        text = stringResource(R.string.menu),
                        style = MaterialTheme.typography.titleMedium,
                        color = NightSlope
                    )
                }
            } else {
                PrimaryButton(
                    text = stringResource(R.string.roll_again),
                    onClick = onRollAgain
                )
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu
                )
            }
        }
    }
}

@Composable
private fun BuilderStrip(state: GameOverUiState) {
    val slots = listOf(96.dp, 72.dp, 52.dp)
    val sorted = state.runSizes.sortedDescending()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            slots.forEachIndexed { index, slot ->
                val filled = sorted.getOrNull(index)
                Box(
                    modifier = Modifier.size(slot),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(slot)
                            .border(2.dp, IceEdge.copy(alpha = 0.7f), CircleShape)
                    )
                    if (filled != null) {
                        val drawn by animateDpAsState(
                            targetValue = filled
                                .coerceIn(GameConfig.MIN_SIZE_DP, GameConfig.MAX_SIZE_DP)
                                .dp,
                            animationSpec = tween(
                                durationMillis = 420,
                                easing = FastOutSlowInEasing
                            ),
                            label = "slot"
                        )
                        Image(
                            painter = painterResource(R.drawable.sprite_1),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(drawn)
                        )
                    }
                }
            }
        }
        if (state.hillComplete) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(ScarfRed, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.snowman_ready),
                    style = MaterialTheme.typography.labelLarge,
                    color = NightSlope
                )
            }
        }
    }
}
