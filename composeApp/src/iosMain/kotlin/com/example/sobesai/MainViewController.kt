package com.example.sobesai

import androidx.compose.ui.window.ComposeUIViewController
import com.example.sobesai.di.appModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule)
    }
    App()
}
