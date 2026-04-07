package com.example.sobesai.core.auth

import androidx.compose.runtime.Composable

@Composable
expect fun rememberAuthManager(): AuthManager
interface AuthManager {
    fun startOAuthFlow(provider: String)
    suspend fun handleOAuthCallback(callbackUrl: String?): Boolean
}
