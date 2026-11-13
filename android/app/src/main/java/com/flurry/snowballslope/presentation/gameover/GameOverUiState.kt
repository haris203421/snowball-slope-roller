package com.flurry.snowballslope.presentation.gameover

import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.model.SnowAnchor
import com.flurry.snowballslope.domain.model.Snowman
import com.flurry.snowballslope.domain.model.SnowmanPart

data class GameOverUiState(
    val hillId: Int = 1,
    val runIndex: Int = 1,
    val runsPerSnowman: Int = GameConfig.RUNS_PER_SNOWMAN,
    val finalSizeDp: Float = GameConfig.START_SIZE_DP,
    val part: SnowmanPart = SnowmanPart.Head,
    val patches: Int = 0,
    val items: Int = 0,
    val runSizes: List<Float> = emptyList(),
    val snowman: Snowman? = null,
    val anchors: List<SnowAnchor> = emptyList(),
    val nextHillId: Int = 1
) {
    val hillComplete: Boolean get() = runSizes.size >= runsPerSnowman
    val showStats: Boolean get() = patches > 0 && items > 0
}
