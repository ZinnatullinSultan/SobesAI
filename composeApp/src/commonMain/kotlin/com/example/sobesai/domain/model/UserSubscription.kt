package com.example.sobesai.domain.model

/**
 * User subscription plan type
 */
enum class UserPlan {
    FREE,
    PREMIUM
}

/**
 * Subscription status information
 */
data class SubscriptionStatus(
    val plan: UserPlan,
    val interviewsUsed: Int,
    val interviewsLimit: Int,
    val isPremium: Boolean = plan == UserPlan.PREMIUM
) {
    val interviewsRemaining: Int
        get() = if (isPremium) Int.MAX_VALUE else maxOf(0, interviewsLimit - interviewsUsed)
    
    val canStartInterview: Boolean
        get() = isPremium || interviewsUsed < interviewsLimit
    
    companion object {
        const val FREE_INTERVIEWS_LIMIT = 3
    }
}
