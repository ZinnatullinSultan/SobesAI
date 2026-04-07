package com.example.sobesai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.sobesai.core.AndroidAuthManager
import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private const val LOG_TAG_AUTH = "AUTH"

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()
    private val authManager: AndroidAuthManager by lazy {
        AndroidAuthManager(this, settingsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Napier.base(DebugAntilog())

        handleAuthIntent(intent)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        setContent {
            val isDark = isSystemInDarkTheme()
            insetsController.isAppearanceLightStatusBars = !isDark
            insetsController.isAppearanceLightNavigationBars = !isDark
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthIntent(intent)
    }

    private fun handleAuthIntent(intent: Intent?) {
        lifecycleScope.launch {
            val handled = authManager.handleOAuthCallback(intent?.dataString)
            if (handled) {
                Napier.d(tag = LOG_TAG_AUTH) { "OAuth callback обработан в AuthManager" }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
