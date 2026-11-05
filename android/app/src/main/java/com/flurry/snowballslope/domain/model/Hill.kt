package com.flurry.snowballslope.domain.model

data class Hill(val id: Int, val name: String)

data class HillProgress(
    val hillId: Int,
    val runSizes: List<Float>,
    val decorations: List<DecorationItem>,
    val unlocked: Boolean
) {
    val complete: Boolean get() = runSizes.size >= 3
}
