package com.sporkllm.app.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.sporkllm.app.util.ApiKeyManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://openrouter.ai/"
    private const val TAG = "RetrofitClient"

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val apiKey = ApiKeyManager.getStoredApiKey()
            Log.d(TAG, "Using API key: ${apiKey?.take(5)}...")
            
            val request = original.newBuilder()
                .header("Authorization", "Bearer ${apiKey}")
                .header("HTTP-Referer", "android://com.sporkllm.app")
                .header("X-Title", "LLM-Notebook Android")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        // Increase timeouts for streaming
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        // Disable retries for streaming
        .retryOnConnectionFailure(false)
        .build()

    private val gson = GsonBuilder()
        .setLenient() // Important for parsing streaming responses
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val openRouterApi: OpenRouterApi = retrofit.create(OpenRouterApi::class.java)
}
