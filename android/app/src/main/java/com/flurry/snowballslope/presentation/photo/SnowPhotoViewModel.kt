package com.flurry.snowballslope.presentation.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flurry.snowballslope.data.local.PhotoSaver
import com.flurry.snowballslope.domain.model.SnowAnchor
import com.flurry.snowballslope.domain.model.Snowman
import com.flurry.snowballslope.domain.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PhotoStatus { Idle, Saving, Saved, Error }

data class SnowPhotoUiState(
    val hillId: Int = 1,
    val hillName: String = "",
    val snowman: Snowman? = null,
    val anchors: List<SnowAnchor> = emptyList(),
    val status: PhotoStatus = PhotoStatus.Idle
)

class SnowPhotoViewModel(
    private val gameRepository: GameRepository,
    private val photoSaver: PhotoSaver
) : ViewModel() {

    private val _state = MutableStateFlow(SnowPhotoUiState())
    val state: StateFlow<SnowPhotoUiState> = _state.asStateFlow()

    fun load(hillId: Int) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    hillId = hillId,
                    hillName = gameRepository.hillName(hillId),
                    snowman = gameRepository.snowmanOf(hillId),
                    anchors = gameRepository.anchors(),
                    status = PhotoStatus.Idle
                )
            }
        }
    }

    fun save(caption: String) {
        val snowman = _state.value.snowman ?: return
        if (_state.value.status == PhotoStatus.Saving) return
        viewModelScope.launch {
            _state.update { it.copy(status = PhotoStatus.Saving) }
            val name = "snowman_${snowman.hillId}_${System.currentTimeMillis()}.png"
            val done = withContext(Dispatchers.IO) {
                photoSaver.savePostcard(snowman, caption, name)
            }
            _state.update {
                it.copy(status = if (done) PhotoStatus.Saved else PhotoStatus.Error)
            }
        }
    }
}
