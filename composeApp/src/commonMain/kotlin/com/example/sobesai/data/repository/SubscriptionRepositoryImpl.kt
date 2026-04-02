package com.example.sobesai.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.sobesai.domain.model.SubscriptionStatus
import com.example.sobesai.domain.model.UserPlan
import com.example.sobesai.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val KEY_USER_PLAN = "user_plan"
private const val KEY_INTERVIEWS_USED = "interviews_used"

private val PREF_USER_PLAN = stringPreferencesKey(KEY_USER_PLAN)
private val PREF_INTERVIEWS_USED = intPreferencesKey(KEY_INTERVIEWS_USED)

private fun parsePlanOrDefault(planName: String?): UserPlan {
    return planName?.let { name ->
        UserPlan.entries.find { it.name == name }
    } ?: UserPlan.FREE
}

class SubscriptionRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SubscriptionRepository {
    
    override val subscriptionStatus: Flow<SubscriptionStatus> = dataStore.data.map { preferences ->
        val planName = preferences[PREF_USER_PLAN]
        val plan = parsePlanOrDefault(planName)
        val interviewsUsed = preferences[PREF_INTERVIEWS_USED] ?: 0
        
        SubscriptionStatus(
            plan = plan,
            interviewsUsed = interviewsUsed,
            interviewsLimit = SubscriptionStatus.FREE_INTERVIEWS_LIMIT
        )
    }
    
    override suspend fun canStartInterview(): Boolean {
        val preferences = dataStore.data.first()
        val planName = preferences[PREF_USER_PLAN]
        val plan = parsePlanOrDefault(planName)
        val interviewsUsed = preferences[PREF_INTERVIEWS_USED] ?: 0
        
        return plan == UserPlan.PREMIUM || interviewsUsed < SubscriptionStatus.FREE_INTERVIEWS_LIMIT
    }
    
    override suspend fun incrementInterviewCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[PREF_INTERVIEWS_USED] ?: 0
            val planName = preferences[PREF_USER_PLAN]
            val plan = parsePlanOrDefault(planName)
            
            // Only increment for free users
            if (plan == UserPlan.FREE) {
                preferences[PREF_INTERVIEWS_USED] = currentCount + 1
            }
        }
    }
    
    override suspend fun upgradeToPremium() {
        dataStore.edit { preferences ->
            preferences[PREF_USER_PLAN] = UserPlan.PREMIUM.name
        }
    }
    
    override suspend fun resetInterviewCount() {
        dataStore.edit { preferences ->
            preferences[PREF_INTERVIEWS_USED] = 0
        }
    }

    override suspend fun clearSubscriptionData() {
        dataStore.edit { preferences ->
            preferences.remove(PREF_USER_PLAN)
            preferences.remove(PREF_INTERVIEWS_USED)
        }
    }
}
