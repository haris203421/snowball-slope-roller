package com.flurry.snowballslope.data.repository

import com.flurry.snowballslope.data.local.GamePreferences
import com.flurry.snowballslope.domain.model.ControlMode
import com.flurry.snowballslope.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val preferences: GamePreferences
) : SettingsRepository {

    override fun controlMode(): ControlMode {
        val stored = preferences.controlMode()
        return ControlMode.entries.firstOrNull { it.name == stored } ?: ControlMode.Tilt
    }

    override fun setControlMode(mode: ControlMode) {
        preferences.setControlMode(mode.name)
    }

    override fun tiltOffset(): Float = preferences.tiltOffset()

    override fun setTiltOffset(value: Float) {
        preferences.setTiltOffset(value)
    }

    override fun soundOn(): Boolean = preferences.flag(GamePreferences.KEY_SOUND, true)

    override fun setSoundOn(value: Boolean) {
        preferences.setFlag(GamePreferences.KEY_SOUND, value)
    }

    override fun hapticsOn(): Boolean = preferences.flag(GamePreferences.KEY_HAPTICS, true)

    override fun setHapticsOn(value: Boolean) {
        preferences.setFlag(GamePreferences.KEY_HAPTICS, value)
    }
}
