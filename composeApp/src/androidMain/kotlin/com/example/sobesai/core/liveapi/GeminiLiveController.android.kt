package com.example.sobesai.core.liveapi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberGeminiLiveController(): GeminiLiveController {
    val context = LocalContext.current
    
    // Initialize audio manager with context
    initAudioManager(context)
    
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
