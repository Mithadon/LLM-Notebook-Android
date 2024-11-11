package com.sporkllm.app.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenRouterApi {
    @Streaming
    @Headers(
        "Accept: text/event-stream",
        "Cache-Control: no-cache",
        "Connection: keep-alive"
    )
    @POST("api/v1/chat/completions")
    suspend fun generateCompletionStream(
        @Body request: CompletionRequest
    ): Response<ResponseBody>

    @GET("api/v1/models")
    suspend fun getModels(): ModelsResponse
}

data class CompletionRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = true,
    val temperature: Double = 0.7,
    val max_tokens: Int = 500,
    val top_p: Double = 0.9,
    val top_k: Int = 40
)

data class Message(
    val role: String,
    val content: String
)

data class StreamResponse(
    val choices: List<StreamChoice>
)

data class StreamChoice(
    val delta: Delta,
    val finish_reason: String?
)

data class Delta(
    val content: String?
)

data class ModelsResponse(
    val data: List<ModelInfo>
)

data class ModelInfo(
    val id: String,
    val name: String,
    val description: String?,
    val pricing: ModelPricing?,
    val context_length: Int?,
    val architecture: ModelArchitecture?
)

data class ModelPricing(
    val prompt: String?,
    val completion: String?,
    val image: String?,
    val request: String?
)

data class ModelArchitecture(
    val modality: String?,
    val tokenizer: String?,
    val instruct_type: String?
)
