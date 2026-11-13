package com.flurry.snowballslope.presentation.calibrate

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.flurry.snowballslope.core.di.AppViewModelFactory
import com.flurry.snowballslope.core.theme.FrostSurface
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.IceEdge
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.SlateText
import com.flurry.snowballslope.core.theme.SnowBackground
import com.flurry.snowballslope.core.theme.SnowBackgroundDeep
import com.flurry.snowballslope.core.ui.PrimaryButton
import com.flurry.snowballslope.core.ui.SecondaryButton

@Composable
fun CalibrateRoute(onBack: () -> Unit) {
    val calibrateViewModel: CalibrateViewModel = viewModel(factory = AppViewModelFactory())
    val state by calibrateViewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose { calibrateViewModel.stop() }
    }

    CalibrateScreen(
        state = state,
        onSetLevel = calibrateViewModel::setAsLevel,
        onBack = onBack
    )
}

@Composable
private fun CalibrateScreen(
    state: CalibrateUiState,
    onSetLevel: () -> Unit,
    onBack: () -> Unit
) {
    val bubbleColor by animateColorAsState(
        targetValue = if (state.isLevel) GlacierPrimary else IceEdge,
        label = "bubbleColor"
    )
    val bubbleShift by animateDpAsState(
        targetValue = (state.level * 90f).dp,
        animationSpec = spring(),
        label = "bubbleShift"
    )

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
                            SnowBackground.copy(alpha = 0.95f),
                            SnowBackgroundDeep.copy(alpha = 0.97f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.calibrate_tilt),
                style = MaterialTheme.typography.headlineMedium,
                color = NightSlope,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(FrostSurface.copy(alpha = 0.55f), CircleShape)
                    .border(2.dp, IceEdge, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .border(2.dp, bubbleColor.copy(alpha = 0.6f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.dp, bubbleColor, CircleShape)
                )
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_snowball),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(x = bubbleShift)
                        .size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = if (state.isLevel) {
                    stringResource(R.string.that_is_level)
                } else {
                    stringResource(R.string.hold_level)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = SlateText,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )

            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.set_as_level),
                onClick = onSetLevel,
                enabled = state.available,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.back),
                onClick = onBack
            )
        }
    }
}
