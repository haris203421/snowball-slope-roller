package com.flurry.snowballslope.domain.model

enum class DecorationKind { Scarf, Hat, Button }

data class DecorationItem(val kind: DecorationKind, val anchorId: String)

data class SnowAnchor(
    val id: String,
    val x: Float,
    val y: Float,
    val kinds: Set<DecorationKind>
)

data class Inventory(val scarves: Int, val hats: Int, val buttons: Int) {
    val total: Int get() = scarves + hats + buttons

    fun countOf(kind: DecorationKind): Int = when (kind) {
        DecorationKind.Scarf -> scarves
        DecorationKind.Hat -> hats
        DecorationKind.Button -> buttons
    }
}
