package com.example.sobesai.core.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.sobesai.core.utils.AUTH_SCHEME
import com.example.sobesai.core.utils.KEY_ACCESS_TOKEN
import com.example.sobesai.core.utils.KEY_REFRESH_TOKEN
import com.example.sobesai.core.utils.REDIRECT_URL
import com.example.sobesai.core.utils.SUPABASE_URL
import com.example.sobesai.core.utils.extractDisplayNameFromJwt
import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSError
import platform.Foundation.NSURL

private const val LOG_TAG_AUTH = "AUTH"

@Composable
actual fun rememberAuthManager(): AuthManager {
    val settingsRepository: SettingsRepository = koinInject()
    return remember { IosAuthManager(settingsRepository) }
}

class IosAuthManager(
    private val settingsRepository: SettingsRepository
) : AuthManager {
    private var authSession: ASWebAuthenticationSession? = null

    override suspend fun handleOAuthCallback(callbackUrl: String?): Boolean = false

    @OptIn(ExperimentalForeignApi::class)
    override fun startOAuthFlow(provider: String) {
        val authUrl = "${SUPABASE_URL}/auth/v1/authorize?" +
                "provider=$provider&" +
                "redirect_to=${REDIRECT_URL}"

        val url = NSURL(string = authUrl)

        val completionHandler: (NSURL?, NSError?) -> Unit = { callbackUrl, error ->
            if (error != null) {
                Napier.e(tag = LOG_TAG_AUTH) { "OAuth error: $error" }
            } else {
                val fragment = callbackUrl?.fragment
                if (!fragment.isNullOrEmpty()) {
                    val params = fragment.split("&").associate {
                        val parts = it.split("=")
                        parts[0] to parts.getOrElse(1) { "" }
                    }

                    val accessToken = params[KEY_ACCESS_TOKEN]
                    val refreshToken = params[KEY_REFRESH_TOKEN]
                    if (accessToken != null) {
                        runBlocking {
                            if (!refreshToken.isNullOrBlank()) {
                                settingsRepository.saveTokens(accessToken, refreshToken)
                            } else {
                                settingsRepository.saveToken(accessToken)
                            }
                            extractDisplayNameFromJwt(accessToken)?.let { displayName ->
                                settingsRepository.saveDisplayName(displayName)
                            }
                        }
                        Napier.d(tag = LOG_TAG_AUTH) { "Токены успешно сохранены" }
                    }
                }
            }
        }
        authSession = ASWebAuthenticationSession(
            uRL = url,
            callbackURLScheme = AUTH_SCHEME,
            completionHandler = completionHandler
        )
        authSession?.start()
    }
}
