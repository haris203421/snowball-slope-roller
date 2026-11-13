package com.flurry.snowballslope.presentation.grownups

import com.flurry.snowballslope.domain.model.ControlMode

data class GrownUpsUiState(
    val gatePassed: Boolean = false,
    val entry: String = "",
    val wrongAttempt: Boolean = false,
    val controlMode: ControlMode = ControlMode.Tilt,
    val tiltAvailable: Boolean = true,
    val soundOn: Boolean = true,
    val hapticsOn: Boolean = true,
    val showResetDialog: Boolean = false
) {
    val showCalibrate: Boolean get() = controlMode == ControlMode.Tilt && tiltAvailable
}
