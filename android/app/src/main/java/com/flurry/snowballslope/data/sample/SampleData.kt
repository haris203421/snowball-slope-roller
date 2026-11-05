package com.flurry.snowballslope.data.sample

import com.flurry.snowballslope.domain.model.DecorationKind
import com.flurry.snowballslope.domain.model.Hill
import com.flurry.snowballslope.domain.model.SnowAnchor

object SampleData {

    val hills: List<Hill> = listOf(
        Hill(1, "FIRST FLURRY"),
        Hill(2, "PINE BEND"),
        Hill(3, "FROST GULLY"),
        Hill(4, "MITTEN MEADOW"),
        Hill(5, "COCOA RIDGE"),
        Hill(6, "SILVER BIRCH RUN"),
        Hill(7, "LANTERN LANE"),
        Hill(8, "POWDER BOWL"),
        Hill(9, "ROBIN HOLLOW"),
        Hill(10, "GLASS LAKE DROP"),
        Hill(11, "SPRUCE SHOULDER"),
        Hill(12, "NORTH CHIMNEY"),
        Hill(13, "MOONLIT TERRACE"),
        Hill(14, "AURORA CREST"),
        Hill(15, "VILLAGE SQUARE")
    )

    val anchors: List<SnowAnchor> = listOf(
        SnowAnchor("HEAD_HAT", 0.50f, 0.14f, setOf(DecorationKind.Hat)),
        SnowAnchor("HEAD_EYE_L", 0.44f, 0.22f, setOf(DecorationKind.Button)),
        SnowAnchor("HEAD_EYE_R", 0.58f, 0.22f, setOf(DecorationKind.Button)),
        SnowAnchor("HEAD_NOSE", 0.52f, 0.28f, setOf(DecorationKind.Hat)),
        SnowAnchor("NECK_SCARF", 0.50f, 0.38f, setOf(DecorationKind.Scarf)),
        SnowAnchor("MID_BUTTON_1", 0.50f, 0.50f, setOf(DecorationKind.Button)),
        SnowAnchor("MID_BUTTON_2", 0.50f, 0.60f, setOf(DecorationKind.Button)),
        SnowAnchor("BASE_BUTTON", 0.50f, 0.74f, setOf(DecorationKind.Button))
    )

    fun hillName(hillId: Int): String =
        hills.firstOrNull { it.id == hillId }?.name ?: hills.first().name

    fun anchorOf(id: String): SnowAnchor? = anchors.firstOrNull { it.id == id }
}
