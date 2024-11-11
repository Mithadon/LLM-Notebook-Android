package com.llmnotebook.app.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenRouterApi {
    @GET("api/v1/auth/key")
    suspend fun validateApiKey(
        @Header("Authorization") authorization: String
    ): Response<Unit>
}
