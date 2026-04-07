package com.example.sobesai.data.local.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val PREF_IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")

class OnboardingStorage(private val dataStore: DataStore<Preferences>) {

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PREF_IS_FIRST_LAUNCH] ?: true
    }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[PREF_IS_FIRST_LAUNCH] = false
        }
    }
}
