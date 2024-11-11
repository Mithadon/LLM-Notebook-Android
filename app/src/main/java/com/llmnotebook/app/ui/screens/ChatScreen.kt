package com.llmnotebook.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.llmnotebook.app.R
import com.llmnotebook.app.ui.components.PulsatingText
import com.llmnotebook.app.util.SettingsManager
import com.llmnotebook.app.viewmodel.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToUpcoming: () -> Unit,
    settingsManager: SettingsManager,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showMenu = false
                                onNavigateToAbout()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Upcoming Features") },
                            onClick = {
                                showMenu = false
                                onNavigateToUpcoming()
                            }
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Chat interface coming soon!",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
