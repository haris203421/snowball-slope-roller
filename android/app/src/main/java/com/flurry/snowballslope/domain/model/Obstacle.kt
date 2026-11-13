package com.flurry.snowballslope.domain.model

enum class ObstacleKind { Patch, Pine, Scarf, Hat }

data class Obstacle(
    val id: Long,
    val kind: ObstacleKind,
    val normX: Float,
    val worldY: Float,
    val sizeDp: Float
)
