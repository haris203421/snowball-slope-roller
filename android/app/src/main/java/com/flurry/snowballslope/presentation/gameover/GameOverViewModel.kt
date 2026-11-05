package com.flurry.snowballslope.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flurry.snowballslope.domain.repository.GameRepository
import com.flurry.snowballslope.domain.usecase.AssignSnowmanPartUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameOverViewModel(
    private val gameRepository: GameRepository,
    private val assignPart: AssignSnowmanPartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    fun load(
        hillId: Int,
        runIndex: Int,
        finalSizeDp: Float,
        patches: Int,
        items: Int
    ) {
        viewModelScope.launch {
            val progress = gameRepository.progressOf(hillId)
            _state.update {
                it.copy(
                    hillId = hillId,
                    runIndex = runIndex,
                    finalSizeDp = finalSizeDp,
                    part = assignPart(finalSizeDp),
                    patches = patches,
                    items = items,
                    runSizes = progress.runSizes,
                    snowman = gameRepository.snowmanOf(hillId),
                    anchors = gameRepository.anchors(),
                    nextHillId = gameRepository.currentHill()
                )
            }
        }
    }
}
