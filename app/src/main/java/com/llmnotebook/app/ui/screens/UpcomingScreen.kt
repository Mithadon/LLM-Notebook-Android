package com.llmnotebook.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upcoming Features") },
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
    }
}
