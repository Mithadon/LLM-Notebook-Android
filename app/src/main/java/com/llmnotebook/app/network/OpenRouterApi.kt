package com.llmnotebook.app.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Body
import retrofit2.http.Streaming

interface OpenRouterApi {
    @GET("auth/key")
    suspend fun validateApiKey(
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @GET("models")
    suspend fun getModels(
        @Header("Authorization") authorization: String
    ): Response<ModelsResponse>

    @Streaming
    @POST("chat/completions")
    suspend fun generateText(
        @Header("Authorization") authorization: String,
        @Body request: GenerateRequest
    ): Response<ResponseBody>
}
