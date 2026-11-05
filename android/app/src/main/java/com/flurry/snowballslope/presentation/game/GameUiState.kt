package com.flurry.snowballslope.presentation.game

import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.model.ControlMode
import com.flurry.snowballslope.domain.model.RunState
import com.flurry.snowballslope.domain.model.SnowmanPart

enum class GamePhase { Ready, Rolling, Finishing, Finished }

enum class EffectKind { Spray, Puff, Sparkle }

data class FieldEffect(
    val id: Long,
    val kind: EffectKind,
    val normX: Float,
    val screenYDp: Float,
    val progress: Float
)

data class GameResult(
    val hillId: Int,
    val runIndex: Int,
    val finalSizeDp: Float,
    val patches: Int,
    val items: Int
)

sealed interface GameEvent {
    data object Bump : GameEvent
    data class Finished(val result: GameResult) : GameEvent
}

data class GameUiState(
    val hillId: Int = 1,
    val hillName: String = "",
    val runIndex: Int = 1,
    val runsPerSnowman: Int = GameConfig.RUNS_PER_SNOWMAN,
    val phase: GamePhase = GamePhase.Ready,
    val countdown: Int = GameConfig.COUNTDOWN_FROM,
    val run: RunState = RunState(),
    val controlMode: ControlMode = ControlMode.Tilt,
    val tiltLevel: Float = 0f,
    val effects: List<FieldEffect> = emptyList(),
    val paused: Boolean = false,
    val part: SnowmanPart = SnowmanPart.Head
) {
    val sizeFraction: Float
        get() = ((run.ballSizeDp - GameConfig.MIN_SIZE_DP) /
            (GameConfig.MAX_SIZE_DP - GameConfig.MIN_SIZE_DP)).coerceIn(0f, 1f)
}
