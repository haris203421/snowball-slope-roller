package com.flurry.snowballslope.domain.usecase

import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.model.RunState

class StartRunUseCase {
    operator fun invoke(hillId: Int, runIndex: Int): RunState {
        val warmUp = (hillId - 1).coerceAtLeast(0) * 6f
        return RunState(
            ballSizeDp = GameConfig.START_SIZE_DP,
            nextPatchAtDp = 300f + warmUp,
            nextPineAtDp = 580f + warmUp + runIndex * 40f,
            nextScarfAtDp = 760f + runIndex * 120f,
            nextHatAtDp = 1580f + runIndex * 90f
        )
    }
}
