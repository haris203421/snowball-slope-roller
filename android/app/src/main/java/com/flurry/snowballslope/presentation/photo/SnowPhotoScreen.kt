package com.flurry.snowballslope.presentation.photo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.IceEdge
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.core.theme.SlateText
import com.flurry.snowballslope.core.theme.SnowBackground
import com.flurry.snowballslope.core.theme.SnowBackgroundDeep
import com.flurry.snowballslope.core.ui.PrimaryButton
import com.flurry.snowballslope.core.ui.SecondaryButton
import com.flurry.snowballslope.core.ui.SnowmanPreview

@Composable
fun SnowPhotoRoute(
    hillId: Int,
    onMenu: () -> Unit
) {
    val photoViewModel: SnowPhotoViewModel = viewModel(factory = AppViewModelFactory())
    val state by photoViewModel.state.collectAsStateWithLifecycle()
    val caption = stringResource(R.string.photo_caption, state.hillName)

    LaunchedEffect(hillId) {
        photoViewModel.load(hillId)
    }

    SnowPhotoScreen(
        state = state,
        onSave = { photoViewModel.save(caption) },
        onMenu = onMenu
    )
}

@Composable
private fun SnowPhotoScreen(
    state: SnowPhotoUiState,
    onSave: () -> Unit,
    onMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.14f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SnowBackgroundDeep.copy(alpha = 0.95f),
                            SnowBackground
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.save_photo),
                style = MaterialTheme.typography.headlineMedium,
                color = NightSlope
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .aspectRatio(0.8f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(10.dp, PureSnow),
                colors = CardDefaults.cardColors(containerColor = PureSnow),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(R.drawable.bg_game),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        SnowmanPreview(
                            snowman = state.snowman,
                            anchors = state.anchors,
                            height = 230.dp,
                            contentDescription = stringResource(R.string.cd_snowman),
                            modifier = Modifier.padding(bottom = 34.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.photo_caption, state.hillName),
                        style = MaterialTheme.typography.labelMedium,
                        color = SlateText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(PureSnow.copy(alpha = 0.85f))
                            .padding(vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            StatusCard(status = state.status)
            Spacer(modifier = Modifier.height(14.dp))

            PrimaryButton(
                text = if (state.status == PhotoStatus.Error) {
                    stringResource(R.string.try_again)
                } else {
                    stringResource(R.string.save_photo)
                },
                onClick = onSave,
                enabled = state.snowman != null && state.status != PhotoStatus.Saving
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu
            )
        }
    }
}

@Composable
private fun StatusCard(status: PhotoStatus) {
    when (status) {
        PhotoStatus.Idle -> Spacer(modifier = Modifier.height(0.dp))

        PhotoStatus.Saving -> CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = GlacierPrimary
        )

        PhotoStatus.Saved -> Card(
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, IceEdge),
            colors = CardDefaults.cardColors(containerColor = GlacierPrimary),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = PureSnow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.saved),
                    style = MaterialTheme.typography.titleMedium,
                    color = PureSnow
                )
            }
        }

        PhotoStatus.Error -> Card(
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, ScarfRed),
            colors = CardDefaults.cardColors(containerColor = PureSnow),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Text(
                text = stringResource(R.string.could_not_save),
                style = MaterialTheme.typography.titleMedium,
                color = ScarfRed,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}
