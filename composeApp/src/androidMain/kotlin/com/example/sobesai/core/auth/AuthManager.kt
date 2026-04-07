package com.example.sobesai.core.auth

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.sobesai.core.utils.AUTH_HOST
import com.example.sobesai.core.utils.AUTH_SCHEME
import com.example.sobesai.core.utils.KEY_ACCESS_TOKEN
import com.example.sobesai.core.utils.KEY_REFRESH_TOKEN
import com.example.sobesai.core.utils.REDIRECT_URL
import com.example.sobesai.core.utils.SUPABASE_URL
import com.example.sobesai.core.utils.extractDisplayNameFromJwt
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
    override fun startOAuthFlow(provider: String) {
        val authUrl = "${SUPABASE_URL}/auth/v1/authorize?" +
                "provider=${provider}&" +
                "redirect_to=${REDIRECT_URL}"
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, authUrl.toUri())
    }

    override suspend fun handleOAuthCallback(callbackUrl: String?): Boolean {
        val result = runCatching {
            val uri = callbackUrl?.toUri()
            requireNotNull(uri) { "URI is null" }
            require(uri.scheme == AUTH_SCHEME && uri.host == AUTH_HOST) { "Invalid scheme or host" }

            val fragment = uri.fragment
            requireNotNull(fragment) { "Fragment is null" }

            val params = fragment.split("&").mapNotNull {
                val pair = it.split("=", limit = 2)
                if (pair.size == 2) pair[0] to pair[1] else null
            }.toMap()

            val accessToken = params[KEY_ACCESS_TOKEN]
            requireNotNull(accessToken) { "Access token is null" }

            val refreshToken = params[KEY_REFRESH_TOKEN]
            if (!refreshToken.isNullOrBlank()) {
                settingsRepository.saveTokens(accessToken, refreshToken)
            } else {
                settingsRepository.saveToken(accessToken)
            }

            extractDisplayNameFromJwt(accessToken)?.let { displayName ->
                settingsRepository.saveDisplayName(displayName)
            }
            true
        }
        return result.getOrDefault(false)
    }
}
