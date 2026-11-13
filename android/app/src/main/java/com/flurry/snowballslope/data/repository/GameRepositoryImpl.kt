package com.flurry.snowballslope.data.repository

import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.data.local.GamePreferences
import com.flurry.snowballslope.data.sample.SampleData
import com.flurry.snowballslope.domain.model.DecorationItem
import com.flurry.snowballslope.domain.model.DecorationKind
import com.flurry.snowballslope.domain.model.Hill
import com.flurry.snowballslope.domain.model.HillProgress
import com.flurry.snowballslope.domain.model.Inventory
import com.flurry.snowballslope.domain.model.SnowballRun
import com.flurry.snowballslope.domain.model.SnowAnchor
import com.flurry.snowballslope.domain.model.Snowman
import com.flurry.snowballslope.domain.repository.GameRepository
import com.flurry.snowballslope.domain.usecase.BuildSnowmanUseCase

class GameRepositoryImpl(
    private val preferences: GamePreferences,
    private val buildSnowman: BuildSnowmanUseCase
) : GameRepository {

    override fun hills(): List<Hill> = SampleData.hills

    override fun hillName(hillId: Int): String = SampleData.hillName(hillId)

    override fun anchors(): List<SnowAnchor> = SampleData.anchors

    override fun progressOf(hillId: Int): HillProgress = HillProgress(
        hillId = hillId,
        runSizes = preferences.runSizes(hillId),
        decorations = decode(preferences.decorations(hillId)),
        unlocked = hillId <= currentHill()
    )

    override fun allProgress(): List<HillProgress> =
        SampleData.hills.map { progressOf(it.id) }

    override fun saveRun(run: SnowballRun) {
        val sizes = preferences.runSizes(run.hillId).toMutableList()
        if (sizes.size >= GameConfig.RUNS_PER_SNOWMAN) return
        sizes += run.finalSizeDp
        preferences.setRunSizes(run.hillId, sizes)
        preferences.addItems(DecorationKind.Scarf.name, run.scarves)
        preferences.addItems(DecorationKind.Hat.name, run.hats)
        preferences.addItems(DecorationKind.Button.name, run.buttons)
    }

    override fun saveDecorations(hillId: Int, items: List<DecorationItem>) {
        val previous = decode(preferences.decorations(hillId))
        preferences.setDecorations(hillId, items.map { "${it.kind.name}:${it.anchorId}" })
        DecorationKind.entries.forEach { kind ->
            val before = previous.count { it.kind == kind }
            val after = items.count { it.kind == kind }
            if (after > before) preferences.spendItem(kind.name, after - before)
            if (after < before) preferences.addItems(kind.name, before - after)
        }
    }

    override fun inventory(): Inventory = Inventory(
        scarves = preferences.itemCount(DecorationKind.Scarf.name),
        hats = preferences.itemCount(DecorationKind.Hat.name),
        buttons = preferences.itemCount(DecorationKind.Button.name)
    )

    override fun snowmanOf(hillId: Int): Snowman? {
        val progress = progressOf(hillId)
        return buildSnowman(hillId, progress.runSizes, progress.decorations)
    }

    override fun village(): List<Snowman> =
        SampleData.hills.mapNotNull { snowmanOf(it.id) }

    override fun currentHill(): Int {
        val firstOpen = SampleData.hills.firstOrNull {
            preferences.runSizes(it.id).size < GameConfig.RUNS_PER_SNOWMAN
        }
        return firstOpen?.id ?: GameConfig.TOTAL_HILLS
    }

    override fun snowmenBuilt(): Int = SampleData.hills.count {
        preferences.runSizes(it.id).size >= GameConfig.RUNS_PER_SNOWMAN
    }

    override fun clearAll() {
        preferences.clearAll()
    }

    private fun decode(raw: List<String>): List<DecorationItem> = raw.mapNotNull { entry ->
        val parts = entry.split(":")
        if (parts.size != 2) return@mapNotNull null
        val kind = DecorationKind.entries.firstOrNull { it.name == parts[0] }
            ?: return@mapNotNull null
        val anchor = SampleData.anchorOf(parts[1]) ?: return@mapNotNull null
        DecorationItem(kind, anchor.id)
    }
}
