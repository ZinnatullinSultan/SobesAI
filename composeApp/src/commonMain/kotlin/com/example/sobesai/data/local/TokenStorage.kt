package com.example.sobesai.data.local

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


object TokenStorage {

    private val settings: Settings = Settings()
    private const val TOKEN_KEY = "auth_token"

    private val savedToken: String? = settings.getStringOrNull(TOKEN_KEY)

    private val _token = MutableStateFlow<String?>(savedToken)
    val token: StateFlow<String?> = _token.asStateFlow()

    fun saveToken(newToken: String) {
        _token.value = newToken
        settings.putString(TOKEN_KEY, newToken)
    }

    fun getToken(): String? = _token.value

    fun clearToken() {
        _token.value = null
        settings.remove(TOKEN_KEY)
    }
}