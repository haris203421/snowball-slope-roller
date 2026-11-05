package com.flurry.snowballslope.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.core.di.ScreenArgs
import com.flurry.snowballslope.data.local.FeedbackPlayer
import com.flurry.snowballslope.data.local.TiltSensorSource
import com.flurry.snowballslope.domain.model.ControlMode
import com.flurry.snowballslope.domain.model.RunState
import com.flurry.snowballslope.domain.model.SnowballRun
import com.flurry.snowballslope.domain.model.SnowmanPart
import com.flurry.snowballslope.domain.repository.GameRepository
import com.flurry.snowballslope.domain.repository.SettingsRepository
import com.flurry.snowballslope.domain.usecase.AdvanceRunUseCase
import com.flurry.snowballslope.domain.usecase.ApplyTiltUseCase
import com.flurry.snowballslope.domain.usecase.StartRunUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val args: ScreenArgs,
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository,
    private val tiltSource: TiltSensorSource,
    private val feedbackPlayer: FeedbackPlayer,
    private val startRun: StartRunUseCase,
    private val advanceRun: AdvanceRunUseCase,
    private val applyTilt: ApplyTiltUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var fieldWidthDp = 0f
    private var fieldHeightDp = 0f
    private var lateral = 0f
    private var dragDelta = 0f
    private var effectId = 0L
    private var soundOn = true
    private var hapticsOn = true
    private var loop: Job? = null

    init {
        val progress = gameRepository.progressOf(args.hillId)
        val runIndex = (progress.runSizes.size + 1).coerceAtMost(GameConfig.RUNS_PER_SNOWMAN)
        soundOn = settingsRepository.soundOn()
        hapticsOn = settingsRepository.hapticsOn()
        _state.value = GameUiState(
            hillId = args.hillId,
            hillName = gameRepository.hillName(args.hillId),
            runIndex = runIndex,
            phase = GamePhase.Ready,
            countdown = GameConfig.COUNTDOWN_FROM,
            run = startRun(args.hillId, runIndex),
            controlMode = settingsRepository.controlMode(),
            part = SnowmanPart.Head
        )
        observeTilt()
        startLoop()
    }

    fun onFieldMeasured(widthDp: Float, heightDp: Float) {
        fieldWidthDp = widthDp
        fieldHeightDp = heightDp
    }

    fun onDrag(deltaNorm: Float) {
        if (_state.value.controlMode != ControlMode.Drag) return
        dragDelta += deltaNorm
    }

    fun pause() {
        _state.update { it.copy(paused = true) }
    }

    fun resume() {
        _state.update { it.copy(paused = false) }
    }

    private fun observeTilt() {
        if (!tiltSource.available) return
        viewModelScope.launch {
            val offset = settingsRepository.tiltOffset()
            tiltSource.readings().collect { reading ->
                val value = applyTilt(reading.raw, offset)
                lateral = value
                _state.update { it.copy(tiltLevel = value) }
            }
        }
    }

    private fun startLoop() {
        loop?.cancel()
        loop = viewModelScope.launch {
            var step = GameConfig.COUNTDOWN_FROM
            while (step > 0) {
                _state.update { it.copy(phase = GamePhase.Ready, countdown = step) }
                delay(GameConfig.COUNTDOWN_STEP_MS)
                step -= 1
            }
            _state.update { it.copy(phase = GamePhase.Rolling) }

            var last = System.nanoTime()
            while (isActive) {
                if (_state.value.paused) {
                    delay(120L)
                    last = System.nanoTime()
                    continue
                }
                delay(GameConfig.FRAME_MS)
                val now = System.nanoTime()
                val deltaSec = ((now - last) / 1_000_000_000f).coerceIn(0f, 0.05f)
                last = now
                tick(deltaSec)
                if (_state.value.run.finished) break
            }
            completeRun()
        }
    }

    private suspend fun tick(deltaSec: Float) {
        if (fieldWidthDp <= 0f || fieldHeightDp <= 0f) return
        val current = _state.value
        val steered = applyDrag(current.run, current.controlMode)
        val lane = if (current.controlMode == ControlMode.Drag) 0f else lateral
        val step = advanceRun(steered, deltaSec, lane, fieldWidthDp, fieldHeightDp)

        val produced = mutableListOf<FieldEffect>()
        if (step.patchHit) {
            produced += newEffect(EffectKind.Spray, step.effectNormX, step.effectScreenYDp)
            if (soundOn) feedbackPlayer.playCollect()
        }
        if (step.pineHit) {
            produced += newEffect(EffectKind.Puff, step.effectNormX, step.effectScreenYDp)
            if (soundOn) feedbackPlayer.playBump()
            if (hapticsOn) _events.send(GameEvent.Bump)
        }
        if (step.pickup != null) {
            produced += newEffect(EffectKind.Sparkle, step.effectNormX, step.effectScreenYDp)
            if (soundOn) feedbackPlayer.playConfirm()
        }

        val advanced = current.effects.mapNotNull { effect ->
            val progress = effect.progress + deltaSec * GameConfig.EFFECT_SPEED
            if (progress >= 1f) null else effect.copy(progress = progress)
        } + produced

        val flattenAt = GameConfig.RUN_DISTANCE_DP - GameConfig.FLATTEN_DISTANCE_DP
        val phase = when {
            step.state.finished -> GamePhase.Finished
            step.state.traveledDp >= flattenAt -> GamePhase.Finishing
            else -> GamePhase.Rolling
        }

        _state.update {
            it.copy(
                run = step.state,
                effects = advanced,
                phase = phase,
                part = partOf(step.state.ballSizeDp)
            )
        }
    }

    private fun applyDrag(run: RunState, mode: ControlMode): RunState {
        if (mode != ControlMode.Drag || dragDelta == 0f) return run
        val moved = (run.ballNormX + dragDelta).coerceIn(0f, 1f)
        dragDelta = 0f
        return run.copy(ballNormX = moved)
    }

    private fun newEffect(kind: EffectKind, normX: Float, screenYDp: Float): FieldEffect {
        effectId += 1
        return FieldEffect(effectId, kind, normX, screenYDp, 0f)
    }

    private fun partOf(sizeDp: Float): SnowmanPart = when {
        sizeDp >= GameConfig.BASE_THRESHOLD_DP -> SnowmanPart.Base
        sizeDp >= GameConfig.MIDDLE_THRESHOLD_DP -> SnowmanPart.Middle
        else -> SnowmanPart.Head
    }

    private suspend fun completeRun() {
        val current = _state.value
        val run = current.run
        gameRepository.saveRun(
            SnowballRun(
                hillId = current.hillId,
                runIndex = current.runIndex,
                finalSizeDp = run.ballSizeDp,
                patches = run.patches,
                scarves = run.scarves,
                hats = run.hats,
                buttons = run.buttons
            )
        )
        _state.update { it.copy(phase = GamePhase.Finished) }
        _events.send(
            GameEvent.Finished(
                GameResult(
                    hillId = current.hillId,
                    runIndex = current.runIndex,
                    finalSizeDp = run.ballSizeDp,
                    patches = run.patches,
                    items = run.items
                )
            )
        )
    }

    override fun onCleared() {
        loop?.cancel()
        super.onCleared()
    }
}
