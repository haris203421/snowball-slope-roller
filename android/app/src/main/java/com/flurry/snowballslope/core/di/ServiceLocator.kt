package com.flurry.snowballslope.core.di

import android.content.Context
import com.flurry.snowballslope.data.local.FeedbackPlayer
import com.flurry.snowballslope.data.local.GamePreferences
import com.flurry.snowballslope.data.local.PhotoSaver
import com.flurry.snowballslope.data.local.TiltSensorSource
import com.flurry.snowballslope.data.repository.GameRepositoryImpl
import com.flurry.snowballslope.data.repository.SettingsRepositoryImpl
import com.flurry.snowballslope.domain.repository.GameRepository
import com.flurry.snowballslope.domain.repository.SettingsRepository
import com.flurry.snowballslope.domain.usecase.AdvanceRunUseCase
import com.flurry.snowballslope.domain.usecase.ApplyTiltUseCase
import com.flurry.snowballslope.domain.usecase.AssignSnowmanPartUseCase
import com.flurry.snowballslope.domain.usecase.BuildSnowmanUseCase
import com.flurry.snowballslope.domain.usecase.StartRunUseCase

object ServiceLocator {

    private var appContext: Context? = null
    private var preferences: GamePreferences? = null
    private var games: GameRepository? = null
    private var settings: SettingsRepository? = null
    private var tilt: TiltSensorSource? = null
    private var feedback: FeedbackPlayer? = null
    private var photos: PhotoSaver? = null

    val buildSnowman = BuildSnowmanUseCase()
    val assignPart = AssignSnowmanPartUseCase()
    val startRun = StartRunUseCase()
    val advanceRun = AdvanceRunUseCase()
    val applyTilt = ApplyTiltUseCase()

    fun init(context: Context) {
        if (appContext == null) appContext = context.applicationContext
    }

    val gameRepository: GameRepository
        get() = games ?: GameRepositoryImpl(prefs(), buildSnowman).also { games = it }

    val settingsRepository: SettingsRepository
        get() = settings ?: SettingsRepositoryImpl(prefs()).also { settings = it }

    val tiltSource: TiltSensorSource
        get() = tilt ?: TiltSensorSource(context()).also { tilt = it }

    val feedbackPlayer: FeedbackPlayer
        get() = feedback ?: FeedbackPlayer(context()).also { feedback = it }

    val photoSaver: PhotoSaver
        get() = photos ?: PhotoSaver(context()).also { photos = it }

    private fun context(): Context = requireNotNull(appContext)

    private fun prefs(): GamePreferences =
        preferences ?: GamePreferences(context()).also { preferences = it }
}
