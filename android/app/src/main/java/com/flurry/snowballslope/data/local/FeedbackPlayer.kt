package com.flurry.snowballslope.data.local

import android.content.Context
import android.media.AudioManager

class FeedbackPlayer(context: Context) {

    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    fun playCollect() = play(AudioManager.FX_KEY_CLICK, 0.45f)

    fun playBump() = play(AudioManager.FX_KEYPRESS_DELETE, 0.35f)

    fun playConfirm() = play(AudioManager.FX_KEYPRESS_RETURN, 0.6f)

    private fun play(effect: Int, volume: Float) {
        val manager = audioManager ?: return
        runCatching { manager.playSoundEffect(effect, volume) }
    }
}
