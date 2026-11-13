package com.flurry.snowballslope.presentation.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.di.AppViewModelFactory
import com.flurry.snowballslope.core.theme.FrostSurface
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.IceEdge
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.core.theme.SlateText
import com.flurry.snowballslope.core.theme.SnowBackground
import com.flurry.snowballslope.core.ui.PrimaryButton
import com.flurry.snowballslope.core.ui.SecondaryButton
import com.flurry.snowballslope.core.ui.SnowCard
import com.flurry.snowballslope.core.ui.SnowfallCanvas
import com.flurry.snowballslope.core.ui.SnowmanPreview
import com.flurry.snowballslope.core.ui.StatCard

@Composable
fun MenuRoute(
    onPlay: (Int) -> Unit,
    onPickHill: () -> Unit,
    onDecorate: (Int) -> Unit,
    onGrownUps: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuScreen(
        state = state,
        onPlay = { onPlay(state.currentHill) },
        onPickHill = onPickHill,
        onDecorate = { onDecorate(state.decorateHillId) },
        onGrownUps = onGrownUps
    )
}

@Composable
private fun MenuScreen(
    state: MenuUiState,
    onPlay: () -> Unit,
    onPickHill: () -> Unit,
    onDecorate: () -> Unit,
    onGrownUps: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SnowBackground.copy(alpha = 0.05f),
                            SnowBackground.copy(alpha = 0.4f),
                            SnowBackground
                        )
                    )
                )
        )
        SnowfallCanvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.58f),
            flakeCount = 14,
            speedMillis = 7000
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .safeDrawingPadding()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_home),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onGrownUps, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.cd_grown_ups),
                        tint = NightSlope,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                VillageSquare(state = state)
            }

            SnowSheet(
                state = state,
                onPlay = onPlay,
                onPickHill = onPickHill,
                onDecorate = onDecorate
            )
        }
    }
}

@Composable
private fun VillageSquare(state: MenuUiState) {
    val transition = rememberInfiniteTransition(label = "village")
    if (state.village.isEmpty()) {
        Text(
            text = stringResource(R.string.village_empty),
            style = MaterialTheme.typography.labelLarge,
            color = SlateText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 18.dp, start = 32.dp, end = 32.dp)
        )
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom
    ) {
        state.village.forEachIndexed { index, snowman ->
            val bob by transition.animateFloat(
                initialValue = 0f,
                targetValue = 3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2400,
                        delayMillis = index * 220,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bob"
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SnowmanPreview(
                    snowman = snowman,
                    anchors = state.anchors,
                    height = 64.dp,
                    contentDescription = stringResource(R.string.cd_snowman),
                    showMound = false,
                    modifier = Modifier.offset(y = -bob.dp)
                )
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 14.dp)
                        .background(PureSnow.copy(alpha = 0.8f), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun SnowSheet(
    state: MenuUiState,
    onPlay: () -> Unit,
    onPickHill: () -> Unit,
    onDecorate: () -> Unit
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        shown = true
    }
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 4 }
        ) + fadeIn(animationSpec = tween(durationMillis = 450))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = PureSnow.copy(alpha = 0.94f),
            border = BorderStroke(1.dp, IceEdge.copy(alpha = 0.5f)),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .safeDrawingPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 5.dp)
                        .background(FrostSurface, CircleShape)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = NightSlope,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.menu_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))

                if (state.villageComplete) {
                    SnowCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = ScarfRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.village_complete),
                                style = MaterialTheme.typography.titleMedium,
                                color = NightSlope
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                } else if (state.showStats) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.Star,
                            value = stringResource(
                                R.string.snowmen_of_total,
                                state.snowmenBuilt,
                                state.totalHills
                            ),
                            label = stringResource(R.string.stat_snowmen),
                            accent = GlacierPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.Favorite,
                            value = stringResource(R.string.count_value, state.decorationCount),
                            label = stringResource(R.string.stat_items),
                            accent = ScarfRed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                PrimaryButton(
                    text = stringResource(R.string.play),
                    icon = Icons.Default.PlayArrow,
                    onClick = onPlay
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryButton(
                        text = stringResource(R.string.pick_hill),
                        onClick = onPickHill,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = stringResource(R.string.decorate),
                        onClick = onDecorate,
                        enabled = state.canDecorate,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
