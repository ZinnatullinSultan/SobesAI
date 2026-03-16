package com.example.sobesai.data.local

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings

private const val KEYCHAIN_SERVICE = "com.example.sobesai.tokens"
private const val KEY_ACCESS_TOKEN = "access_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"

@OptIn(ExperimentalSettingsImplementation::class)
actual class SecureTokenStorage {
private val keychain = KeychainSettings(KEYCHAIN_SERVICE)

    actual fun getAccessToken(): String? = keychain.getStringOrNull(KEY_ACCESS_TOKEN)

    actual fun getRefreshToken(): String? = keychain.getStringOrNull(KEY_REFRESH_TOKEN)

    actual fun saveAccessToken(token: String) {
        keychain.putString(KEY_ACCESS_TOKEN, token)
    }

    actual fun saveRefreshToken(token: String) {
        keychain.putString(KEY_REFRESH_TOKEN, token)
    }

    actual fun clearTokens() {
        keychain.remove(KEY_ACCESS_TOKEN)
        keychain.remove(KEY_REFRESH_TOKEN)
    }
}

actual fun provideSecureTokenStorage(context: DataStoreContext): SecureTokenStorage {
    return SecureTokenStorage()
}
