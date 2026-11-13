package com.flurry.snowballslope.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object HillPicker : Screen
    data class Game(val hillId: Int, val token: Long = 0L) : Screen
    data class GameOver(
        val hillId: Int,
        val runIndex: Int,
        val finalSizeDp: Float,
        val patches: Int,
        val items: Int
    ) : Screen
    data class Decorate(val hillId: Int) : Screen
    data class Photo(val hillId: Int) : Screen
    data object GrownUps : Screen
    data object Calibrate : Screen
}
