package com.example.sobesai.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    @SerialName("contents")
    val contents: List<GeminiContent>,
    @SerialName("system_instruction")
    val systemInstruction: GeminiSystemInstruction? = null
)

@Serializable
data class GeminiContent(
    @SerialName("role")
    val role: String,
    @SerialName("parts")
    val parts: List<GeminiPart>
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_MODEL = "model"
    }
}

@Serializable
data class GeminiPart(
    @SerialName("text")
    val text: String
)

@Serializable
data class GeminiSystemInstruction(
    @SerialName("parts")
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiResponse(
    @SerialName("candidates")
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    @SerialName("content")
    val content: GeminiContent? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)
