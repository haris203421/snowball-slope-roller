package com.flurry.snowballslope.presentation.grownups

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.flurry.snowballslope.core.theme.GlacierPrimary
import com.flurry.snowballslope.core.theme.NightSlope
import com.flurry.snowballslope.core.theme.PureSnow
import com.flurry.snowballslope.core.theme.ScarfRed
import com.flurry.snowballslope.core.theme.SlateText
import com.flurry.snowballslope.core.theme.SnowBackground
import com.flurry.snowballslope.core.theme.SnowBackgroundDeep
import com.flurry.snowballslope.core.ui.ChoiceOption
import com.flurry.snowballslope.core.ui.SecondaryButton
import com.flurry.snowballslope.core.ui.SegmentedChoice
import com.flurry.snowballslope.core.ui.SnowCard
import com.flurry.snowballslope.domain.model.ControlMode
import com.flurry.snowballslope.presentation.dialog.ResetDialog

@Composable
fun GrownUpsRoute(
    onCalibrate: () -> Unit,
    onMenu: () -> Unit
) {
    val grownUpsViewModel: GrownUpsViewModel = viewModel(factory = AppViewModelFactory())
    val state by grownUpsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        grownUpsViewModel.refresh()
        grownUpsViewModel.exit.collect { onMenu() }
    }

    GrownUpsScreen(
        state = state,
        onDigit = grownUpsViewModel::onDigit,
        onClearEntry = grownUpsViewModel::onClearEntry,
        onControlMode = grownUpsViewModel::setControlMode,
        onSound = grownUpsViewModel::setSound,
        onHaptics = grownUpsViewModel::setHaptics,
        onRequestReset = grownUpsViewModel::requestReset,
        onCancelReset = grownUpsViewModel::cancelReset,
        onConfirmReset = grownUpsViewModel::confirmReset,
        onCalibrate = onCalibrate,
        onMenu = onMenu
    )
}

@Composable
private fun GrownUpsScreen(
    state: GrownUpsUiState,
    onDigit: (Int) -> Unit,
    onClearEntry: () -> Unit,
    onControlMode: (ControlMode) -> Unit,
    onSound: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onRequestReset: () -> Unit,
    onCancelReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onCalibrate: () -> Unit,
    onMenu: () -> Unit
) {
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
                .padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.grown_ups),
                style = MaterialTheme.typography.labelLarge,
                color = SlateText
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (!state.gatePassed) {
                ParentGate(
                    state = state,
                    onDigit = onDigit,
                    onClearEntry = onClearEntry,
                    onMenu = onMenu
                )
            } else {
                Settings(
                    state = state,
                    onControlMode = onControlMode,
                    onSound = onSound,
                    onHaptics = onHaptics,
                    onRequestReset = onRequestReset,
                    onCalibrate = onCalibrate,
                    onMenu = onMenu
                )
            }
        }

        if (state.showResetDialog) {
            ResetDialog(onConfirm = onConfirmReset, onCancel = onCancelReset)
        }
    }
}

@Composable
private fun ParentGate(
    state: GrownUpsUiState,
    onDigit: (Int) -> Unit,
    onClearEntry: () -> Unit,
    onMenu: () -> Unit
) {
    val shake by animateDpAsState(
        targetValue = if (state.wrongAttempt) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "gateShake"
    )
    SnowCard(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = shake),
        contentPadding = 20.dp
    ) {
        Text(
            text = stringResource(R.string.gate_question),
            style = MaterialTheme.typography.titleLarge,
            color = NightSlope,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 52.dp)
                .background(FrostSurface.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.entry,
                style = MaterialTheme.typography.headlineMedium,
                color = if (state.wrongAttempt) ScarfRed else NightSlope
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        val rows = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { digit ->
                    DigitKey(digit = digit, onDigit = onDigit)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DigitKey(digit = 0, onDigit = onDigit)
            TextButton(onClick = onClearEntry) {
                Text(
                    text = stringResource(R.string.gate_clear),
                    style = MaterialTheme.typography.titleMedium,
                    color = GlacierPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        SecondaryButton(text = stringResource(R.string.menu), onClick = onMenu)
    }
}

@Composable
private fun DigitKey(digit: Int, onDigit: (Int) -> Unit) {
    OutlinedButton(
        onClick = { onDigit(digit) },
        modifier = Modifier.size(56.dp),
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = digit.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = NightSlope
        )
    }
}

@Composable
private fun Settings(
    state: GrownUpsUiState,
    onControlMode: (ControlMode) -> Unit,
    onSound: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onRequestReset: () -> Unit,
    onCalibrate: () -> Unit,
    onMenu: () -> Unit
) {
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SnowCard(modifier = Modifier.fillMaxWidth(), contentPadding = 16.dp) {
            Text(
                text = stringResource(R.string.control),
                style = MaterialTheme.typography.titleMedium,
                color = NightSlope
            )
            Spacer(modifier = Modifier.height(10.dp))
            SegmentedChoice(
                options = listOf(
                    ChoiceOption(ControlMode.Tilt.name, stringResource(R.string.tilt)),
                    ChoiceOption(ControlMode.Drag.name, stringResource(R.string.drag))
                ),
                selectedId = state.controlMode.name,
                selectedState = stringResource(R.string.state_selected),
                idleState = offLabel,
                onSelect = { id ->
                    onControlMode(
                        if (id == ControlMode.Drag.name) ControlMode.Drag else ControlMode.Tilt
                    )
                }
            )
            if (state.showCalibrate) {
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryButton(
                    text = stringResource(R.string.calibrate_tilt),
                    onClick = onCalibrate
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        ToggleRow(
            label = stringResource(R.string.sound),
            checked = state.soundOn,
            stateLabel = if (state.soundOn) onLabel else offLabel,
            onCheckedChange = onSound
        )
        Spacer(modifier = Modifier.height(12.dp))
        ToggleRow(
            label = stringResource(R.string.haptics),
            checked = state.hapticsOn,
            stateLabel = if (state.hapticsOn) onLabel else offLabel,
            onCheckedChange = onHaptics
        )

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onRequestReset,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(22.dp)
        ) {
            Text(
                text = stringResource(R.string.reset_progress),
                style = MaterialTheme.typography.titleMedium,
                color = ScarfRed
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        SecondaryButton(text = stringResource(R.string.menu), onClick = onMenu)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    stateLabel: String,
    onCheckedChange: (Boolean) -> Unit
) {
    SnowCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = NightSlope
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.semantics { stateDescription = stateLabel },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = GlacierPrimary,
                    checkedThumbColor = PureSnow,
                    uncheckedTrackColor = FrostSurface
                )
            )
        }
    }
}
