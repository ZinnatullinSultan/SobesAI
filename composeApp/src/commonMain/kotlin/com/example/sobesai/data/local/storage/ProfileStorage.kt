package com.example.sobesai.data.local.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val PREF_DISPLAY_NAME = stringPreferencesKey("display_name")

class ProfileStorage(private val dataStore: DataStore<Preferences>) {

    val displayName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PREF_DISPLAY_NAME]
    }

    suspend fun saveDisplayName(name: String) {
        dataStore.edit { preferences ->
            preferences[PREF_DISPLAY_NAME] = name
        }
    }

    suspend fun clearDisplayName() {
        dataStore.edit { preferences ->
            preferences.remove(PREF_DISPLAY_NAME)
        }
    }
}
