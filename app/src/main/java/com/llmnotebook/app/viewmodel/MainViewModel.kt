package com.llmnotebook.app.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.llmnotebook.app.data.Model
import com.llmnotebook.app.data.OpenRouterRepository
import com.llmnotebook.app.util.ApiKeyManager
import com.llmnotebook.app.util.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class MainViewModel(
    private val settingsManager: SettingsManager,
    private val repository: OpenRouterRepository = OpenRouterRepository()
) : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _isValidatingApiKey = MutableStateFlow(false)
    val isValidatingApiKey: StateFlow<Boolean> = _isValidatingApiKey.asStateFlow()

    private val _apiKeyValidationResult = MutableStateFlow<Boolean?>(null)
    val apiKeyValidationResult: StateFlow<Boolean?> = _apiKeyValidationResult.asStateFlow()

    private val _modelList = MutableStateFlow<List<Model>>(listOf(Model.getDefaultModel()))
    val modelList: StateFlow<List<Model>> = _modelList.asStateFlow()

    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Connecting)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    var isGenerating by mutableStateOf(false)
        private set

    var tokenCount by mutableStateOf(0)
        private set

    private var generationJob: Job? = null
    private var lastPrompt: String? = null
    private var lastGeneratedText: StringBuilder = StringBuilder()
    private var baseText: String = ""

    init {
        // Initialize with default model and fetch model list if API key exists
        viewModelScope.launch {
            if (ApiKeyManager.getStoredApiKey() != null) {
                fetchModelList()
            } else {
                _connectionStatus.value = ConnectionStatus.NoApiKey
            }
        }
    }

    fun validateApiKey(apiKey: String? = null) {
        Log.d(TAG, "Validating API key")
        viewModelScope.launch {
            _isValidatingApiKey.value = true
            _connectionStatus.value = ConnectionStatus.Connecting
            try {
                val isValid = repository.validateApiKey(apiKey)
                Log.d(TAG, "API key validation result: $isValid")
                _apiKeyValidationResult.value = isValid
                if (isValid) {
                    fetchModelList()
                } else {
                    _connectionStatus.value = ConnectionStatus.InvalidApiKey
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error validating API key", e)
                _apiKeyValidationResult.value = false
                _connectionStatus.value = ConnectionStatus.Error(e.message ?: "Unknown error")
            } finally {
                _isValidatingApiKey.value = false
            }
        }
    }

    fun fetchModelList() {
        Log.d(TAG, "Fetching model list")
        viewModelScope.launch {
            _connectionStatus.value = ConnectionStatus.Connecting
            try {
                val models = repository.fetchModelList()
                Log.d(TAG, "Fetched ${models.size} models")
                _modelList.value = models
                
                // If no default model is set, set the first model as default
                if (settingsManager.defaultModel.value == null && models.isNotEmpty()) {
                    settingsManager.setDefaultModel(models.first().id)
                }
                
                _connectionStatus.value = ConnectionStatus.Connected
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching model list", e)
                _modelList.value = listOf(Model.getDefaultModel())
                _connectionStatus.value = ConnectionStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun generateText(prompt: String, onUpdate: (String) -> Unit) {
        lastPrompt = prompt
        baseText = prompt // Store the original text
        lastGeneratedText.clear() // Reset last generated text
        
        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            isGenerating = true
            try {
                repository.generateText(settingsManager, prompt)
                    .catch { e ->
                        Log.e(TAG, "Error during text generation", e)
                        _connectionStatus.value = ConnectionStatus.Error(e.message ?: "Unknown error")
                    }
                    .collect { generatedText ->
                        lastGeneratedText.append(generatedText)
                        onUpdate(generatedText)
                    }
            } finally {
                isGenerating = false
            }
        }
    }

    fun regenerateText(onUpdate: (String) -> Unit) {
        lastPrompt?.let { prompt ->
            // Remove the last generated text from the base text
            if (lastGeneratedText.isNotEmpty()) {
                val lastGenerated = lastGeneratedText.toString()
                if (baseText.endsWith(lastGenerated)) {
                    baseText = baseText.substring(0, baseText.length - lastGenerated.length)
                }
            }
            // Generate new text starting from the base text
            generateText(baseText, onUpdate)
        }
    }

    fun toggleGeneration(prompt: String, onUpdate: (String) -> Unit) {
        if (isGenerating) {
            stopGeneration()
        } else {
            generateText(prompt, onUpdate)
        }
    }

    fun stopGeneration() {
        viewModelScope.launch {
            generationJob?.cancelAndJoin()
            isGenerating = false
        }
    }

    fun resetValidation() {
        _apiKeyValidationResult.value = null
    }

    sealed class ConnectionStatus {
        object Connecting : ConnectionStatus()
        object Connected : ConnectionStatus()
        object NoApiKey : ConnectionStatus()
        object InvalidApiKey : ConnectionStatus()
        data class Error(val message: String) : ConnectionStatus()
    }

    class Factory(private val settingsManager: SettingsManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(settingsManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
