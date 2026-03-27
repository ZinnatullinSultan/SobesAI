package com.example.sobesai.domain.model

import io.ktor.http.HttpStatusCode

class SupabaseApiException(val body: String, val status: HttpStatusCode) : Exception(body)
