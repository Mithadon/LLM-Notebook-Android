package com.llmnotebook.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.R
import com.llmnotebook.app.util.SettingsManager

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
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = "A mobile interface for interacting with large language models through OpenRouter's API.",
                style = MaterialTheme.typography.bodyLarge
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
}
