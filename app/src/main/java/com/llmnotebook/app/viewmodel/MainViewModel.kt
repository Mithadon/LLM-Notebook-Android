package com.llmnotebook.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.llmnotebook.app.data.OpenRouterRepository
import com.llmnotebook.app.util.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsManager: SettingsManager,
    private val repository: OpenRouterRepository = OpenRouterRepository()
) : ViewModel() {

    private val _isValidatingApiKey = MutableStateFlow(false)
    val isValidatingApiKey: StateFlow<Boolean> = _isValidatingApiKey.asStateFlow()

    private val _apiKeyValidationResult = MutableStateFlow<Boolean?>(null)
    val apiKeyValidationResult: StateFlow<Boolean?> = _apiKeyValidationResult.asStateFlow()

    private var validationJob: Job? = null

    fun validateApiKey(apiKey: String) {
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            _isValidatingApiKey.value = true
            try {
                val isValid = repository.validateApiKey(apiKey)
                _apiKeyValidationResult.value = isValid
            } catch (e: Exception) {
                _apiKeyValidationResult.value = false
            } finally {
                _isValidatingApiKey.value = false
            }
        }
    }

    fun resetValidation() {
        _apiKeyValidationResult.value = null
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
