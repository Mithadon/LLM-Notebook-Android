package com.llmnotebook.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.R
import com.llmnotebook.app.ui.components.PulsatingText
import com.llmnotebook.app.util.SettingsManager
import com.llmnotebook.app.viewmodel.MainViewModel
import com.llmnotebook.app.viewmodel.MainViewModel.ConnectionStatus
import java.io.File
import java.io.FileWriter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    settingsManager: SettingsManager,
    viewModel: MainViewModel
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var showMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val saveDirectory by settingsManager.saveDirectory.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            actions = {
                // Connection status indicator
                ConnectionStatusIndicator(connectionStatus)

                // Menu Icon
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    // Save option
                    DropdownMenuItem(
                        text = { Text("Save as TXT") },
                        onClick = {
                            val file = File(saveDirectory ?: "", "llm_notebook_${System.currentTimeMillis()}.txt")
                            FileWriter(file).use { it.write(textFieldValue.text) }
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Create,
                                contentDescription = "Save"
                            )
                        }
                    )

                    HorizontalDivider()

                    // Settings option
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            onNavigateToSettings()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    )

                    // About option
                    DropdownMenuItem(
                        text = { Text("About") },
                        onClick = {
                            onNavigateToAbout()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "About"
                            )
                        }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main text input/output area
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                if (!viewModel.isGenerating) {
                    textFieldValue = newValue
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
            enabled = !viewModel.isGenerating && connectionStatus == ConnectionStatus.Connected,
            readOnly = viewModel.isGenerating,
            label = { Text("Write or paste your text here...") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Token counter
        Text(
            text = "Tokens: ${viewModel.tokenCount}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Combined Continue/Stop button
            Button(
                onClick = {
                    scope.launch {
                        viewModel.toggleGeneration(textFieldValue.text) { generatedText ->
                            textFieldValue = TextFieldValue(
                                text = textFieldValue.text + generatedText,
                                selection = TextRange(textFieldValue.text.length + generatedText.length)
                            )
                            // Scroll to bottom
                            scope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        }
                    }
                },
                enabled = textFieldValue.text.isNotEmpty() && connectionStatus == ConnectionStatus.Connected,
                modifier = Modifier.weight(1f)
            ) {
                if (viewModel.isGenerating) {
                    PulsatingText {
                        Text("Stop")
                    }
                } else {
                    Text("Continue Writing")
                }
            }

            // Regenerate button
            Button(
                onClick = {
                    scope.launch {
                        viewModel.regenerateText { generatedText ->
                            textFieldValue = TextFieldValue(
                                text = generatedText,
                                selection = TextRange(generatedText.length)
                            )
                            // Scroll to bottom
                            scope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        }
                    }
                },
                enabled = !viewModel.isGenerating && textFieldValue.text.isNotEmpty() && connectionStatus == ConnectionStatus.Connected,
                modifier = Modifier.weight(1f)
            ) {
                Text("Regenerate")
            }
        }

        // Model and connection status display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val defaultModel by settingsManager.defaultModel.collectAsState()
            Text(
                text = "Model: ${defaultModel ?: "Not set"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ConnectionStatusIndicator(status: ConnectionStatus) {
    val (icon, tint, description) = when (status) {
        is ConnectionStatus.Connecting -> Triple(
            Icons.Default.Refresh,
            MaterialTheme.colorScheme.primary,
            "Connecting to OpenRouter"
        )
        is ConnectionStatus.Connected -> Triple(
            Icons.Default.CheckCircle,
            MaterialTheme.colorScheme.primary,
            "Connected to OpenRouter"
        )
        is ConnectionStatus.NoApiKey -> Triple(
            Icons.Default.Warning,
            MaterialTheme.colorScheme.error,
            "No API key set"
        )
        is ConnectionStatus.InvalidApiKey -> Triple(
            Icons.Default.Warning,
            MaterialTheme.colorScheme.error,
            "Invalid API key"
        )
        is ConnectionStatus.Error -> Triple(
            Icons.Default.Warning,
            MaterialTheme.colorScheme.error,
            status.message
        )
    }

    IconButton(
        onClick = {},
        enabled = false
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = tint
        )
    }
}
