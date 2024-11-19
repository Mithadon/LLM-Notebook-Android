package com.llmnotebook.app.network

import com.google.gson.annotations.SerializedName
import com.llmnotebook.app.util.SettingsManager

data class GenerateRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Float,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    @SerializedName("top_p")
    val topP: Float,
    @SerializedName("top_k")
    val topK: Int,
    val stream: Boolean = true,
    @SerializedName("response_format")
    val responseFormat: ResponseFormat = ResponseFormat("text")
) {
    data class Message(
        val role: String = "user",
        val content: String
    )

    data class ResponseFormat(
        val type: String
    )

    companion object {
        suspend fun create(settingsManager: SettingsManager, prompt: String): GenerateRequest {
            return GenerateRequest(
                model = settingsManager.defaultModel.value ?: error("No model selected"),
                messages = listOf(Message(content = prompt)),
                temperature = settingsManager.temperature.value,
                maxTokens = settingsManager.maxTokens.value,
                topP = settingsManager.topP.value,
                topK = settingsManager.topK.value
            )
        }
    }
}
