package com.llmnotebook.app.data

import com.google.gson.annotations.SerializedName

data class Model(
    val id: String,
    val name: String,
    val description: String? = null,
    val pricing: Pricing? = null,
    @SerializedName("context_length")
    val contextLength: Int? = null
) {
    data class Pricing(
        val prompt: Double,
        val completion: Double
    )

    companion object {
        fun getDefaultModel() = Model(
            id = "meta-llama/llama-3.1-70b-instruct:free",
            name = "Llama 3.1 70B Instruct",
            description = "Free model for testing",
            pricing = Pricing(0.0, 0.0),
            contextLength = 4096
        )
    }
}
