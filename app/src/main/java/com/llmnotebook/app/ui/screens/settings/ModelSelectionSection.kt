package com.llmnotebook.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.data.Model
import com.llmnotebook.app.util.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectionSection(
    modelList: List<Model>?,
    defaultModel: String?,
    showModelDropdown: Boolean,
    showModelSetAsDefaultMessage: Boolean,
    settingsManager: SettingsManager,
    scope: CoroutineScope,
    onShowModelDropdownChange: (Boolean) -> Unit,
    onShowModelSetAsDefaultMessageChange: (Boolean) -> Unit
) {
    modelList?.let { models ->
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Model",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = showModelDropdown,
                            onExpandedChange = { onShowModelDropdownChange(!showModelDropdown) }
                        ) {
                            OutlinedTextField(
                                value = models.find { it.id == defaultModel }?.name ?: "Select Model",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showModelDropdown) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = showModelDropdown,
                                onDismissRequest = { onShowModelDropdownChange(false) }
                            ) {
                                models.forEach { model ->
                                    DropdownMenuItem(
                                        text = { Text(model.name) },
                                        onClick = {
                                            scope.launch {
                                                settingsManager.setDefaultModel(model.id)
                                                onShowModelSetAsDefaultMessageChange(true)
                                            }
                                            onShowModelDropdownChange(false)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                defaultModel?.let { modelId ->
                                    settingsManager.setDefaultModel(modelId)
                                    onShowModelSetAsDefaultMessageChange(true)
                                }
                            }
                        },
                        enabled = defaultModel != null
                    ) {
                        Text("Set as Default")
                    }
                }

                if (showModelSetAsDefaultMessage) {
                    Text(
                        text = "Model set as default!",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
