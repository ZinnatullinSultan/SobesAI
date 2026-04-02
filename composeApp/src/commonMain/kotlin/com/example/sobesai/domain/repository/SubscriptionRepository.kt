package com.example.sobesai.domain.repository

import com.example.sobesai.domain.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    /**
     * Flow of current subscription status
     */
    val subscriptionStatus: Flow<SubscriptionStatus>
    
    /**
     * Check if user can start a new interview
     */
    suspend fun canStartInterview(): Boolean
    
    /**
     * Increment interview count after starting a new interview
     */
    suspend fun incrementInterviewCount()
    
    /**
     * Upgrade to premium (simulated purchase)
     */
    suspend fun upgradeToPremium()
    
    /**
     * Reset interview count (for testing or monthly reset)
     */
    suspend fun resetInterviewCount()
    
    /**
     * Clear all subscription data (on logout)
     */
    suspend fun clearSubscriptionData()
}
