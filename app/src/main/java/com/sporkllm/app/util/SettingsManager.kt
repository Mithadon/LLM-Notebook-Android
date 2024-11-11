package com.sporkllm.app.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _selectedModel = mutableStateOf(prefs.getString(KEY_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL)
    val selectedModel: State<String> = _selectedModel

    private val _darkMode = mutableStateOf(prefs.getInt(KEY_DARK_MODE, DARK_MODE_SYSTEM))
    val darkMode: State<Int> = _darkMode

    private val _temperature = mutableStateOf(prefs.getFloat(KEY_TEMPERATURE, 0.7f))
    val temperature: State<Float> = _temperature

    private val _maxTokens = mutableStateOf(prefs.getInt(KEY_MAX_TOKENS, 500))
    val maxTokens: State<Int> = _maxTokens

    private val _topP = mutableStateOf(prefs.getFloat(KEY_TOP_P, 0.9f))
    val topP: State<Float> = _topP

    private val _topK = mutableStateOf(prefs.getInt(KEY_TOP_K, 40))
    val topK: State<Int> = _topK

    private val defaultSaveDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
        ?: Environment.getExternalStorageDirectory().absolutePath + "/Documents"
    
    private val _saveDirectory = mutableStateOf(prefs.getString(KEY_SAVE_DIR, defaultSaveDir) ?: defaultSaveDir)
    val saveDirectory: State<String> = _saveDirectory

    // Callback for theme changes
    private var onThemeChangeCallback: (() -> Unit)? = null

    fun setOnThemeChangeCallback(callback: (() -> Unit)?) {
        onThemeChangeCallback = callback
    }

    fun setModel(model: String) {
        prefs.edit { putString(KEY_MODEL, model) }
        _selectedModel.value = model
    }

    fun setDarkMode(mode: Int) {
        prefs.edit { putInt(KEY_DARK_MODE, mode) }
        _darkMode.value = mode
        onThemeChangeCallback?.invoke()
    }

    fun setTemperature(value: Float) {
        prefs.edit { putFloat(KEY_TEMPERATURE, value) }
        _temperature.value = value
    }

    fun setMaxTokens(value: Int) {
        prefs.edit { putInt(KEY_MAX_TOKENS, value) }
        _maxTokens.value = value
    }

    fun setTopP(value: Float) {
        prefs.edit { putFloat(KEY_TOP_P, value) }
        _topP.value = value
    }

    fun setTopK(value: Int) {
        prefs.edit { putInt(KEY_TOP_K, value) }
        _topK.value = value
    }

    fun setSaveDirectory(path: String) {
        prefs.edit { putString(KEY_SAVE_DIR, path) }
        _saveDirectory.value = path
    }

    @Composable
    fun shouldUseDarkTheme(): Boolean {
        val currentMode by darkMode
        return when (currentMode) {
            DARK_MODE_ON -> true
            DARK_MODE_OFF -> false
            else -> isSystemInDarkTheme()
        }
    }

    companion object {
        private const val PREFS_NAME = "sporkllm_settings"
        private const val KEY_MODEL = "selected_model"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_TEMPERATURE = "temperature"
        private const val KEY_MAX_TOKENS = "max_tokens"
        private const val KEY_TOP_P = "top_p"
        private const val KEY_TOP_K = "top_k"
        private const val KEY_SAVE_DIR = "save_directory"

        const val DEFAULT_MODEL = "anthropic/claude-3-opus-20240229"
        const val DARK_MODE_SYSTEM = 0
        const val DARK_MODE_ON = 1
        const val DARK_MODE_OFF = 2

        const val VERSION = "0.1"
        const val BUILD_DATE = "2024-11-10"
    }
}
