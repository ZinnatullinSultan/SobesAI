package com.example.sobesai.data.remote.api

import com.example.sobesai.core.utils.PATH_AUTH
import com.example.sobesai.data.remote.dto.LoginRequest
import com.example.sobesai.data.remote.dto.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): HttpResponse {
        return client.post {
            url("${PATH_AUTH}token?grant_type=password")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun register(request: RegisterRequest): HttpResponse {
        return client.post {
            url("${PATH_AUTH}signup")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
