package com.example.sobesai.data.repository

class LoginRepository {
    fun login(username: String, password: String): Result<String> {
        return if (username == "admin" && password == "123456") {
            Result.success("fake_jwt_token_from_server")
        } else {
            Result.failure(Exception("Неверный логин или пароль"))
        }
    }
}