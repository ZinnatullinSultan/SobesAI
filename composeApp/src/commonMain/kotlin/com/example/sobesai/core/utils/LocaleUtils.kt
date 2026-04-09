package com.example.sobesai.core.utils

expect fun getCurrentLanguage(): String
interface LanguageProvider {
    fun getLanguageTag(): String
}

class SystemLanguageProvider : LanguageProvider {
    override fun getLanguageTag(): String = getCurrentLanguage()
}
