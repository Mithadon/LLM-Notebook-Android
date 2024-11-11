package com.sporkllm.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Upcoming Features") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            FeatureItem(
                title = "TTS Implementation",
                description = "Implement text-to-speech narration"
            )

            FeatureItem(
                title = "Save Path Selection",
                description = "Add browse button to select save path"
            )

            FeatureItem(
                title = "System Theme Colors",
                description = "Use the system's theme colors, not just Light/Dark"
            )

            FeatureItem(
                title = "Text Customization",
                description = "Add options for font size, font type, and line spacing"
            )

            FeatureItem(
                title = "Custom App Icon",
                description = "Design and implement a unique app icon"
            )
        }
    }
}

@Composable
private fun FeatureItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
