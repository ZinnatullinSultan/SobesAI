@file:Suppress("MatchingDeclarationName")

package com.example.sobesai.data.local.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual class DataStoreContext(val context: Context)

actual fun provideDataStore(context: DataStoreContext): DataStore<Preferences> {
    return createDataStore {
        context.context.filesDir.resolve(SETTINGS_PREFERENCES).absolutePath
    }
}
