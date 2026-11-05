package com.flurry.snowballslope.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flurry.snowballslope.R
import com.flurry.snowballslope.core.di.AppViewModelFactory
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.NightSlopeMid
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.core.theme.SplashCaption
import com.flurry.snowballslope.core.theme.SplashHint
import com.flurry.snowballslope.core.ui.SnowfallCanvas

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashScreen()
}

@Composable
private fun SplashScreen() {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }

    val entryScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.84f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "entryScale"
    )
    val entryAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "entryAlpha"
    )

    val transition = rememberInfiniteTransition(label = "splashLoop")
    val halo by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )
    val tilt by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.22f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            NightSlope.copy(alpha = 0.95f),
                            NightSlopeMid.copy(alpha = 0.9f),
                            GlacierPrimary.copy(alpha = 0.85f)
                        )
                    )
                )
        )
        SnowfallCanvas(modifier = Modifier.fillMaxSize(), flakeCount = 18)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(halo)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(PureSnow.copy(alpha = 0.2f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(176.dp)
                        .scale(entryScale)
                        .alpha(entryAlpha)
                        .graphicsLayer { rotationZ = tilt }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = stringResource(R.string.title),
                style = MaterialTheme.typography.displaySmall,
                color = PureSnow,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.alpha(entryAlpha)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelLarge,
                color = SplashCaption,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(168.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ScarfRed,
                trackColor = PureSnow.copy(alpha = 0.24f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelLarge,
                color = SplashHint
            )
        }
    }
}
