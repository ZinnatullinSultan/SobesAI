package com.example.sobesai.data.local

expect class SecureTokenStorage {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveAccessToken(token: String)
    fun saveRefreshToken(token: String)
    fun clearTokens()
}

expect fun provideSecureTokenStorage(context: DataStoreContext): SecureTokenStorage
