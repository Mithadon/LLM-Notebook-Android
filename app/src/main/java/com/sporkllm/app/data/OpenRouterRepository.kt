package com.sporkllm.app.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.sporkllm.app.network.*
import com.sporkllm.app.util.ApiKeyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class OpenRouterRepository {
    private val api = RetrofitClient.openRouterApi
    private val gson = Gson()
    private val TAG = "OpenRouterRepository"

    // Cache for models
    private var cachedModels: List<String>? = null
    private val modelsMutex = Mutex()

    suspend fun getAvailableModels(): List<String> {
        if (!ApiKeyManager.hasApiKey()) {
            Log.w(TAG, "No API key set")
            return emptyList()
        }

        return modelsMutex.withLock {
            try {
                Log.d(TAG, "Fetching available models from OpenRouter API")
                val response = api.getModels()
                Log.d(TAG, "Received models response: $response")
                
                val models = response.data
                    .filter { it.id != null }
                    .sortedWith(compareByDescending<ModelInfo> { it.id.contains("claude-3") }
                        .thenByDescending { it.id.contains("claude") }
                        .thenByDescending { it.id.contains("gpt-4") }
                        .thenByDescending { it.id.contains("mistral") })
                    .map { it.id }

                Log.d(TAG, "Processed ${models.size} models: $models")
                cachedModels = models
                models
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching models", e)
                e.printStackTrace()
                
                // If we have cached models, use them as fallback
                cachedModels?.let {
                    Log.d(TAG, "Using cached models: $it")
                    return@withLock it
                }

                // Otherwise, return a default list
                Log.d(TAG, "Using default models list")
                listOf(
                    "anthropic/claude-3-opus-20240229",
                    "anthropic/claude-3-sonnet-20240229",
                    "anthropic/claude-2.1",
                    "mistral/mistral-large-latest"
                )
            }
        }
    }

    fun generateCompletionStream(
        text: String,
        model: String,
        temperature: Float,
        maxTokens: Int,
        topP: Float,
        topK: Int
    ): Flow<String> = flow {
        if (!ApiKeyManager.hasApiKey()) {
            throw Exception("No API key set")
        }

        val request = CompletionRequest(
            model = model,
            messages = listOf(
                Message(
                    role = "system",
                    content = "You are a helpful writing assistant. Continue the text in a natural way, maintaining the same style and tone. Generate a substantial amount of text, at least several paragraphs."
                ),
                Message(
                    role = "user",
                    content = text
                )
            ),
            stream = true,
            temperature = temperature.toDouble(),
            max_tokens = maxTokens,
            top_p = topP.toDouble(),
            top_k = topK
        )

        try {
            Log.d(TAG, "Starting stream request with model: $model")
            val response = api.generateCompletionStream(request)
            
            if (!response.isSuccessful) {
                Log.e(TAG, "Error response: ${response.code()} - ${response.message()}")
                throw Exception("API request failed with code ${response.code()}")
            }

            val responseBody = response.body() ?: throw Exception("Response body is null")
            val reader = BufferedReader(
                InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8)
            )

            var lineCount = 0
            var accumulatedContent = ""
            var foundStart = false

            reader.use { bufferedReader ->
                while (true) {
                    val line = bufferedReader.readLine() ?: break
                    lineCount++
                    Log.d(TAG, "Raw line $lineCount: $line")

                    if (line.startsWith("data: ")) {
                        val jsonData = line.substring(6).trim()
                        if (jsonData == "[DONE]") {
                            Log.d(TAG, "Stream completed")
                            break
                        }

                        try {
                            val streamResponse = gson.fromJson(jsonData, StreamResponse::class.java)
                            val content = streamResponse.choices.firstOrNull()?.delta?.content
                            
                            if (!content.isNullOrEmpty()) {
                                accumulatedContent += content
                                
                                // Check if we've found where the actual new content starts
                                if (!foundStart) {
                                    if (accumulatedContent.length >= text.length) {
                                        // If we've accumulated enough content to match the input text
                                        if (accumulatedContent.startsWith(text)) {
                                            // Skip the input text portion
                                            val newContent = accumulatedContent.substring(text.length)
                                            if (newContent.isNotEmpty()) {
                                                foundStart = true
                                                emit(newContent)
                                            }
                                        } else {
                                            // If it doesn't match the input text, we can start emitting
                                            foundStart = true
                                            emit(accumulatedContent)
                                        }
                                        accumulatedContent = ""
                                    }
                                } else {
                                    // We've already found the start, emit normally
                                    Log.d(TAG, "Emitting content: $content")
                                    emit(content)
                                    delay(100) // 10 updates per second
                                }
                            }
                        } catch (e: JsonSyntaxException) {
                            Log.e(TAG, "Error parsing JSON: ${e.message}")
                            Log.e(TAG, "Raw JSON: $jsonData")
                            continue
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing line: ${e.message}")
                            e.printStackTrace()
                            continue
                        }
                    }
                }
            }

            Log.d(TAG, "Processed $lineCount lines total")
        } catch (e: Exception) {
            Log.e(TAG, "Stream error: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }.flowOn(Dispatchers.IO)
}
