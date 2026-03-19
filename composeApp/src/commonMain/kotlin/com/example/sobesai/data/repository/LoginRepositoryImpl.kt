package com.example.sobesai.data.repository

import com.example.sobesai.domain.repository.LoginRepository

class LoginRepositoryImpl : LoginRepository {
    private val anonKey =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJyaHlraXR6am93dHBiaWtramt6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI3ODc0NTgsImV4cCI6MjA4ODM2MzQ1OH0.RMGHGL4QKsHtmOkLOZxzj_wFhBs-B0bLeUS3rhDiKTU"

    override suspend fun login(username: String, password: String): Result<String> {
        return if (username == "admin" && password == "123") {
            Result.success(anonKey)
        } else {
            Result.failure(Exception("Неверный логин или пароль"))
        }
    }
}