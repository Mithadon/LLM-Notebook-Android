package com.llmnotebook.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Info Card
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Text(
                        text = "Version 0.2",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "Built on 2024-11-18",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Developed by Mithadon",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "A mobile interface for interacting with large language models through OpenRouter's API.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            // GitHub Links Card
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Source Code",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    TextButton(
                        onClick = {
                            uriHandler.openUri("https://github.com/Mithadon/LLM-Notebook")
                        }
                    ) {
                        Text("View Desktop Version on GitHub")
                    }
                    
                    TextButton(
                        onClick = {
                            uriHandler.openUri("https://github.com/Mithadon/LLM-Notebook-Android")
                        }
                    ) {
                        Text("View Android Version on GitHub")
                    }
                }
            }
            
            // Features Card
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    ListItem(
                        headlineContent = { Text("Chat Interface") },
                        supportingContent = { Text("A full-featured chat interface for interacting with language models") }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Model Selection") },
                        supportingContent = { Text("Choose from various language models available through OpenRouter") }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Parameter Customization") },
                        supportingContent = { Text("Fine-tune generation parameters like temperature and max tokens") }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Chat History") },
                        supportingContent = { Text("Save and load previous chat sessions") }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Export Options") },
                        supportingContent = { Text("Export chat history in various formats") }
                    )
                }

                            // To-do card
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Upcoming",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    ListItem(
                        headlineContent = { Text("Chat Interface") },
                        supportingContent = { Text("Add scrollbars to 'About', Notebook. Add font selection. Fix theme colors not being applied. Revamp aesthetics entirely. Fix regenerate to only remove one previous generation. Fix 'save as txt' crashing.") }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Model information") },
                        supportingContent = { Text("Display model description and cost for input/output tokens") }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Parameter Customization") },
                        supportingContent = { Text("Add default and custom presets. Add more samplers.") }
                    )
                    
                    ListItem(
                        headlineContent = { Text("OpenAI-compatible API") },
                        supportingContent = { Text("Add this local feature, especially for oobabooga-text-generation-webui") }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Style") },
                        supportingContent = { Text("Allow custom theme colors based on text formatting") }
                    )
                }
            }
        }
    }
}
