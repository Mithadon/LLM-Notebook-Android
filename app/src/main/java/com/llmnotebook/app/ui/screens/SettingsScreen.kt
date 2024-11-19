package com.llmnotebook.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.data.OpenRouterRepository
import com.llmnotebook.app.ui.components.ColorPickerDialog
import com.llmnotebook.app.ui.screens.settings.*
import com.llmnotebook.app.util.ApiKeyManager
import com.llmnotebook.app.util.SettingsManager
import com.llmnotebook.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    settingsManager: SettingsManager,
    viewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val repository = remember { OpenRouterRepository() }
    val scrollState = rememberScrollState()
    
    var isValidating by remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf<Boolean?>(null) }
    var showApiKeyInput by remember { mutableStateOf(ApiKeyManager.getStoredApiKey() == null) }
    var apiKey by remember { mutableStateOf("") }
    var showSamplerSettings by remember { mutableStateOf(false) }
    var showModelDropdown by remember { mutableStateOf(false) }
    var showModelSetAsDefaultMessage by remember { mutableStateOf(false) }
    
    // Color picker state
    var showColorPicker by remember { mutableStateOf(false) }
    var currentColorTitle by remember { mutableStateOf("") }
    var currentColor by remember { mutableStateOf(Color.Transparent) }
    var onColorSelected by remember { mutableStateOf<(Color) -> Unit>({}) }

    // Collect all StateFlow values inside @Composable
    val darkMode by settingsManager.darkMode.collectAsState()
    val modelList by viewModel.modelList.collectAsState()
    val defaultModel by settingsManager.defaultModel.collectAsState()
    val saveDirectory by settingsManager.saveDirectory.collectAsState()
    val temperature by settingsManager.temperature.collectAsState()
    val maxTokens by settingsManager.maxTokens.collectAsState()
    val topP by settingsManager.topP.collectAsState()
    val topK by settingsManager.topK.collectAsState()

    // Theme colors
    val colorPrimary by settingsManager.colorPrimary.collectAsState()
    val colorOnPrimary by settingsManager.colorOnPrimary.collectAsState()
    val colorSecondary by settingsManager.colorSecondary.collectAsState()
    val colorOnSecondary by settingsManager.colorOnSecondary.collectAsState()
    val colorBackground by settingsManager.colorBackground.collectAsState()
    val colorOnBackground by settingsManager.colorOnBackground.collectAsState()
    val colorSurface by settingsManager.colorSurface.collectAsState()
    val colorOnSurface by settingsManager.colorOnSurface.collectAsState()

    LaunchedEffect(Unit) {
        if (!showApiKeyInput) {
            viewModel.fetchModelList()
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = currentColor,
            onColorSelected = onColorSelected,
            onDismiss = { showColorPicker = false },
            title = currentColorTitle
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ApiKeySection(
                showApiKeyInput = showApiKeyInput,
                apiKey = apiKey,
                isValidating = isValidating,
                validationResult = validationResult,
                repository = repository,
                viewModel = viewModel,
                scope = scope,
                onShowApiKeyInputChange = { showApiKeyInput = it },
                onApiKeyChange = { apiKey = it },
                onValidationResultChange = { validationResult = it },
                onIsValidatingChange = { isValidating = it }
            )

            ThemeColorsSection(
                colorPrimary = colorPrimary,
                colorOnPrimary = colorOnPrimary,
                colorSecondary = colorSecondary,
                colorOnSecondary = colorOnSecondary,
                colorBackground = colorBackground,
                colorOnBackground = colorOnBackground,
                colorSurface = colorSurface,
                colorOnSurface = colorOnSurface,
                settingsManager = settingsManager,
                scope = scope,
                onShowColorPickerChange = { showColorPicker = it },
                onCurrentColorTitleChange = { currentColorTitle = it },
                onCurrentColorChange = { currentColor = it },
                onColorSelectedChange = { onColorSelected = it }
            )

            SaveDirectorySection(
                saveDirectory = saveDirectory,
                settingsManager = settingsManager,
                scope = scope
            )

            ModelSelectionSection(
                modelList = modelList,
                defaultModel = defaultModel,
                showModelDropdown = showModelDropdown,
                showModelSetAsDefaultMessage = showModelSetAsDefaultMessage,
                settingsManager = settingsManager,
                scope = scope,
                onShowModelDropdownChange = { showModelDropdown = it },
                onShowModelSetAsDefaultMessageChange = { showModelSetAsDefaultMessage = it }
            )

            SamplerSettingsSection(
                showSamplerSettings = showSamplerSettings,
                temperature = temperature,
                maxTokens = maxTokens,
                topP = topP,
                topK = topK,
                settingsManager = settingsManager,
                scope = scope,
                onShowSamplerSettingsChange = { showSamplerSettings = it }
            )

            // Add some padding at the bottom for better scrolling
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
