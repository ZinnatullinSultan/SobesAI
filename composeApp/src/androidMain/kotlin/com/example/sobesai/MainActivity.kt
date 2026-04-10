package com.example.sobesai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.sobesai.core.auth.AndroidAuthManager
import com.example.sobesai.core.push.PushRegistrar
import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private const val LOG_TAG_AUTH = "AUTH"
private const val LOG_TAG_PUSH = "PUSH"

class MainActivity : ComponentActivity() {
    private val notificationsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pushRegistrar.registerPushToken()
            } else {
                Napier.d(tag = LOG_TAG_PUSH) { "Разрешение на уведомления не выдано" }
            }
        }

    private val settingsRepository: SettingsRepository by inject()
    private val pushRegistrar: PushRegistrar by inject()

    private val authManager: AndroidAuthManager by lazy {
        AndroidAuthManager(this, settingsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        handleAuthIntent(intent)
        requestNotificationsPermissionIfNeeded()

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

    private fun requestNotificationsPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            pushRegistrar.registerPushToken()
            return
        }

        val isGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            pushRegistrar.registerPushToken()
        } else {
            notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
