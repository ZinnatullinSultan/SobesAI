package com.example.sobesai.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSError
import platform.Foundation.NSURL

@Composable
actual fun rememberAuthManager(): AuthManager {
    val settingsRepository: SettingsRepository = koinInject()
    return remember { IosAuthManager(settingsRepository) }
}

class IosAuthManager(
    private val settingsRepository: SettingsRepository
) : AuthManager {
    private var authSession: ASWebAuthenticationSession? = null

    @OptIn(ExperimentalForeignApi::class)
    override fun startOAuthFlow(provider: String) {
        val supabaseUrl = "https://rrhykitzjowtpbikkjkz.supabase.co"
        val authUrl = "$supabaseUrl/auth/v1/authorize?" +
                "provider=$provider&" +
                "redirect_to=com.example.sobesai://login-callback"

        val url = NSURL(string = authUrl)
        val callbackUrlScheme = "com.example.sobesai"

        val completionHandler: (NSURL?, NSError?) -> Unit = { callbackUrl, error ->
            if (error != null) {
                Napier.e(tag = "AUTH") { "OAuth error: $error" }
            } else {
                val fragment = callbackUrl?.fragment
                if (!fragment.isNullOrEmpty()) {
                    val params = fragment.split("&").associate {
                        val parts = it.split("=")
                        parts[0] to parts.getOrElse(1) { "" }
                    }

                    val accessToken = params["access_token"]
                    if (accessToken != null) {
                        runBlocking {
                            settingsRepository.saveToken(accessToken)
                        }
                        Napier.d(tag = "AUTH") { "Токен успешно сохранен!" }
                    }
                }
            }
        }

        authSession = ASWebAuthenticationSession(
            uRL = url,
            callbackURLScheme = callbackUrlScheme,
            completionHandler = completionHandler
        )

        authSession?.start()
    }
}
