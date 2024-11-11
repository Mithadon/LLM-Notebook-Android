package com.llmnotebook.app.util

import android.content.Context
import android.content.SharedPreferences

object ApiKeyManager {
    private const val PREFS_NAME = "api_key_prefs"
    private const val API_KEY = "api_key"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun storeApiKey(apiKey: String) {
        prefs.edit().putString(API_KEY, apiKey).apply()
    }

    fun getStoredApiKey(): String? {
        return prefs.getString(API_KEY, null)
    }

    fun clearApiKey() {
        prefs.edit().remove(API_KEY).apply()
    }
}
