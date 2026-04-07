package com.example.sobesai.data.local.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val SETTINGS_PREFERENCES = "settings.preferences_pb"
fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

expect class DataStoreContext

expect fun provideDataStore(context: DataStoreContext): DataStore<Preferences>
