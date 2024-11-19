package com.llmnotebook.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.data.OpenRouterRepository
import com.llmnotebook.app.util.ApiKeyManager
import com.llmnotebook.app.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ApiKeySection(
    showApiKeyInput: Boolean,
    apiKey: String,
    isValidating: Boolean,
    validationResult: Boolean?,
    repository: OpenRouterRepository,
    viewModel: MainViewModel,
    scope: CoroutineScope,
    onShowApiKeyInputChange: (Boolean) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onValidationResultChange: (Boolean?) -> Unit,
    onIsValidatingChange: (Boolean) -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "OpenRouter API Key",
                style = MaterialTheme.typography.titleMedium
            )

            if (showApiKeyInput) {
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { 
                        onApiKeyChange(it)
                        onValidationResultChange(null)
                    },
                    label = { Text("Enter API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Button(
                    onClick = {
                        scope.launch {
                            onIsValidatingChange(true)
                            val isValid = try {
                                val valid = repository.validateApiKey(apiKey)
                                if (valid) {
                                    ApiKeyManager.storeApiKey(apiKey)
                                    viewModel.fetchModelList()
                                    onShowApiKeyInputChange(false)
                                }
                                valid
                            } catch (e: Exception) {
                                false
                            }
                            onValidationResultChange(isValid)
                            if (!isValid) {
                                ApiKeyManager.clearApiKey()
                            }
                            onIsValidatingChange(false)
                        }
                    },
                    enabled = apiKey.isNotBlank() && !isValidating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isValidating) "Validating..." else "Validate and Save API Key")
                }
                
                validationResult?.let {
                    Text(
                        text = if (it) "API key is valid" else "Invalid API key",
                        color = if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "API key is stored and valid",
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(
                        onClick = {
                            ApiKeyManager.clearApiKey()
                            onShowApiKeyInputChange(true)
                            onApiKeyChange("")
                            onValidationResultChange(null)
                        }
                    ) {
                        Text("Clear API Key")
                    }
                }
            }
        }
    }
}
