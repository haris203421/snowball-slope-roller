package com.flurry.snowballslope.domain.usecase

import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.model.Obstacle
import com.flurry.snowballslope.domain.model.ObstacleKind
import com.flurry.snowballslope.domain.model.RunState
import com.flurry.snowballslope.domain.model.RunStep
import kotlin.math.PI
import kotlin.math.abs
import kotlin.random.Random

class AdvanceRunUseCase {

    operator fun invoke(
        state: RunState,
        deltaSec: Float,
        lateral: Float,
        fieldWidthDp: Float,
        fieldHeightDp: Float
    ): RunStep {
        if (state.finished || fieldWidthDp <= 0f || fieldHeightDp <= 0f) {
            return RunStep(state)
        }

        val speed = speedAt(state.traveledDp)
        val traveled = state.traveledDp + speed * deltaSec
        val laneDp = (fieldWidthDp - 2f * GameConfig.TRACK_MARGIN_DP - state.ballSizeDp)
            .coerceAtLeast(1f)
        val normX = (state.ballNormX + lateral * GameConfig.LATERAL_SPEED_DP * deltaSec / laneDp)
            .coerceIn(0f, 1f)
        val rotation = (state.ballRotation +
            speed * deltaSec / (PI.toFloat() * state.ballSizeDp) * 360f) % 360f

        val ballScreenY = fieldHeightDp * GameConfig.BALL_SCREEN_RATIO
        val spawnAhead = traveled + ballScreenY + 160f
        val spawnLimit = GameConfig.RUN_DISTANCE_DP - GameConfig.FLATTEN_DISTANCE_DP

        var patchAt = state.nextPatchAtDp
        var pineAt = state.nextPineAtDp
        var scarfAt = state.nextScarfAtDp
        var hatAt = state.nextHatAtDp
        var nextId = state.nextObstacleId
        val spawned = mutableListOf<Obstacle>()

        while (patchAt <= spawnAhead) {
            if (patchAt < spawnLimit) {
                val count = if (Random.nextFloat() < 0.35f) 2 else 1
                repeat(count) { index ->
                    spawned += Obstacle(
                        id = nextId++,
                        kind = ObstacleKind.Patch,
                        normX = lanePosition(index, count),
                        worldY = patchAt + index * 70f,
                        sizeDp = GameConfig.PATCH_SIZE_DP
                    )
                }
            }
            patchAt += Random.nextFloat() *
                (GameConfig.PATCH_GAP_MAX_DP - GameConfig.PATCH_GAP_MIN_DP) +
                GameConfig.PATCH_GAP_MIN_DP
        }
        while (pineAt <= spawnAhead) {
            if (pineAt < spawnLimit) {
                spawned += Obstacle(
                    id = nextId++,
                    kind = ObstacleKind.Pine,
                    normX = 0.08f + Random.nextFloat() * 0.84f,
                    worldY = pineAt,
                    sizeDp = GameConfig.PINE_SIZE_DP
                )
            }
            pineAt += Random.nextFloat() *
                (GameConfig.PINE_GAP_MAX_DP - GameConfig.PINE_GAP_MIN_DP) +
                GameConfig.PINE_GAP_MIN_DP
        }
        while (scarfAt <= spawnAhead) {
            if (scarfAt < spawnLimit) {
                spawned += Obstacle(
                    id = nextId++,
                    kind = ObstacleKind.Scarf,
                    normX = 0.15f + Random.nextFloat() * 0.7f,
                    worldY = scarfAt,
                    sizeDp = GameConfig.PICKUP_SIZE_DP
                )
            }
            scarfAt += GameConfig.SCARF_GAP_DP
        }
        while (hatAt <= spawnAhead) {
            if (hatAt < spawnLimit) {
                spawned += Obstacle(
                    id = nextId++,
                    kind = ObstacleKind.Hat,
                    normX = 0.15f + Random.nextFloat() * 0.7f,
                    worldY = hatAt,
                    sizeDp = GameConfig.PICKUP_SIZE_DP
                )
            }
            hatAt += GameConfig.HAT_GAP_DP
        }

        val despawnBelow = traveled - (fieldHeightDp - ballScreenY) - 160f
        val living = (state.obstacles + spawned).filter { it.worldY > despawnBelow }

        val ballCenterX = GameConfig.TRACK_MARGIN_DP + state.ballSizeDp / 2f + normX * laneDp
        var size = state.ballSizeDp
        var patches = state.patches
        var scarves = state.scarves
        var hats = state.hats
        var buttons = state.buttons
        var invulnUntil = state.invulnUntilDp
        var patchHit = false
        var pineHit = false
        var pickup: ObstacleKind? = null
        var effectX = normX
        var effectY = ballScreenY
        val remaining = mutableListOf<Obstacle>()

        for (obstacle in living) {
            val screenY = ballScreenY - (obstacle.worldY - traveled)
            val obstacleLane = (fieldWidthDp - 2f * GameConfig.TRACK_MARGIN_DP - obstacle.sizeDp)
                .coerceAtLeast(1f)
            val obstacleX = GameConfig.TRACK_MARGIN_DP + obstacle.sizeDp / 2f +
                obstacle.normX * obstacleLane
            val reach = (size + obstacle.sizeDp) / 2f
            val touching = abs(screenY - ballScreenY) < reach * 0.85f &&
                abs(obstacleX - ballCenterX) < reach * 0.8f
            if (!touching) {
                remaining += obstacle
                continue
            }
            when (obstacle.kind) {
                ObstacleKind.Patch -> {
                    size = (size + GameConfig.PATCH_GROWTH_DP).coerceAtMost(GameConfig.MAX_SIZE_DP)
                    patches += 1
                    patchHit = true
                    effectX = obstacle.normX
                    effectY = screenY
                }
                ObstacleKind.Pine -> {
                    if (traveled >= invulnUntil) {
                        size = (size - GameConfig.TREE_SHRINK_DP)
                            .coerceAtLeast(GameConfig.MIN_SIZE_DP)
                        invulnUntil = traveled + GameConfig.BUMP_INVULN_DP
                        pineHit = true
                        effectX = obstacle.normX
                        effectY = screenY
                    } else {
                        remaining += obstacle
                    }
                }
                ObstacleKind.Scarf -> {
                    scarves += 1
                    pickup = ObstacleKind.Scarf
                    effectX = obstacle.normX
                    effectY = screenY
                }
                ObstacleKind.Hat -> {
                    if (Random.nextFloat() < 0.5f) hats += 1 else buttons += 1
                    pickup = ObstacleKind.Hat
                    effectX = obstacle.normX
                    effectY = screenY
                }
            }
        }

        val finished = traveled >= GameConfig.RUN_DISTANCE_DP
        return RunStep(
            state = state.copy(
                traveledDp = traveled,
                speedDp = speed,
                ballNormX = normX,
                ballSizeDp = size,
                ballRotation = rotation,
                obstacles = remaining,
                patches = patches,
                scarves = scarves,
                hats = hats,
                buttons = buttons,
                invulnUntilDp = invulnUntil,
                nextPatchAtDp = patchAt,
                nextPineAtDp = pineAt,
                nextScarfAtDp = scarfAt,
                nextHatAtDp = hatAt,
                nextObstacleId = nextId,
                finished = finished
            ),
            patchHit = patchHit,
            pineHit = pineHit,
            pickup = pickup,
            effectNormX = effectX,
            effectScreenYDp = effectY
        )
    }

    fun speedAt(traveledDp: Float): Float {
        val ramp = (traveledDp / GameConfig.RUN_DISTANCE_DP).coerceIn(0f, 1f)
        val base = GameConfig.START_SPEED_DP +
            (GameConfig.END_SPEED_DP - GameConfig.START_SPEED_DP) * ramp
        val flattenStart = GameConfig.RUN_DISTANCE_DP - GameConfig.FLATTEN_DISTANCE_DP
        if (traveledDp < flattenStart) return base
        val left = ((GameConfig.RUN_DISTANCE_DP - traveledDp) /
            GameConfig.FLATTEN_DISTANCE_DP).coerceIn(0f, 1f)
        return base * (0.25f + 0.75f * left)
    }

    private fun lanePosition(index: Int, count: Int): Float {
        if (count <= 1) return 0.1f + Random.nextFloat() * 0.8f
        val slot = Random.nextInt(3)
        val lanes = listOf(0.16f, 0.5f, 0.84f)
        return lanes[(slot + index) % 3]
    }
}
