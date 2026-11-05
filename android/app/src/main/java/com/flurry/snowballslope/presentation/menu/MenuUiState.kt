package com.flurry.snowballslope.presentation.menu

import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.model.SnowAnchor
import com.flurry.snowballslope.domain.model.Snowman

data class MenuUiState(
    val snowmenBuilt: Int = 0,
    val totalHills: Int = GameConfig.TOTAL_HILLS,
    val decorationCount: Int = 0,
    val currentHill: Int = 1,
    val village: List<Snowman> = emptyList(),
    val anchors: List<SnowAnchor> = emptyList(),
    val decorateHillId: Int = 0,
    val villageComplete: Boolean = false
) {
    val canDecorate: Boolean get() = decorateHillId > 0
    val showStats: Boolean get() = snowmenBuilt > 0 && decorationCount > 0
}
