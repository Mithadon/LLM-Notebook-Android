package com.sporkllm.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sporkllm.app.R
import com.sporkllm.app.ui.components.PulsatingText
import com.sporkllm.app.util.SettingsManager
import com.sporkllm.app.viewmodel.MainViewModel
import java.io.File
import java.io.FileWriter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToUpcoming: () -> Unit,
    settingsManager: SettingsManager,
    viewModel: MainViewModel
) {
    var textFieldValue by remember { 
        mutableStateOf(TextFieldValue(""))
    }
    var showMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            actions = {
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
                            val file = File(settingsManager.saveDirectory.value, "llm_notebook_${System.currentTimeMillis()}.txt")
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

                    Divider()

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

                    // Upcoming features option
                    DropdownMenuItem(
                        text = { Text("Upcoming Features") },
                        onClick = {
                            onNavigateToUpcoming()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "Upcoming Features"
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
            enabled = !viewModel.isGenerating,
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    val baseText = textFieldValue.text
                    scope.launch {
                        viewModel.generateText(baseText) { generatedText ->
                            textFieldValue = TextFieldValue(
                                text = baseText + generatedText,
                                selection = TextRange(baseText.length + generatedText.length)
                            )
                            // Scroll to bottom
                            scope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        }
                    }
                },
                enabled = !viewModel.isGenerating && textFieldValue.text.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                if (viewModel.isGenerating) {
                    PulsatingText {
                        Text("Generating...")
                    }
                } else {
                    Text("Continue Writing")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.stopGeneration() },
                enabled = viewModel.isGenerating,
                modifier = Modifier.weight(1f)
            ) {
                Text("Stop")
            }
        }

        // Model display
        Text(
            text = "Model: ${settingsManager.selectedModel.value}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
