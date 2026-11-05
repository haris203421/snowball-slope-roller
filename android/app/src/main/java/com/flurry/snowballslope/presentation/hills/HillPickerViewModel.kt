package com.flurry.snowballslope.presentation.hills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flurry.snowballslope.domain.repository.GameRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HillPickerViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HillPickerUiState())
    val state: StateFlow<HillPickerUiState> = _state.asStateFlow()

    private val _selected = Channel<Int>(Channel.BUFFERED)
    val selected = _selected.receiveAsFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val progress = gameRepository.allProgress().associateBy { it.hillId }
            val cards = gameRepository.hills().map { hill ->
                val hillProgress = progress[hill.id]
                HillCard(
                    id = hill.id,
                    name = hill.name,
                    runsDone = hillProgress?.runSizes?.size ?: 0,
                    unlocked = hillProgress?.unlocked ?: false,
                    complete = hillProgress?.complete ?: false
                )
            }
            _state.update { it.copy(hills = cards) }
        }
    }

    fun onHillTapped(card: HillCard) {
        if (card.unlocked) {
            viewModelScope.launch { _selected.send(card.id) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(shakeHillId = card.id) }
            delay(260)
            _state.update { it.copy(shakeHillId = 0) }
        }
    }
}
