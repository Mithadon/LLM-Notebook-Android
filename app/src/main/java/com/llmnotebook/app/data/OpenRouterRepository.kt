package com.llmnotebook.app.data

import com.google.gson.JsonSyntaxException
import com.llmnotebook.app.network.*
import com.llmnotebook.app.util.ApiKeyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class OpenRouterRepository {
    suspend fun validateApiKey(apiKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.openRouterApi.validateApiKey("Bearer $apiKey")
                response.isSuccessful
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> false
                    is IOException -> false
                    is JsonSyntaxException -> false
                    else -> throw e
                }
            }
        }
    }
}
