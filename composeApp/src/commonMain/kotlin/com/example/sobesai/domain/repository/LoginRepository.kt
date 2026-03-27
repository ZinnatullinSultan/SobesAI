package com.example.sobesai.domain.repository

interface LoginRepository {
    suspend fun login(username: String, password: String): Result<String>
    suspend fun register(email: String, password: String, displayName: String): Result<Unit>
}
