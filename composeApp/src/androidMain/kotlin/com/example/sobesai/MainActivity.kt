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
import com.example.sobesai.data.local.TokenStorage
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {
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
                    TokenStorage.saveToken(accessToken)

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