package com.example.sobesai.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.model.SubscriptionStatus
import com.example.sobesai.domain.usecase.auth.GetProfileUseCase
import com.example.sobesai.domain.usecase.auth.LogoutUseCase
import com.example.sobesai.domain.usecase.subscription.GetSubscriptionStatusUseCase
import com.example.sobesai.domain.usecase.subscription.PurchasePremiumUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val FLOW_STOP_TIMEOUT_MS = 5000L

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val purchasePremiumUseCase: PurchasePremiumUseCase,
    getProfileUseCase: GetProfileUseCase,
    getSubscriptionStatusUseCase: GetSubscriptionStatusUseCase
) : ViewModel() {
    
    private val _isUpgrading = MutableStateFlow(false)
    
    val uiState: StateFlow<ProfileUiState> = combine(
        getProfileUseCase(),
        getSubscriptionStatusUseCase(),
        _isUpgrading
    ) { displayName, subscriptionStatus, isUpgrading ->
        ProfileUiState(
            displayName = displayName,
            subscriptionStatus = subscriptionStatus,
            isUpgrading = isUpgrading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
        initialValue = ProfileUiState()
    )

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
    
    fun purchasePremium() {
        viewModelScope.launch {
            _isUpgrading.value = true
            purchasePremiumUseCase()
                .onSuccess {
                    // Subscription status will be updated automatically via Flow
                }
                .onFailure {
                    // Handle error (could add error state)
                }
            _isUpgrading.value = false
        }
    }
}
