package com.example.sobesai.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAuthManager(): AuthManager {
    return remember { IosAuthManager() }
}

class IosAuthManager : AuthManager {
    override fun startOAuthFlow(provider: String) {
        // Пока просто принт для заглушки
        println("OAuth started on iOS")
    }
}