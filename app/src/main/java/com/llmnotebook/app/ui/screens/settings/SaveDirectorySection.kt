package com.llmnotebook.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.ui.components.DirectoryPicker
import com.llmnotebook.app.util.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SaveDirectorySection(
    saveDirectory: String?,
    settingsManager: SettingsManager,
    scope: CoroutineScope
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Save Directory",
                style = MaterialTheme.typography.titleMedium
            )

            DirectoryPicker(
                currentPath = saveDirectory,
                onDirectorySelected = { path ->
                    scope.launch {
                        settingsManager.setSaveDirectory(path)
                    }
                }
            )
        }
    }
}
