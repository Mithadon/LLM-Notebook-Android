package com.sporkllm.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sporkllm.app.data.OpenRouterRepository
import com.sporkllm.app.util.ApiKeyManager
import com.sporkllm.app.util.SettingsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    settingsManager: SettingsManager
) {
    var apiKey by remember { mutableStateOf("") }
    var showSavedMessage by remember { mutableStateOf(false) }
    var expandedSamplers by remember { mutableStateOf(false) }
    var showModelSetAsDefaultMessage by remember { mutableStateOf(false) }
    
    val repository = remember { OpenRouterRepository() }
    val scope = rememberCoroutineScope()
    var availableModels by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingModels by remember { mutableStateOf(true) }

    // Load models when API key changes
    LaunchedEffect(Unit) {
        scope.launch {
            isLoadingModels = true
            try {
                availableModels = repository.getAvailableModels()
            } finally {
                isLoadingModels = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // API Key Section
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("OpenRouter API Key") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    ApiKeyManager.setApiKey(apiKey)
                    showSavedMessage = true
                    // Refresh models when API key changes
                    scope.launch {
                        isLoadingModels = true
                        try {
                            availableModels = repository.getAvailableModels()
                        } finally {
                            isLoadingModels = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save API Key")
            }

            if (showSavedMessage) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "API Key saved successfully!",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dark Mode Section
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var expanded by remember { mutableStateOf(false) }
                val items = listOf("System", "Light", "Dark")
                val selectedIndex = settingsManager.darkMode.value

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = items[selectedIndex],
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        items.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    settingsManager.setDarkMode(index)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Directory Section
            Text(
                text = "Save Directory",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = settingsManager.saveDirectory.value,
                onValueChange = { settingsManager.setSaveDirectory(it) },
                label = { Text("Default Save Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Model Selection Section
            Text(
                text = "Model",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoadingModels) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedModel = settingsManager.selectedModel.value

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedModel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            availableModels.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model) },
                                    onClick = {
                                        settingsManager.setModel(model)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            showModelSetAsDefaultMessage = true
                        }
                    ) {
                        Text("Set as Default")
                    }
                }
            }

            if (showModelSetAsDefaultMessage) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Model set as default!",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Samplers Section
            Surface(
                onClick = { expandedSamplers = !expandedSamplers },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Samplers",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = if (expandedSamplers) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expandedSamplers) "Collapse" else "Expand"
                    )
                }
            }

            if (expandedSamplers) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    // Temperature
                    Text("Temperature: ${settingsManager.temperature.value}")
                    Slider(
                        value = settingsManager.temperature.value,
                        onValueChange = { settingsManager.setTemperature(it) },
                        valueRange = 0f..1f,
                        steps = 20
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Max Tokens
                    Text("Max Tokens: ${settingsManager.maxTokens.value}")
                    Slider(
                        value = settingsManager.maxTokens.value.toFloat(),
                        onValueChange = { settingsManager.setMaxTokens(it.toInt()) },
                        valueRange = 100f..2000f,
                        steps = 19
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Top P
                    Text("Top P: ${settingsManager.topP.value}")
                    Slider(
                        value = settingsManager.topP.value,
                        onValueChange = { settingsManager.setTopP(it) },
                        valueRange = 0f..1f,
                        steps = 20
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Top K
                    Text("Top K: ${settingsManager.topK.value}")
                    Slider(
                        value = settingsManager.topK.value.toFloat(),
                        onValueChange = { settingsManager.setTopK(it.toInt()) },
                        valueRange = 1f..100f,
                        steps = 99
                    )
                }
            }

            // Add some padding at the bottom for better scrolling
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
