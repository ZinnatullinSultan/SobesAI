package com.example.sobesai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

private lateinit var dataStoreContext: Context

fun initDataStoreContext(context: Context) {
    dataStoreContext = context
}

actual fun provideDataStore(): DataStore<Preferences> {
    return createDataStore {
        dataStoreContext.filesDir.resolve(SETTINGS_PREFERENCES).absolutePath
    }
}