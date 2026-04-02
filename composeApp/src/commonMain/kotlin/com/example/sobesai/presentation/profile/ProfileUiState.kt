package com.example.sobesai.presentation.profile

import com.example.sobesai.domain.model.SubscriptionStatus

data class ProfileUiState(
    val displayName: String? = null,
    val subscriptionStatus: SubscriptionStatus? = null,
    val isUpgrading: Boolean = false
)
