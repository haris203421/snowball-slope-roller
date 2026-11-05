package com.flurry.snowballslope.data.local

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun runSizes(hillId: Int): List<Float> = readFloats(runsKey(hillId))

    fun setRunSizes(hillId: Int, sizes: List<Float>) {
        prefs.edit().putString(runsKey(hillId), sizes.joinToString(SEP)).apply()
    }

    fun decorations(hillId: Int): List<String> {
        val raw = prefs.getString(decorKey(hillId), "").orEmpty()
        if (raw.isBlank()) return emptyList()
        return raw.split(SEP).filter { it.isNotBlank() }
    }

    fun setDecorations(hillId: Int, encoded: List<String>) {
        prefs.edit().putString(decorKey(hillId), encoded.joinToString(SEP)).apply()
    }

    fun itemCount(type: String): Int = prefs.getInt(itemKey(type), 0)

    fun addItems(type: String, amount: Int) {
        if (amount <= 0) return
        prefs.edit().putInt(itemKey(type), itemCount(type) + amount).apply()
    }

    fun spendItem(type: String, amount: Int) {
        if (amount <= 0) return
        val left = (itemCount(type) - amount).coerceAtLeast(0)
        prefs.edit().putInt(itemKey(type), left).apply()
    }

    fun controlMode(): String = prefs.getString(KEY_CONTROL, "").orEmpty()

    fun setControlMode(value: String) {
        prefs.edit().putString(KEY_CONTROL, value).apply()
    }

    fun tiltOffset(): Float = prefs.getFloat(KEY_TILT_OFFSET, 0f)

    fun setTiltOffset(value: Float) {
        prefs.edit().putFloat(KEY_TILT_OFFSET, value).apply()
    }

    fun flag(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun setFlag(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    private fun readFloats(key: String): List<Float> {
        val raw = prefs.getString(key, "").orEmpty()
        if (raw.isBlank()) return emptyList()
        return raw.split(SEP).mapNotNull { it.toFloatOrNull() }
    }

    private fun runsKey(hillId: Int) = "hill_${hillId}_runs"

    private fun decorKey(hillId: Int) = "hill_${hillId}_decor"

    private fun itemKey(type: String) = "items_$type"

    companion object {
        private const val STORE = "snowball_slope_roller"
        private const val SEP = ","
        const val KEY_CONTROL = "control_mode"
        const val KEY_TILT_OFFSET = "tilt_offset_x"
        const val KEY_SOUND = "sound_on"
        const val KEY_HAPTICS = "haptics_on"
    }
}
