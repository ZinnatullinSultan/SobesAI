package com.example.sobesai.domain.repository

interface LoginRepository {
    suspend fun login(username: String, password: String): Result<String>
}

