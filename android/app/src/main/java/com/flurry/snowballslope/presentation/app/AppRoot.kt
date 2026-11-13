package com.flurry.snowballslope.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flurry.snowballslope.core.di.AppViewModelFactory
import com.flurry.snowballslope.core.navigation.Screen
import com.flurry.snowballslope.presentation.calibrate.CalibrateRoute
import com.flurry.snowballslope.presentation.decorate.DecorateRoute
import com.flurry.snowballslope.presentation.game.GameRoute
import com.flurry.snowballslope.presentation.gameover.GameOverRoute
import com.flurry.snowballslope.presentation.grownups.GrownUpsRoute
import com.flurry.snowballslope.presentation.hills.HillPickerRoute
import com.flurry.snowballslope.presentation.menu.MenuRoute
import com.flurry.snowballslope.presentation.photo.SnowPhotoRoute
import com.flurry.snowballslope.presentation.splash.SplashRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    AnimatedContent(
        targetState = screen,
        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(300)) },
        label = "screenHost"
    ) { target ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (target) {
                Screen.Splash -> SplashRoute(
                    onFinished = { appViewModel.replace(Screen.Menu) }
                )

                Screen.Menu -> MenuRoute(
                    onPlay = { hillId -> appViewModel.startGame(hillId) },
                    onPickHill = { appViewModel.navigate(Screen.HillPicker) },
                    onDecorate = { hillId -> appViewModel.navigate(Screen.Decorate(hillId)) },
                    onGrownUps = { appViewModel.navigate(Screen.GrownUps) }
                )

                Screen.HillPicker -> HillPickerRoute(
                    onHillSelected = { hillId -> appViewModel.restartGame(hillId) },
                    onMenu = { appViewModel.toMenu() }
                )

                is Screen.Game -> GameRoute(
                    hillId = target.hillId,
                    token = target.token,
                    onFinished = { result ->
                        appViewModel.replace(
                            Screen.GameOver(
                                hillId = result.hillId,
                                runIndex = result.runIndex,
                                finalSizeDp = result.finalSizeDp,
                                patches = result.patches,
                                items = result.items
                            )
                        )
                    },
                    onMenu = { appViewModel.toMenu() }
                )

                is Screen.GameOver -> GameOverRoute(
                    hillId = target.hillId,
                    runIndex = target.runIndex,
                    finalSizeDp = target.finalSizeDp,
                    patches = target.patches,
                    items = target.items,
                    onRollAgain = { hillId -> appViewModel.restartGame(hillId) },
                    onDecorate = { hillId -> appViewModel.navigate(Screen.Decorate(hillId)) },
                    onMenu = { appViewModel.toMenu() }
                )

                is Screen.Decorate -> DecorateRoute(
                    hillId = target.hillId,
                    onPhoto = { hillId -> appViewModel.navigate(Screen.Photo(hillId)) },
                    onMenu = { appViewModel.toMenu() }
                )

                is Screen.Photo -> SnowPhotoRoute(
                    hillId = target.hillId,
                    onMenu = { appViewModel.toMenu() }
                )

                Screen.GrownUps -> GrownUpsRoute(
                    onCalibrate = { appViewModel.navigate(Screen.Calibrate) },
                    onMenu = { appViewModel.toMenu() }
                )

                Screen.Calibrate -> CalibrateRoute(
                    onBack = { appViewModel.back() }
                )
            }
        }
    }
}
