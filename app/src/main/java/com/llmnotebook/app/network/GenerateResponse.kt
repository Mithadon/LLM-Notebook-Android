package com.llmnotebook.app.network

import com.google.gson.annotations.SerializedName

data class GenerateResponse(
    val id: String,
    val model: String,
    val created: Long,
    val choices: List<Choice>,
    val usage: Usage?
) {
    data class Choice(
        val index: Int,
        val delta: Delta?,
        @SerializedName("finish_reason")
        val finishReason: String?
    )

    data class Delta(
        val role: String?,
        val content: String?
    )

    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("completion_tokens")
        val completionTokens: Int,
        @SerializedName("total_tokens")
        val totalTokens: Int
    )
}
