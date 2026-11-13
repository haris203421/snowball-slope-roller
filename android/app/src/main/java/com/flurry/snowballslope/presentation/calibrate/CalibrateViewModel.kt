package com.flurry.snowballslope.presentation.calibrate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flurry.snowballslope.data.local.TiltSensorSource
import com.flurry.snowballslope.domain.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

data class CalibrateUiState(
    val raw: Float = 0f,
    val offset: Float = 0f,
    val available: Boolean = true
) {
    val level: Float get() = ((raw - offset) / 4f).coerceIn(-1f, 1f)
    val isLevel: Boolean get() = abs(raw - offset) < 0.5f
}

class CalibrateViewModel(
    private val settingsRepository: SettingsRepository,
    private val tiltSource: TiltSensorSource
) : ViewModel() {

    private val _state = MutableStateFlow(CalibrateUiState())
    val state: StateFlow<CalibrateUiState> = _state.asStateFlow()

    private var reader: Job? = null

    init {
        _state.update {
            it.copy(offset = settingsRepository.tiltOffset(), available = tiltSource.available)
        }
        start()
    }

    fun start() {
        if (!tiltSource.available) return
        reader?.cancel()
        reader = viewModelScope.launch {
            tiltSource.readings().collect { reading ->
                _state.update { it.copy(raw = reading.raw) }
            }
        }
    }

    fun stop() {
        reader?.cancel()
        reader = null
    }

    fun setAsLevel() {
        val neutral = _state.value.raw
        settingsRepository.setTiltOffset(neutral)
        _state.update { it.copy(offset = neutral) }
    }

    override fun onCleared() {
        stop()
        super.onCleared()
    }
}
