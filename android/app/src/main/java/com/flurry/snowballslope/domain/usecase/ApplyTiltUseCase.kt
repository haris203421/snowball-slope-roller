package com.flurry.snowballslope.domain.usecase

import com.flurry.snowballslope.core.config.GameConfig
import kotlin.math.abs

class ApplyTiltUseCase {
    operator fun invoke(raw: Float, offset: Float): Float {
        val zeroed = raw - offset
        if (abs(zeroed) < GameConfig.TILT_DEAD_ZONE) return 0f
        val clamped = zeroed.coerceIn(-GameConfig.TILT_MAX_G, GameConfig.TILT_MAX_G)
        return -(clamped / GameConfig.TILT_MAX_G)
    }
}
