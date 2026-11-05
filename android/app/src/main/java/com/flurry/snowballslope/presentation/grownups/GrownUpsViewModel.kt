package com.flurry.snowballslope.presentation.grownups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.model.ControlMode
import com.flurry.snowballslope.domain.repository.GameRepository
import com.flurry.snowballslope.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GrownUpsViewModel(
    private val settingsRepository: SettingsRepository,
    private val gameRepository: GameRepository,
    private val tiltAvailable: Boolean
) : ViewModel() {

    private val _state = MutableStateFlow(GrownUpsUiState())
    val state: StateFlow<GrownUpsUiState> = _state.asStateFlow()

    private val _exit = Channel<Unit>(Channel.BUFFERED)
    val exit = _exit.receiveAsFlow()

    private var wrongAnswers = 0

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    controlMode = settingsRepository.controlMode(),
                    tiltAvailable = tiltAvailable,
                    soundOn = settingsRepository.soundOn(),
                    hapticsOn = settingsRepository.hapticsOn()
                )
            }
        }
    }

    fun onDigit(digit: Int) {
        val current = _state.value
        if (current.gatePassed || current.entry.length >= 2) return
        val next = current.entry + digit.toString()
        _state.update { it.copy(entry = next) }
        if (next.length == 2) verify(next)
    }

    fun onClearEntry() {
        _state.update { it.copy(entry = "") }
    }

    fun setControlMode(mode: ControlMode) {
        settingsRepository.setControlMode(mode)
        _state.update { it.copy(controlMode = mode) }
    }

    fun setSound(enabled: Boolean) {
        settingsRepository.setSoundOn(enabled)
        _state.update { it.copy(soundOn = enabled) }
    }

    fun setHaptics(enabled: Boolean) {
        settingsRepository.setHapticsOn(enabled)
        _state.update { it.copy(hapticsOn = enabled) }
    }

    fun requestReset() {
        _state.update { it.copy(showResetDialog = true) }
    }

    fun cancelReset() {
        _state.update { it.copy(showResetDialog = false) }
    }

    fun confirmReset() {
        viewModelScope.launch {
            gameRepository.clearAll()
            _state.update { it.copy(showResetDialog = false) }
            _exit.send(Unit)
        }
    }

    private fun verify(entry: String) {
        if (entry.toIntOrNull() == GameConfig.PARENT_GATE_ANSWER) {
            _state.update { it.copy(gatePassed = true, entry = "", wrongAttempt = false) }
            return
        }
        wrongAnswers += 1
        viewModelScope.launch {
            _state.update { it.copy(wrongAttempt = true) }
            delay(220)
            _state.update { it.copy(wrongAttempt = false, entry = "") }
            if (wrongAnswers >= GameConfig.PARENT_GATE_TRIES) _exit.send(Unit)
        }
    }
}
