package com.example.sobesai.core

import androidx.compose.runtime.Composable

@Composable
expect fun rememberAuthManager(): AuthManager

interface AuthManager {
    fun startOAuthFlow(provider: String)
}