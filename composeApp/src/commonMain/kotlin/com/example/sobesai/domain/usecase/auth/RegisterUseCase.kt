package com.example.sobesai.domain.usecase.auth

import com.example.sobesai.domain.repository.LoginRepository

class RegisterUseCase(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): Result<Unit> {
        return loginRepository.register(email, password, displayName)
    }
}
