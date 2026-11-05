package com.flurry.snowballslope.presentation.decorate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.model.DecorationItem
import com.flurry.snowballslope.domain.model.DecorationKind
import com.flurry.snowballslope.domain.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class DecorateViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DecorateUiState())
    val state: StateFlow<DecorateUiState> = _state.asStateFlow()

    private var hillId = 0

    fun load(id: Int) {
        hillId = id
        viewModelScope.launch { publish() }
    }

    fun drop(kind: DecorationKind, normX: Float, normY: Float) {
        if (hillId == 0) return
        viewModelScope.launch {
            val placed = gameRepository.progressOf(hillId).decorations
            val taken = placed.map { it.anchorId }.toSet()
            val target = gameRepository.anchors()
                .filter { kind in it.kinds && it.id !in taken }
                .minByOrNull { abs(it.x - normX) + abs(it.y - normY) }
                ?: return@launch
            val distance = abs(target.x - normX) + abs(target.y - normY)
            if (distance > GameConfig.SNAP_RADIUS * 2f) return@launch
            if (gameRepository.inventory().countOf(kind) <= 0) return@launch
            gameRepository.saveDecorations(hillId, placed + DecorationItem(kind, target.id))
            publish()
        }
    }

    fun clear() {
        if (hillId == 0) return
        viewModelScope.launch {
            gameRepository.saveDecorations(hillId, emptyList())
            publish()
        }
    }

    private fun publish() {
        val progress = gameRepository.progressOf(hillId)
        _state.update {
            it.copy(
                hillId = hillId,
                hillName = gameRepository.hillName(hillId),
                snowman = gameRepository.snowmanOf(hillId),
                anchors = gameRepository.anchors(),
                inventory = gameRepository.inventory(),
                placedCount = progress.decorations.size
            )
        }
    }
}
