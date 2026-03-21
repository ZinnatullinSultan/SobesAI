package com.example.sobesai.data.repository

import com.example.sobesai.data.local.dao.InterviewDao
import com.example.sobesai.data.mapper.toDomain
import com.example.sobesai.data.mapper.toEntity
import com.example.sobesai.data.remote.dto.OpenAiMessage
import com.example.sobesai.data.remote.dto.OpenRouterRequest
import com.example.sobesai.data.remote.dto.OpenRouterResponse
import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.model.EmptyAiResponseException
import com.example.sobesai.domain.model.MessageRole
import com.example.sobesai.domain.repository.InterviewRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

class InterviewRepositoryImpl(
    private val client: HttpClient,
    private val interviewDao: InterviewDao
) : InterviewRepository {

    private val modelId = "arcee-ai/trinity-large-preview:free"

    override suspend fun getInterviewHistory(specId: Long, difficulty: String): List<ChatMessage> {
        return interviewDao.getMessages(specId, difficulty).map { it.toDomain() }
    }

    override suspend fun clearInterviewHistory(specId: Long, difficulty: String) {
        interviewDao.clearHistory(specId, difficulty)
    }

    override suspend fun sendMessage(
        specId: Long,
        difficulty: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Result<ChatMessage> {
        return try {
            val userMsg = ChatMessage(MessageRole.USER, userMessage)
            interviewDao.insertMessage(userMsg.toEntity(specId, difficulty))

            performAiRequest(specId, difficulty, history, userMessage)
        } catch (e: Exception) {
            Napier.e(tag = "INTERVIEW_REPO", throwable = e) { "Ошибка при отправке сообщения" }
            Result.failure(e)
        }
    }

    private suspend fun performAiRequest(
        specId: Long,
        difficulty: String,
        history: List<ChatMessage>,
        lastText: String
    ): Result<ChatMessage> {
        return try {
            val messages = history.map {
                OpenAiMessage(
                    role = if (it.role == MessageRole.USER) "user" else "assistant",
                    content = it.text
                )
            } + OpenAiMessage(role = "user", content = lastText)

            val fullMessages =
                listOf(OpenAiMessage(role = "system", content = getSystemPrompt())) + messages

            val request = OpenRouterRequest(model = modelId, messages = fullMessages)

            val httpResponse = client.post("") {
                setBody(request)
            }

            if (httpResponse.status.value != 200) {
                val errorText = httpResponse.bodyAsText()
                Napier.e(tag = "INTERVIEW_REPO") { "Ошибка OpenRouter ($modelId): $errorText" }
                return Result.failure(Exception("Ошибка ИИ: ${httpResponse.status.value}"))
            }

            val response: OpenRouterResponse = httpResponse.body()
            val aiText = response.choices.firstOrNull()?.message?.content
                ?: throw EmptyAiResponseException("ИИ прислал пустой ответ")

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

            val initialPrompt =
                "Начни интервью на позицию $specializationTitle ($difficulty). Представься и задай первый вопрос."
            performAiRequest(specId, difficulty, emptyList(), initialPrompt).map { listOf(it) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getSystemPrompt() = """
        Ты — опытный IT-интервьюер SobesAI. Твоя цель — провести техническое собеседование.
        
        СТРОГИЕ ПРАВИЛА ТЕКСТА:
        1. НЕ ИСПОЛЬЗУЙ Markdown (звездочки *, решетки #). Пиши только чистый, голый текст.
        2. НИКОГДА не используй квадратные скобки типа [Имя] или [Компания].
        3. Представляйся просто как "интервьюер SobesAI".
        
        ПРАВИЛА ИНТЕРВЬЮ:
        1. Задавай ровно один вопрос за раз.
        2. Кратко оценивай ответ пользователя и давай фидбек перед следующим вопросом.
    """.trimIndent()
}
