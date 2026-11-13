package com.flurry.snowballslope.domain.repository

import com.flurry.snowballslope.domain.model.DecorationItem
import com.flurry.snowballslope.domain.model.Hill
import com.flurry.snowballslope.domain.model.HillProgress
import com.flurry.snowballslope.domain.model.Inventory
import com.flurry.snowballslope.domain.model.SnowballRun
import com.flurry.snowballslope.domain.model.SnowAnchor
import com.flurry.snowballslope.domain.model.Snowman

interface GameRepository {
    fun hills(): List<Hill>
    fun hillName(hillId: Int): String
    fun anchors(): List<SnowAnchor>
    fun progressOf(hillId: Int): HillProgress
    fun allProgress(): List<HillProgress>
    fun saveRun(run: SnowballRun)
    fun saveDecorations(hillId: Int, items: List<DecorationItem>)
    fun inventory(): Inventory
    fun snowmanOf(hillId: Int): Snowman?
    fun village(): List<Snowman>
    fun currentHill(): Int
    fun snowmenBuilt(): Int
    fun clearAll()
}
