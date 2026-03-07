package com.example.sobesai.data.repository

class LoginRepository {
    fun login(username: String, password: String): Result<Unit> {
        return if (username == "admin" && password == "123456") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Неверный логин или пароль"))
        }
    }
}