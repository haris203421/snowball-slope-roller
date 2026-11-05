package com.flurry.snowballslope.presentation.hills

import com.flurry.snowballslope.core.config.GameConfig

data class HillCard(
    val id: Int,
    val name: String,
    val runsDone: Int,
    val unlocked: Boolean,
    val complete: Boolean
)

data class HillPickerUiState(
    val hills: List<HillCard> = emptyList(),
    val runsPerSnowman: Int = GameConfig.RUNS_PER_SNOWMAN,
    val shakeHillId: Int = 0
)
