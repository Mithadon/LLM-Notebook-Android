package com.llmnotebook.app.data

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.llmnotebook.app.network.*
import com.llmnotebook.app.util.ApiKeyManager
import com.llmnotebook.app.util.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import okhttp3.ResponseBody
import retrofit2.Response

class OpenRouterRepository {
    companion object {
        private const val TAG = "OpenRouterRepository"
    }

    suspend fun validateApiKey(apiKey: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val key = apiKey ?: ApiKeyManager.getStoredApiKey() ?: return@withContext false
                Log.d(TAG, "Validating API key: ${key.take(5)}...")
                
                val response = RetrofitClient.openRouterApi.validateApiKey("Bearer $key")
                Log.d(TAG, "Validation response: ${response.code()} ${response.message()}")
                
                response.isSuccessful
            } catch (e: Exception) {
                Log.e(TAG, "Error validating API key", e)
                when (e) {
                    is HttpException -> {
                        Log.e(TAG, "HTTP error code: ${e.code()}")
                        false
                    }
                    is IOException -> {
                        Log.e(TAG, "IO Exception: ${e.message}")
                        false
                    }
                    is JsonSyntaxException -> {
                        Log.e(TAG, "JSON parsing error: ${e.message}")
                        false
                    }
                    else -> throw e
                }
            }
        }
    }

    suspend fun fetchModelList(): List<Model> {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = ApiKeyManager.getStoredApiKey() ?: return@withContext listOf(Model.getDefaultModel())
                Log.d(TAG, "Fetching models with API key: ${apiKey.take(5)}...")
                
                val response = RetrofitClient.openRouterApi.getModels("Bearer $apiKey")
                Log.d(TAG, "Models response: ${response.code()} ${response.message()}")
                
                if (response.isSuccessful) {
                    val models = response.body()?.data
                    if (models.isNullOrEmpty()) {
                        Log.d(TAG, "No models found, using default model")
                        listOf(Model.getDefaultModel())
                    } else {
                        models
                    }
                } else {
                    Log.e(TAG, "Error fetching models: ${response.code()} ${response.message()}")
                    listOf(Model.getDefaultModel())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching models", e)
                when (e) {
                    is HttpException -> {
                        Log.e(TAG, "HTTP error code: ${e.code()}")
                    }
                    is IOException -> {
                        Log.e(TAG, "IO Exception: ${e.message}")
                    }
                    is JsonSyntaxException -> {
                        Log.e(TAG, "JSON parsing error: ${e.message}")
                    }
                    else -> Log.e(TAG, "Unknown error", e)
                }
                listOf(Model.getDefaultModel())
            }
        }
    }

    suspend fun generateText(settingsManager: SettingsManager, prompt: String): Flow<String> = flow {
        try {
            val apiKey = ApiKeyManager.getStoredApiKey() ?: throw IllegalStateException("No API key found")
            val request = GenerateRequest.create(settingsManager, prompt)
            
            Log.d(TAG, "Generating text with request: $request")
            val response: Response<ResponseBody> = RetrofitClient.openRouterApi.generateText(
                authorization = "Bearer $apiKey",
                request = request
            )
            
            if (response.isSuccessful) {
                val responseBody = response.body() ?: throw IllegalStateException("Empty response body")
                val reader = responseBody.charStream().buffered()
                
                // Read the stream line by line
                reader.use { r ->
                    var line: String?
                    while (r.readLine().also { line = it } != null) {
                        if (line!!.isNotEmpty() && line!!.startsWith("data: ")) {
                            val jsonData = line!!.substring(6).trim() // Remove "data: " prefix
                            Log.d(TAG, "Received SSE data: $jsonData")
                            
                            if (jsonData == "[DONE]") {
                                break
                            }
                            
                            try {
                                val streamResponse = RetrofitClient.gson.fromJson(jsonData, GenerateResponse::class.java)
                                val choice = streamResponse.choices.firstOrNull()
                                val content = choice?.delta?.content
                                
                                if (!content.isNullOrEmpty()) {
                                    Log.d(TAG, "Emitting content: $content")
                                    emit(content)
                                }
                                
                                if (choice?.finishReason != null) {
                                    Log.d(TAG, "Generation finished with reason: ${choice.finishReason}")
                                    break
                                }
                            } catch (e: JsonSyntaxException) {
                                Log.e(TAG, "Error parsing stream response: $jsonData", e)
                            }
                        }
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Error response: ${response.code()} ${response.message()} $errorBody")
                throw HttpException(response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating text", e)
            throw e
        }
    }
}