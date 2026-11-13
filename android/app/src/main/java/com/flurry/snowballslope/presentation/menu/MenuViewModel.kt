package com.flurry.snowballslope.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flurry.snowballslope.core.config.GameConfig
import com.flurry.snowballslope.domain.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val village = gameRepository.village()
            val inventory = gameRepository.inventory()
            val built = gameRepository.snowmenBuilt()
            _state.update {
                it.copy(
                    snowmenBuilt = built,
                    totalHills = GameConfig.TOTAL_HILLS,
                    decorationCount = inventory.total,
                    currentHill = gameRepository.currentHill(),
                    village = village.takeLast(5),
                    anchors = gameRepository.anchors(),
                    decorateHillId = village.lastOrNull()?.hillId ?: 0,
                    villageComplete = built >= GameConfig.TOTAL_HILLS
                )
            }
        }
    }
}
