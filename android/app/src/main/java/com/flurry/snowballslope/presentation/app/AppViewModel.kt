package com.flurry.snowballslope.presentation.app

import androidx.lifecycle.ViewModel
import com.flurry.snowballslope.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val backStack = ArrayDeque<Screen>()

    private var runToken = 0L

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) backStack.addLast(current)
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replace(target: Screen) {
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }

    fun startGame(hillId: Int) {
        runToken += 1
        navigate(Screen.Game(hillId, runToken))
    }

    fun restartGame(hillId: Int) {
        runToken += 1
        replace(Screen.Game(hillId, runToken))
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }
}
