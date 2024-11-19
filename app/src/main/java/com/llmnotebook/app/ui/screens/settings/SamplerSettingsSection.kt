package com.llmnotebook.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.util.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SamplerSettingsSection(
    showSamplerSettings: Boolean,
    temperature: Float,
    maxTokens: Int,
    topP: Float,
    topK: Int,
    settingsManager: SettingsManager,
    scope: CoroutineScope,
    onShowSamplerSettingsChange: (Boolean) -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = { onShowSamplerSettingsChange(!showSamplerSettings) },
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
                        text = "Sampler Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        if (showSamplerSettings) Icons.Default.KeyboardArrowUp 
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (showSamplerSettings) "Hide settings" else "Show settings"
                    )
                }
            }

            if (showSamplerSettings) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Temperature
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Temperature: $temperature")
                        Slider(
                            value = temperature,
                            onValueChange = { 
                                scope.launch {
                                    settingsManager.setTemperature(it)
                                }
                            },
                            valueRange = 0f..2f,
                            steps = 39
                        )
                    }

                    // Max Tokens
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Max Tokens: $maxTokens")
                        Slider(
                            value = maxTokens.toFloat(),
                            onValueChange = { 
                                scope.launch {
                                    settingsManager.setMaxTokens(it.toInt())
                                }
                            },
                            valueRange = 100f..2000f,
                            steps = 38
                        )
                    }

                    // Top P
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Top P: $topP")
                        Slider(
                            value = topP,
                            onValueChange = { 
                                scope.launch {
                                    settingsManager.setTopP(it)
                                }
                            },
                            valueRange = 0f..1f,
                            steps = 19
                        )
                    }

                    // Top K
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Top K: $topK")
                        Slider(
                            value = topK.toFloat(),
                            onValueChange = { 
                                scope.launch {
                                    settingsManager.setTopK(it.toInt())
                                }
                            },
                            valueRange = 1f..100f,
                            steps = 98
                        )
                    }
                }
            }
        }
    }
}
