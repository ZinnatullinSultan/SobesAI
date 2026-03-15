package com.example.sobesai.core

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.sobesai.domain.repository.SettingsRepository
import org.koin.compose.koinInject

@Composable
actual fun rememberAuthManager(): AuthManager {
    val context = LocalContext.current
    val settingsRepository: SettingsRepository = koinInject()
    return remember(context, settingsRepository) {
        AndroidAuthManager(context, settingsRepository)
    }
}

class AndroidAuthManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : AuthManager {
    private val supabaseUrl = "https://rrhykitzjowtpbikkjkz.supabase.co"

    override fun startOAuthFlow(provider: String) {
        val authUrl = "${supabaseUrl}/auth/v1/authorize?" +
                "provider=${provider}&" +
                "redirect_to=com.example.sobesai://login-callback"

        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, authUrl.toUri())
    }

    override suspend fun handleOAuthCallback(callbackUrl: String?): Boolean {
        val uri = callbackUrl?.toUri() ?: return false
        if (uri.scheme != "com.example.sobesai" || uri.host != "login-callback") return false

        val fragment = uri.fragment ?: return false
        val params = fragment.split("&").mapNotNull {
            val pair = it.split("=", limit = 2)
            if (pair.size == 2) pair[0] to pair[1] else null
        }.toMap()

        val accessToken = params["access_token"] ?: return false
        settingsRepository.saveToken(accessToken)
        extractDisplayNameFromJwt(accessToken)?.let { displayName ->
            settingsRepository.saveDisplayName(displayName)
        }
        return true
    }
}