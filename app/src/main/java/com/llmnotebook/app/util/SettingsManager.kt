package com.llmnotebook.app.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
    companion object {
        private const val PREFS_NAME = "settings_prefs"
        private const val DARK_MODE = "dark_mode"
        
        const val DARK_MODE_SYSTEM = "system"
        const val DARK_MODE_ON = "on"
        const val DARK_MODE_OFF = "off"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var onThemeChangeCallback: (() -> Unit)? = null

    private val _darkMode = MutableStateFlow(prefs.getString(DARK_MODE, DARK_MODE_SYSTEM) ?: DARK_MODE_SYSTEM)
    val darkMode: StateFlow<String> = _darkMode.asStateFlow()

    suspend fun setDarkMode(mode: String) {
        prefs.edit().putString(DARK_MODE, mode).apply()
        _darkMode.value = mode
        onThemeChangeCallback?.invoke()
    }

    fun setOnThemeChangeCallback(callback: (() -> Unit)?) {
        onThemeChangeCallback = callback
    }
}
