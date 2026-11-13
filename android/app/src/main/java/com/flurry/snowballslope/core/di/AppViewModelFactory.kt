package com.flurry.snowballslope.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flurry.snowballslope.presentation.app.AppViewModel
import com.flurry.snowballslope.presentation.calibrate.CalibrateViewModel
import com.flurry.snowballslope.presentation.decorate.DecorateViewModel
import com.flurry.snowballslope.presentation.game.GameViewModel
import com.flurry.snowballslope.presentation.gameover.GameOverViewModel
import com.flurry.snowballslope.presentation.grownups.GrownUpsViewModel
import com.flurry.snowballslope.presentation.hills.HillPickerViewModel
import com.flurry.snowballslope.presentation.menu.MenuViewModel
import com.flurry.snowballslope.presentation.photo.SnowPhotoViewModel
import com.flurry.snowballslope.presentation.splash.SplashViewModel

data class ScreenArgs(
    val hillId: Int = 1,
    val runIndex: Int = 1,
    val finalSizeDp: Float = 0f,
    val patches: Int = 0,
    val items: Int = 0
)

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(
    private val args: ScreenArgs = ScreenArgs()
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(ServiceLocator.gameRepository)

            modelClass.isAssignableFrom(HillPickerViewModel::class.java) ->
                HillPickerViewModel(ServiceLocator.gameRepository)

            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                args = args,
                gameRepository = ServiceLocator.gameRepository,
                settingsRepository = ServiceLocator.settingsRepository,
                tiltSource = ServiceLocator.tiltSource,
                feedbackPlayer = ServiceLocator.feedbackPlayer,
                startRun = ServiceLocator.startRun,
                advanceRun = ServiceLocator.advanceRun,
                applyTilt = ServiceLocator.applyTilt
            )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                gameRepository = ServiceLocator.gameRepository,
                assignPart = ServiceLocator.assignPart
            )

            modelClass.isAssignableFrom(DecorateViewModel::class.java) ->
                DecorateViewModel(ServiceLocator.gameRepository)

            modelClass.isAssignableFrom(SnowPhotoViewModel::class.java) ->
                SnowPhotoViewModel(ServiceLocator.gameRepository, ServiceLocator.photoSaver)

            modelClass.isAssignableFrom(GrownUpsViewModel::class.java) -> GrownUpsViewModel(
                settingsRepository = ServiceLocator.settingsRepository,
                gameRepository = ServiceLocator.gameRepository,
                tiltAvailable = ServiceLocator.tiltSource.available
            )

            modelClass.isAssignableFrom(CalibrateViewModel::class.java) -> CalibrateViewModel(
                settingsRepository = ServiceLocator.settingsRepository,
                tiltSource = ServiceLocator.tiltSource
            )

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
