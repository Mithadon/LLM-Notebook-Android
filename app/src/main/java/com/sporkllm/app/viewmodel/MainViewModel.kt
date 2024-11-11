package com.sporkllm.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sporkllm.app.data.OpenRouterRepository
import com.sporkllm.app.util.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {
    private val repository = OpenRouterRepository()
    private var currentJob: Job? = null
    private var generatedText = ""

    var isGenerating by mutableStateOf(false)
        private set

    var tokenCount by mutableStateOf(0)
        private set

    fun generateText(baseText: String, onUpdate: (String) -> Unit) {
        if (isGenerating) return

        isGenerating = true
        generatedText = ""

        currentJob = viewModelScope.launch {
            try {
                repository.generateCompletionStream(
                    text = baseText,
                    model = settingsManager.selectedModel.value,
                    temperature = settingsManager.temperature.value,
                    maxTokens = settingsManager.maxTokens.value,
                    topP = settingsManager.topP.value,
                    topK = settingsManager.topK.value
                )
                .catch { e ->
                    e.printStackTrace()
                    isGenerating = false
                }
                .collect { newContent ->
                    generatedText += newContent
                    onUpdate(generatedText)
                    // Rough token count estimation (1 token ≈ 4 characters)
                    tokenCount = generatedText.length / 4
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isGenerating = false
            }
        }
    }

    fun stopGeneration() {
        currentJob?.cancel()
        currentJob = null
        isGenerating = false
    }

    override fun onCleared() {
        super.onCleared()
        stopGeneration()
    }

    class Factory(private val settingsManager: SettingsManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(settingsManager) as T
        }
    }
}
