package com.example.sobesai.data.repository

import com.example.sobesai.data.local.dao.InterviewDao
import com.example.sobesai.data.mapper.toDomain
import com.example.sobesai.data.mapper.toEntity
import com.example.sobesai.data.mapper.toGeminiContent
import com.example.sobesai.data.mapper.toGeminiContentList
import com.example.sobesai.data.remote.dto.GeminiPart
import com.example.sobesai.data.remote.dto.GeminiRequest
import com.example.sobesai.data.remote.dto.GeminiResponse
import com.example.sobesai.data.remote.dto.GeminiSystemInstruction
import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.model.EmptyAiResponseException
import com.example.sobesai.domain.model.MessageRole
import com.example.sobesai.domain.provider.InterviewPromptProvider
import com.example.sobesai.domain.repository.InterviewRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class InterviewRepositoryImpl(
    private val client: HttpClient,
    private val interviewDao: InterviewDao,
    private val promptProvider: InterviewPromptProvider
) : InterviewRepository {

    private val modelId = "gemini-3.1-flash-lite-preview"

    override suspend fun getInterviewHistory(specId: Long, difficulty: String): List<ChatMessage> {
        return interviewDao.getMessages(specId, difficulty).map { it.toDomain() }
    }

    override suspend fun clearInterviewHistory(specId: Long, difficulty: String) {
        interviewDao.clearHistory(specId, difficulty)
    }

    override suspend fun sendMessage(
        specId: Long,
        specializationTitle: String,
        difficulty: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Result<ChatMessage> {
        return try {
            val userMsg = ChatMessage(MessageRole.USER, userMessage)
            interviewDao.insertMessage(userMsg.toEntity(specId, difficulty))

            performAiRequest(specId, specializationTitle, difficulty, history, userMessage)
        } catch (e: Exception) {
            Napier.e(
                tag = "INTERVIEW_REPO",
                throwable = e
            ) { "Ошибка при сохранении сообщения пользователя" }
            Result.failure(e)
        }
    }

    private suspend fun performAiRequest(
        specId: Long,
        specializationTitle: String,
        difficulty: String,
        history: List<ChatMessage>,
        lastText: String
    ): Result<ChatMessage> {
        return try {
            val contents = history.toGeminiContentList() +
                    ChatMessage(text = lastText, role = MessageRole.USER).toGeminiContent()

            val systemInstruction = GeminiSystemInstruction(
                parts = listOf(
                    GeminiPart(
                        text = promptProvider.getSystemPrompt(
                            specializationTitle,
                            difficulty
                        )
                    )
                )
            )

            val request = GeminiRequest(
                contents = contents,
                systemInstruction = systemInstruction
            )

            val endpoint = "v1beta/models/$modelId:generateContent"

            val httpResponse = client.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val rawBody = httpResponse.bodyAsText()
            Napier.d(tag = "INTERVIEW_REPO") { "RAW RESPONSE: $rawBody" }

            if (httpResponse.status.value != 200) {
                return Result.failure(Exception("Google API Error: ${httpResponse.status.value}"))
            }

            val response: GeminiResponse = httpResponse.body()

            val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw EmptyAiResponseException("ИИ прислал пустой ответ или сработал фильтр")

            val aiMsg = ChatMessage(MessageRole.MODEL, aiText)
            interviewDao.insertMessage(aiMsg.toEntity(specId, difficulty))

            Result.success(aiMsg)
        } catch (e: Exception) {
            Napier.e(tag = "INTERVIEW_REPO", throwable = e) { "Сбой при запросе к ИИ" }
            Result.failure(e)
        }
    }

    override suspend fun startInterview(
        specId: Long,
        specializationTitle: String,
        difficulty: String
    ): Result<List<ChatMessage>> {
        return try {
            val history = getInterviewHistory(specId, difficulty)
            if (history.isNotEmpty()) {
                return Result.success(history)
            }

            val initialPrompt = promptProvider.getInitialUserPrompt(specializationTitle, difficulty)
            performAiRequest(
                specId,
                specializationTitle,
                difficulty,
                emptyList(),
                initialPrompt
            ).map { listOf(it) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
