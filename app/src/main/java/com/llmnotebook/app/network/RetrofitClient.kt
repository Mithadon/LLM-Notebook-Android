package com.llmnotebook.app.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.llmnotebook.app.util.ApiKeyManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://openrouter.ai/api/v1/"
    private const val TAG = "RetrofitClient"

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            
            // Don't modify the Authorization header if it's already set (for validation)
            val request = if (original.header("Authorization") != null) {
                original
            } else {
                val apiKey = ApiKeyManager.getStoredApiKey()
                Log.d(TAG, "Using stored API key: ${apiKey?.take(5)}...")
                
                original.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .build()
            }

            // Add common headers
            val finalRequest = request.newBuilder()
                .header("HTTP-Referer", "android://com.llmnotebook.app")
                .header("X-Title", "LLM-Notebook Android")
                .header("Accept", "text/event-stream")
                .header("Content-Type", "application/json")
                .build()

            chain.proceed(finalRequest)
        }
        .addInterceptor(loggingInterceptor)
        // Increase timeouts for streaming
        .readTimeout(0, TimeUnit.SECONDS) // No timeout for streaming
        .writeTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        // Disable retries for streaming
        .retryOnConnectionFailure(false)
        .build()

    val gson: Gson = GsonBuilder()
        .setLenient() // Important for parsing streaming responses
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val openRouterApi: OpenRouterApi = retrofit.create(OpenRouterApi::class.java)
}
