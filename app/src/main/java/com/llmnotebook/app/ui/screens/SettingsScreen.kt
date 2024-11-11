package com.llmnotebook.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.data.OpenRouterRepository
import com.llmnotebook.app.util.ApiKeyManager
import com.llmnotebook.app.util.SettingsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    settingsManager: SettingsManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { OpenRouterRepository() }
    
    var apiKey by remember { mutableStateOf(ApiKeyManager.getStoredApiKey() ?: "") }
    var isValidating by remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf<Boolean?>(null) }
    
    val darkMode by settingsManager.darkMode

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API Key Section
            OutlinedTextField(
                value = apiKey,
                onValueChange = { 
                    apiKey = it
                    validationResult = null
                },
                label = { Text("OpenRouter API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Button(
                onClick = {
                    scope.launch {
                        isValidating = true
                        validationResult = try {
                            val isValid = repository.validateApiKey(apiKey)
                            if (isValid) {
                                ApiKeyManager.storeApiKey(apiKey)
                            }
                            isValid
                        } catch (e: Exception) {
                            false
                        }
                        isValidating = false
                    }
                },
                enabled = apiKey.isNotBlank() && !isValidating
            ) {
                Text(if (isValidating) "Validating..." else "Validate and Save API Key")
            }
            
            validationResult?.let {
                Text(
                    text = if (it) "API key is valid" else "Invalid API key",
                    color = if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            // Theme Section
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected = darkMode == SettingsManager.DARK_MODE_SYSTEM,
                    onClick = { 
                        scope.launch {
                            settingsManager.setDarkMode(SettingsManager.DARK_MODE_SYSTEM)
                        }
                    }
                )
                Text(
                    text = "System Default",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected = darkMode == SettingsManager.DARK_MODE_ON,
                    onClick = { 
                        scope.launch {
                            settingsManager.setDarkMode(SettingsManager.DARK_MODE_ON)
                        }
                    }
                )
                Text(
                    text = "Dark",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected = darkMode == SettingsManager.DARK_MODE_OFF,
                    onClick = { 
                        scope.launch {
                            settingsManager.setDarkMode(SettingsManager.DARK_MODE_OFF)
                        }
                    }
                )
                Text(
                    text = "Light",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}
