package com.example.sobesai.core.liveapi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
actual fun rememberGeminiLiveController(): GeminiLiveController {
    val controller = remember {
        GeminiLiveControllerImpl(
            audioManager = createPlatformAudioManager()
        )
    }

    DisposableEffect(controller) {
        onDispose {
            (controller as? GeminiLiveControllerImpl)?.release()
        }
    }

    return controller
}
