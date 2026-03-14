package com.example.sobesai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()
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
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "com.example.sobesai" && data.host == "login-callback") {
            val fragment = data.fragment ?: ""
            if (fragment.isNotEmpty()) {
                val params = fragment.split("&").associate {
                    val (key, value) = it.split("=")
                    key to value
                }

                val accessToken = params["access_token"]
                if (accessToken != null) {
                    lifecycleScope.launch {
                        settingsRepository.saveToken(accessToken)
                        Napier.d(tag = "AUTH") { "Токен успешно сохранен в DataStore!" }
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}