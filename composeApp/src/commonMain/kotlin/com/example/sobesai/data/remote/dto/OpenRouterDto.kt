package com.example.sobesai.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenAiMessage>
)

@Serializable
data class OpenAiMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenRouterResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: OpenAiMessage
)