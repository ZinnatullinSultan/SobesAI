package com.example.sobesai.core

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberAuthManager(): AuthManager {
    val context = LocalContext.current
    return remember(context) {
        AndroidAuthManager(context)
    }
}

class AndroidAuthManager(private val context: Context) : AuthManager {
    private val supabaseUrl = "https://rrhykitzjowtpbikkjkz.supabase.co"

    override fun startOAuthFlow(provider: String) {
        val authUrl = "${supabaseUrl}/auth/v1/authorize?" +
                "provider=${provider}&" +
                "redirect_to=com.example.sobesai://login-callback"

        val intent = CustomTabsIntent.Builder().build()
        intent.intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.launchUrl(context, Uri.parse(authUrl))
    }
}