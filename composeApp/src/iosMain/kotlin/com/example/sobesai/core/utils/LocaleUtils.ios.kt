package com.example.sobesai.core.utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getCurrentLanguage(): String {
    val language = NSLocale.currentLocale.languageCode
    return if (language.startsWith("en")) "en" else "ru"
}
