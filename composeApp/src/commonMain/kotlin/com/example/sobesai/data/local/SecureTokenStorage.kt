package com.example.sobesai.data.local

import com.example.sobesai.core.KEY_ACCESS_TOKEN
import com.example.sobesai.core.KEY_REFRESH_TOKEN
import com.liftric.kvault.KVault

class SecureTokenStorage(private val kvault: KVault) {
    fun getAccessToken(): String? = kvault.string(KEY_ACCESS_TOKEN)
    fun getRefreshToken(): String? = kvault.string(KEY_REFRESH_TOKEN)

    fun saveAccessToken(token: String) {
        kvault.set(KEY_ACCESS_TOKEN, token)
    }

    fun saveRefreshToken(token: String) {
        kvault.set(KEY_REFRESH_TOKEN, token)
    }

    fun clearTokens() {
        kvault.deleteObject(KEY_ACCESS_TOKEN)
        kvault.deleteObject(KEY_REFRESH_TOKEN)
    }
}
