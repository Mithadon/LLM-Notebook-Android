package com.llmnotebook.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.llmnotebook.app.ui.screens.AboutScreen
import com.llmnotebook.app.ui.screens.ChatScreen
import com.llmnotebook.app.ui.screens.SettingsScreen
import com.llmnotebook.app.ui.theme.LLMNotebookTheme
import com.llmnotebook.app.util.ApiKeyManager
import com.llmnotebook.app.util.SettingsManager
import com.llmnotebook.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsManager = SettingsManager(this)
        ApiKeyManager.init(this)

        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(settingsManager)
            )
            val darkMode by settingsManager.darkMode.collectAsState()

            LLMNotebookTheme(darkMode = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "chat"
                    ) {
                        composable("chat") {
                            ChatScreen(
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToAbout = { navController.navigate("about") },
                                settingsManager = settingsManager,
                                viewModel = viewModel
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                settingsManager = settingsManager,
                                viewModel = viewModel
                            )
                        }
                        composable("about") {
                            AboutScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
