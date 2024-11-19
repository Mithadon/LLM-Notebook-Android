package com.llmnotebook.app.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class SettingsManager(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "settings_prefs"
        private const val DARK_MODE = "dark_mode"
        private const val DEFAULT_MODEL = "default_model"
        private const val SAVE_DIRECTORY = "save_directory"
        private const val TEMPERATURE = "temperature"
        private const val MAX_TOKENS = "max_tokens"
        private const val TOP_P = "top_p"
        private const val TOP_K = "top_k"
        
        // Theme color keys
        private const val COLOR_PRIMARY = "color_primary"
        private const val COLOR_ON_PRIMARY = "color_on_primary"
        private const val COLOR_PRIMARY_CONTAINER = "color_primary_container"
        private const val COLOR_ON_PRIMARY_CONTAINER = "color_on_primary_container"
        private const val COLOR_SECONDARY = "color_secondary"
        private const val COLOR_ON_SECONDARY = "color_on_secondary"
        private const val COLOR_SECONDARY_CONTAINER = "color_secondary_container"
        private const val COLOR_ON_SECONDARY_CONTAINER = "color_on_secondary_container"
        private const val COLOR_TERTIARY = "color_tertiary"
        private const val COLOR_ON_TERTIARY = "color_on_tertiary"
        private const val COLOR_TERTIARY_CONTAINER = "color_tertiary_container"
        private const val COLOR_ON_TERTIARY_CONTAINER = "color_on_tertiary_container"
        private const val COLOR_BACKGROUND = "color_background"
        private const val COLOR_ON_BACKGROUND = "color_on_background"
        private const val COLOR_SURFACE = "color_surface"
        private const val COLOR_ON_SURFACE = "color_on_surface"
        private const val COLOR_SURFACE_VARIANT = "color_surface_variant"
        private const val COLOR_ON_SURFACE_VARIANT = "color_on_surface_variant"
        private const val COLOR_OUTLINE = "color_outline"
        
        const val DARK_MODE_SYSTEM = "system"
        const val DARK_MODE_ON = "on"
        const val DARK_MODE_OFF = "off"

        // Default values for sampler settings
        const val DEFAULT_TEMPERATURE = 0.7f
        const val DEFAULT_MAX_TOKENS = 500
        const val DEFAULT_TOP_P = 0.9f
        const val DEFAULT_TOP_K = 40

        // Default theme colors
        val DEFAULT_PRIMARY = Color(0xFF6650a4)
        val DEFAULT_ON_PRIMARY = Color(0xFFFFFFFF)
        val DEFAULT_PRIMARY_CONTAINER = Color(0xFFEADDFF)
        val DEFAULT_ON_PRIMARY_CONTAINER = Color(0xFF21005E)
        val DEFAULT_SECONDARY = Color(0xFF625b71)
        val DEFAULT_ON_SECONDARY = Color(0xFFFFFFFF)
        val DEFAULT_SECONDARY_CONTAINER = Color(0xFFE8DEF8)
        val DEFAULT_ON_SECONDARY_CONTAINER = Color(0xFF1D192B)
        val DEFAULT_TERTIARY = Color(0xFF7D5260)
        val DEFAULT_ON_TERTIARY = Color(0xFFFFFFFF)
        val DEFAULT_TERTIARY_CONTAINER = Color(0xFFFFD8E4)
        val DEFAULT_ON_TERTIARY_CONTAINER = Color(0xFF31111D)
        val DEFAULT_BACKGROUND = Color(0xFFFFFBFE)
        val DEFAULT_ON_BACKGROUND = Color(0xFF1C1B1F)
        val DEFAULT_SURFACE = Color(0xFFFFFBFE)
        val DEFAULT_ON_SURFACE = Color(0xFF1C1B1F)
        val DEFAULT_SURFACE_VARIANT = Color(0xFFE7E0EC)
        val DEFAULT_ON_SURFACE_VARIANT = Color(0xFF49454F)
        val DEFAULT_OUTLINE = Color(0xFF79747E)
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var onThemeChangeCallback: (() -> Unit)? = null

    private val _darkMode = MutableStateFlow(prefs.getString(DARK_MODE, DARK_MODE_SYSTEM) ?: DARK_MODE_SYSTEM)
    val darkMode: StateFlow<String> = _darkMode.asStateFlow()

    private val _defaultModel = MutableStateFlow(prefs.getString(DEFAULT_MODEL, null))
    val defaultModel: StateFlow<String?> = _defaultModel.asStateFlow()

    private val defaultSaveDirectory: String by lazy {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val appDir = File(documentsDir, "LLM Notebook")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        appDir.absolutePath
    }

    private val _saveDirectory = MutableStateFlow(prefs.getString(SAVE_DIRECTORY, defaultSaveDirectory))
    val saveDirectory: StateFlow<String?> = _saveDirectory.asStateFlow()

    private val _temperature = MutableStateFlow(prefs.getFloat(TEMPERATURE, DEFAULT_TEMPERATURE))
    val temperature: StateFlow<Float> = _temperature.asStateFlow()

    private val _maxTokens = MutableStateFlow(prefs.getInt(MAX_TOKENS, DEFAULT_MAX_TOKENS))
    val maxTokens: StateFlow<Int> = _maxTokens.asStateFlow()

    private val _topP = MutableStateFlow(prefs.getFloat(TOP_P, DEFAULT_TOP_P))
    val topP: StateFlow<Float> = _topP.asStateFlow()

    private val _topK = MutableStateFlow(prefs.getInt(TOP_K, DEFAULT_TOP_K))
    val topK: StateFlow<Int> = _topK.asStateFlow()

    // Theme color flows
    private val _colorPrimary = MutableStateFlow(Color(prefs.getInt(COLOR_PRIMARY, DEFAULT_PRIMARY.toArgb())))
    val colorPrimary: StateFlow<Color> = _colorPrimary.asStateFlow()

    private val _colorOnPrimary = MutableStateFlow(Color(prefs.getInt(COLOR_ON_PRIMARY, DEFAULT_ON_PRIMARY.toArgb())))
    val colorOnPrimary: StateFlow<Color> = _colorOnPrimary.asStateFlow()

    private val _colorPrimaryContainer = MutableStateFlow(Color(prefs.getInt(COLOR_PRIMARY_CONTAINER, DEFAULT_PRIMARY_CONTAINER.toArgb())))
    val colorPrimaryContainer: StateFlow<Color> = _colorPrimaryContainer.asStateFlow()

    private val _colorOnPrimaryContainer = MutableStateFlow(Color(prefs.getInt(COLOR_ON_PRIMARY_CONTAINER, DEFAULT_ON_PRIMARY_CONTAINER.toArgb())))
    val colorOnPrimaryContainer: StateFlow<Color> = _colorOnPrimaryContainer.asStateFlow()

    private val _colorSecondary = MutableStateFlow(Color(prefs.getInt(COLOR_SECONDARY, DEFAULT_SECONDARY.toArgb())))
    val colorSecondary: StateFlow<Color> = _colorSecondary.asStateFlow()

    private val _colorOnSecondary = MutableStateFlow(Color(prefs.getInt(COLOR_ON_SECONDARY, DEFAULT_ON_SECONDARY.toArgb())))
    val colorOnSecondary: StateFlow<Color> = _colorOnSecondary.asStateFlow()

    private val _colorSecondaryContainer = MutableStateFlow(Color(prefs.getInt(COLOR_SECONDARY_CONTAINER, DEFAULT_SECONDARY_CONTAINER.toArgb())))
    val colorSecondaryContainer: StateFlow<Color> = _colorSecondaryContainer.asStateFlow()

    private val _colorOnSecondaryContainer = MutableStateFlow(Color(prefs.getInt(COLOR_ON_SECONDARY_CONTAINER, DEFAULT_ON_SECONDARY_CONTAINER.toArgb())))
    val colorOnSecondaryContainer: StateFlow<Color> = _colorOnSecondaryContainer.asStateFlow()

    private val _colorTertiary = MutableStateFlow(Color(prefs.getInt(COLOR_TERTIARY, DEFAULT_TERTIARY.toArgb())))
    val colorTertiary: StateFlow<Color> = _colorTertiary.asStateFlow()

    private val _colorOnTertiary = MutableStateFlow(Color(prefs.getInt(COLOR_ON_TERTIARY, DEFAULT_ON_TERTIARY.toArgb())))
    val colorOnTertiary: StateFlow<Color> = _colorOnTertiary.asStateFlow()

    private val _colorTertiaryContainer = MutableStateFlow(Color(prefs.getInt(COLOR_TERTIARY_CONTAINER, DEFAULT_TERTIARY_CONTAINER.toArgb())))
    val colorTertiaryContainer: StateFlow<Color> = _colorTertiaryContainer.asStateFlow()

    private val _colorOnTertiaryContainer = MutableStateFlow(Color(prefs.getInt(COLOR_ON_TERTIARY_CONTAINER, DEFAULT_ON_TERTIARY_CONTAINER.toArgb())))
    val colorOnTertiaryContainer: StateFlow<Color> = _colorOnTertiaryContainer.asStateFlow()

    private val _colorBackground = MutableStateFlow(Color(prefs.getInt(COLOR_BACKGROUND, DEFAULT_BACKGROUND.toArgb())))
    val colorBackground: StateFlow<Color> = _colorBackground.asStateFlow()

    private val _colorOnBackground = MutableStateFlow(Color(prefs.getInt(COLOR_ON_BACKGROUND, DEFAULT_ON_BACKGROUND.toArgb())))
    val colorOnBackground: StateFlow<Color> = _colorOnBackground.asStateFlow()

    private val _colorSurface = MutableStateFlow(Color(prefs.getInt(COLOR_SURFACE, DEFAULT_SURFACE.toArgb())))
    val colorSurface: StateFlow<Color> = _colorSurface.asStateFlow()

    private val _colorOnSurface = MutableStateFlow(Color(prefs.getInt(COLOR_ON_SURFACE, DEFAULT_ON_SURFACE.toArgb())))
    val colorOnSurface: StateFlow<Color> = _colorOnSurface.asStateFlow()

    private val _colorSurfaceVariant = MutableStateFlow(Color(prefs.getInt(COLOR_SURFACE_VARIANT, DEFAULT_SURFACE_VARIANT.toArgb())))
    val colorSurfaceVariant: StateFlow<Color> = _colorSurfaceVariant.asStateFlow()

    private val _colorOnSurfaceVariant = MutableStateFlow(Color(prefs.getInt(COLOR_ON_SURFACE_VARIANT, DEFAULT_ON_SURFACE_VARIANT.toArgb())))
    val colorOnSurfaceVariant: StateFlow<Color> = _colorOnSurfaceVariant.asStateFlow()

    private val _colorOutline = MutableStateFlow(Color(prefs.getInt(COLOR_OUTLINE, DEFAULT_OUTLINE.toArgb())))
    val colorOutline: StateFlow<Color> = _colorOutline.asStateFlow()

    init {
        // Initialize save directory if not set
        if (!prefs.contains(SAVE_DIRECTORY)) {
            prefs.edit().putString(SAVE_DIRECTORY, defaultSaveDirectory).apply()
        }
    }

    suspend fun setDarkMode(mode: String) {
        prefs.edit().putString(DARK_MODE, mode).apply()
        _darkMode.value = mode
        onThemeChangeCallback?.invoke()
    }

    fun setOnThemeChangeCallback(callback: (() -> Unit)?) {
        onThemeChangeCallback = callback
    }

    suspend fun setDefaultModel(modelId: String?) {
        prefs.edit().putString(DEFAULT_MODEL, modelId).apply()
        _defaultModel.value = modelId
    }

    suspend fun setSaveDirectory(path: String?) {
        prefs.edit().putString(SAVE_DIRECTORY, path).apply()
        _saveDirectory.value = path
    }

    suspend fun setTemperature(value: Float) {
        prefs.edit().putFloat(TEMPERATURE, value).apply()
        _temperature.value = value
    }

    suspend fun setMaxTokens(value: Int) {
        prefs.edit().putInt(MAX_TOKENS, value).apply()
        _maxTokens.value = value
    }

    suspend fun setTopP(value: Float) {
        prefs.edit().putFloat(TOP_P, value).apply()
        _topP.value = value
    }

    suspend fun setTopK(value: Int) {
        prefs.edit().putInt(TOP_K, value).apply()
        _topK.value = value
    }

    // Theme color setters
    suspend fun setColorPrimary(color: Color) {
        prefs.edit().putInt(COLOR_PRIMARY, color.toArgb()).apply()
        _colorPrimary.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnPrimary(color: Color) {
        prefs.edit().putInt(COLOR_ON_PRIMARY, color.toArgb()).apply()
        _colorOnPrimary.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorPrimaryContainer(color: Color) {
        prefs.edit().putInt(COLOR_PRIMARY_CONTAINER, color.toArgb()).apply()
        _colorPrimaryContainer.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnPrimaryContainer(color: Color) {
        prefs.edit().putInt(COLOR_ON_PRIMARY_CONTAINER, color.toArgb()).apply()
        _colorOnPrimaryContainer.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorSecondary(color: Color) {
        prefs.edit().putInt(COLOR_SECONDARY, color.toArgb()).apply()
        _colorSecondary.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnSecondary(color: Color) {
        prefs.edit().putInt(COLOR_ON_SECONDARY, color.toArgb()).apply()
        _colorOnSecondary.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorSecondaryContainer(color: Color) {
        prefs.edit().putInt(COLOR_SECONDARY_CONTAINER, color.toArgb()).apply()
        _colorSecondaryContainer.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnSecondaryContainer(color: Color) {
        prefs.edit().putInt(COLOR_ON_SECONDARY_CONTAINER, color.toArgb()).apply()
        _colorOnSecondaryContainer.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorTertiary(color: Color) {
        prefs.edit().putInt(COLOR_TERTIARY, color.toArgb()).apply()
        _colorTertiary.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnTertiary(color: Color) {
        prefs.edit().putInt(COLOR_ON_TERTIARY, color.toArgb()).apply()
        _colorOnTertiary.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorTertiaryContainer(color: Color) {
        prefs.edit().putInt(COLOR_TERTIARY_CONTAINER, color.toArgb()).apply()
        _colorTertiaryContainer.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnTertiaryContainer(color: Color) {
        prefs.edit().putInt(COLOR_ON_TERTIARY_CONTAINER, color.toArgb()).apply()
        _colorOnTertiaryContainer.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorBackground(color: Color) {
        prefs.edit().putInt(COLOR_BACKGROUND, color.toArgb()).apply()
        _colorBackground.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnBackground(color: Color) {
        prefs.edit().putInt(COLOR_ON_BACKGROUND, color.toArgb()).apply()
        _colorOnBackground.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorSurface(color: Color) {
        prefs.edit().putInt(COLOR_SURFACE, color.toArgb()).apply()
        _colorSurface.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnSurface(color: Color) {
        prefs.edit().putInt(COLOR_ON_SURFACE, color.toArgb()).apply()
        _colorOnSurface.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorSurfaceVariant(color: Color) {
        prefs.edit().putInt(COLOR_SURFACE_VARIANT, color.toArgb()).apply()
        _colorSurfaceVariant.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOnSurfaceVariant(color: Color) {
        prefs.edit().putInt(COLOR_ON_SURFACE_VARIANT, color.toArgb()).apply()
        _colorOnSurfaceVariant.value = color
        onThemeChangeCallback?.invoke()
    }

    suspend fun setColorOutline(color: Color) {
        prefs.edit().putInt(COLOR_OUTLINE, color.toArgb()).apply()
        _colorOutline.value = color
        onThemeChangeCallback?.invoke()
    }
}
