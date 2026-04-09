package com.example.sobesai.core.utils

import java.util.Locale

actual fun getCurrentLanguage(): String {
    val language = Locale.getDefault().language
    return if (language.startsWith("en")) "en" else "ru"
}
