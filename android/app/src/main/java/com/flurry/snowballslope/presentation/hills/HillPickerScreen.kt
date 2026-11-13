package com.flurry.snowballslope.presentation.hills

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.di.AppViewModelFactory
import com.flurry.snowballslope.core.theme.FrostSurface
import com.flurry.snowballslope.core.theme.FrostSurfaceDeep
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.core.theme.SnowBackground
import com.flurry.snowballslope.core.theme.SnowBackgroundDeep

@Composable
fun HillPickerRoute(
    onHillSelected: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val pickerViewModel: HillPickerViewModel = viewModel(factory = AppViewModelFactory())
    val state by pickerViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        pickerViewModel.refresh()
        pickerViewModel.selected.collect { hillId -> onHillSelected(hillId) }
    }

    HillPickerScreen(
        state = state,
        onHillTapped = pickerViewModel::onHillTapped,
        onMenu = onMenu
    )
}

@Composable
private fun HillPickerScreen(
    state: HillPickerUiState,
    onHillTapped: (HillCard) -> Unit,
    onMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.12f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SnowBackground.copy(alpha = 0.93f),
                            SnowBackgroundDeep.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onMenu) {
                    Text(
                        text = stringResource(R.string.back),
                        style = MaterialTheme.typography.titleMedium,
                        color = GlacierPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.pick_hill),
                    style = MaterialTheme.typography.titleLarge,
                    color = NightSlope
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.cd_home),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(state.hills, key = { it.id }) { hill ->
                    HillTile(
                        hill = hill,
                        runsPerSnowman = state.runsPerSnowman,
                        shaking = state.shakeHillId == hill.id,
                        onTap = { onHillTapped(hill) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HillTile(
    hill: HillCard,
    runsPerSnowman: Int,
    shaking: Boolean,
    onTap: () -> Unit
) {
    val shake by animateDpAsState(
        targetValue = if (shaking) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 110),
        label = "shake"
    )
    val tint = (hill.id - 1).coerceIn(0, 14) / 14f
    val stateLabel = when {
        !hill.unlocked -> stringResource(R.string.state_locked)
        hill.complete -> stringResource(R.string.state_complete)
        else -> stringResource(R.string.state_open)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .offset(x = shake)
            .alpha(if (hill.unlocked) 1f else 0.5f)
            .clickable { onTap() }
            .semantics { stateDescription = stateLabel },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, FrostSurfaceDeep.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(containerColor = PureSnow.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                                SnowBackgroundDeep.copy(alpha = 0.25f + tint * 0.2f),
                                PureSnow.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(36.dp)
                    .background(PureSnow.copy(alpha = 0.92f), CircleShape)
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = hill.id.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = GlacierPrimary
                )
            }

            if (!hill.unlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NightSlope.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(22.dp)
                )
            }

            if (hill.complete) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(44.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    repeat(runsPerSnowman) { index ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    color = if (index < hill.runsDone) {
                                        GlacierPrimary
                                    } else {
                                        FrostSurface
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                    if (hill.complete) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(ScarfRed, CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = hill.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = NightSlope,
                    textAlign = TextAlign.Start,
                    maxLines = 2
                )
            }
        }
    }
}
