package com.sporkllm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sporkllm.app.ui.screens.*
import com.sporkllm.app.ui.theme.SporkLLMTheme
import com.sporkllm.app.util.ApiKeyManager
import com.sporkllm.app.util.SettingsManager
import com.sporkllm.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    
    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(settingsManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize managers
        ApiKeyManager.init(applicationContext)
        settingsManager = SettingsManager(applicationContext)

        setContent {
            val context = LocalContext.current
            var shouldRecreate by remember { mutableStateOf(false) }
            val systemInDarkTheme = isSystemInDarkTheme()
            val darkMode by settingsManager.darkMode
            
            val isDarkTheme = when (darkMode) {
                SettingsManager.DARK_MODE_ON -> true
                SettingsManager.DARK_MODE_OFF -> false
                else -> systemInDarkTheme
            }

            // Set up theme change callback
            DisposableEffect(Unit) {
                settingsManager.setOnThemeChangeCallback {
                    shouldRecreate = true
                }
                onDispose {
                    settingsManager.setOnThemeChangeCallback(null)
                }
            }

            // Handle theme recreation
            LaunchedEffect(shouldRecreate) {
                if (shouldRecreate) {
                    shouldRecreate = false
                    recreate()
                }
            }

            SporkLLMTheme(darkTheme = isDarkTheme) {
                Surface {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "chat"
                    ) {
                        composable("chat") {
                            ChatScreen(
                                onNavigateToSettings = {
                                    navController.navigate("settings")
                                },
                                onNavigateToAbout = {
                                    navController.navigate("about")
                                },
                                onNavigateToUpcoming = {
                                    navController.navigate("upcoming")
                                },
                                settingsManager = settingsManager,
                                viewModel = viewModel
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                settingsManager = settingsManager
                            )
                        }
                        composable("about") {
                            AboutScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("upcoming") {
                            UpcomingScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
