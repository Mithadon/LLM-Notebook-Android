package com.sporkllm.app.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

object ApiKeyManager {
    private const val TAG = "ApiKeyManager"
    private const val PREFS_NAME = "api_key_prefs"
    private const val KEY_API_KEY = "openrouter_api_key"
    
    private lateinit var prefs: SharedPreferences
    private var _apiKey by mutableStateOf<String?>(null)

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _apiKey = prefs.getString(KEY_API_KEY, null)
        Log.d(TAG, "Initialized with API key: ${_apiKey?.take(5)}...")
    }

    fun setApiKey(key: String) {
        _apiKey = key
        prefs.edit(commit = true) {
            putString(KEY_API_KEY, key)
        }
        Log.d(TAG, "Saved new API key: ${key.take(5)}...")
    }

    fun getStoredApiKey(): String? {
        if (!this::prefs.isInitialized) {
            Log.e(TAG, "ApiKeyManager not initialized!")
            return null
        }
        // Always read from preferences to ensure we have the latest value
        _apiKey = prefs.getString(KEY_API_KEY, null)
        Log.d(TAG, "Retrieved API key: ${_apiKey?.take(5)}...")
        return _apiKey
    }

    fun hasApiKey(): Boolean {
        return !getStoredApiKey().isNullOrEmpty()
    }

    fun clearApiKey() {
        _apiKey = null
        prefs.edit(commit = true) {
            remove(KEY_API_KEY)
        }
        Log.d(TAG, "Cleared API key")
    }
}
