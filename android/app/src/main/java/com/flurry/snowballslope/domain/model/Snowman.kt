package com.flurry.snowballslope.domain.model

data class Snowman(
    val hillId: Int,
    val baseDp: Float,
    val middleDp: Float,
    val headDp: Float,
    val decorations: List<DecorationItem>
)

data class SnowballRun(
    val hillId: Int,
    val runIndex: Int,
    val finalSizeDp: Float,
    val patches: Int,
    val scarves: Int,
    val hats: Int,
    val buttons: Int
) {
    val items: Int get() = scarves + hats + buttons
}
