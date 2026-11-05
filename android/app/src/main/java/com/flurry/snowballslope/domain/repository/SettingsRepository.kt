package com.flurry.snowballslope.domain.repository

import com.flurry.snowballslope.domain.model.ControlMode

interface SettingsRepository {
    fun controlMode(): ControlMode
    fun setControlMode(mode: ControlMode)
    fun tiltOffset(): Float
    fun setTiltOffset(value: Float)
    fun soundOn(): Boolean
    fun setSoundOn(value: Boolean)
    fun hapticsOn(): Boolean
    fun setHapticsOn(value: Boolean)
}
