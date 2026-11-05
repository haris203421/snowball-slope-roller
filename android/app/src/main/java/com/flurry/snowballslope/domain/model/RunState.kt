package com.flurry.snowballslope.domain.model

import com.flurry.snowballslope.core.config.GameConfig

data class RunState(
    val traveledDp: Float = 0f,
    val speedDp: Float = GameConfig.START_SPEED_DP,
    val ballNormX: Float = 0.5f,
    val ballSizeDp: Float = GameConfig.START_SIZE_DP,
    val ballRotation: Float = 0f,
    val obstacles: List<Obstacle> = emptyList(),
    val patches: Int = 0,
    val scarves: Int = 0,
    val hats: Int = 0,
    val buttons: Int = 0,
    val invulnUntilDp: Float = 0f,
    val nextPatchAtDp: Float = 320f,
    val nextPineAtDp: Float = 620f,
    val nextScarfAtDp: Float = 780f,
    val nextHatAtDp: Float = 1600f,
    val nextObstacleId: Long = 1L,
    val finished: Boolean = false
) {
    val progress: Float
        get() = (traveledDp / GameConfig.RUN_DISTANCE_DP).coerceIn(0f, 1f)

    val items: Int get() = scarves + hats + buttons
}

data class RunStep(
    val state: RunState,
    val patchHit: Boolean = false,
    val pineHit: Boolean = false,
    val pickup: ObstacleKind? = null,
    val effectNormX: Float = 0.5f,
    val effectScreenYDp: Float = 0f
)
