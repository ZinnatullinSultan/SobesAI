package com.example.sobesai.presentation.specialization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.model.SubscriptionStatus
import com.example.sobesai.domain.usecase.specialization.GetSpecializationUseCase
import com.example.sobesai.domain.usecase.subscription.CheckInterviewLimitUseCase
import com.example.sobesai.domain.usecase.subscription.GetSubscriptionStatusUseCase
import com.example.sobesai.domain.usecase.subscription.IncrementInterviewCountUseCase
import com.example.sobesai.presentation.specialization.components.DifficultyLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val FLOW_STOP_TIMEOUT_MS = 5000L

data class SpecializationUiState(
    val specialization: Specialization? = null,
    val selectedLevel: DifficultyLevel = DifficultyLevel.Middle,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showLimitDialog: Boolean = false
)

class SpecializationViewModel(
    private val getSpecializationUseCase: GetSpecializationUseCase,
    private val checkInterviewLimitUseCase: CheckInterviewLimitUseCase,
    private val incrementInterviewCountUseCase: IncrementInterviewCountUseCase,
    getSubscriptionStatusUseCase: GetSubscriptionStatusUseCase,
    private val id: Long
) : ViewModel() {
    private val _state = MutableStateFlow(SpecializationUiState())
    val state = _state.asStateFlow()
    
    val subscriptionStatus: StateFlow<SubscriptionStatus?> = getSubscriptionStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
            initialValue = null
        )

    init {
        loadSpecialization()
    }

    fun retry() {
        loadSpecialization()
    }

    private fun loadSpecialization() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getSpecializationUseCase(id)
                .onSuccess { item ->
                    _state.update { it.copy(specialization = item, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun onLevelSelected(level: DifficultyLevel) {
        _state.update { it.copy(selectedLevel = level) }
    }
    
    /**
     * Check if user can start interview.
     * Returns true if can start, false if limit reached.
     */
    suspend fun checkAndPrepareInterview(): Boolean {
        val canStart = checkInterviewLimitUseCase()
        if (canStart) {
            incrementInterviewCountUseCase()
            return true
        } else {
            _state.update { it.copy(showLimitDialog = true) }
            return false
        }
    }
    
    fun dismissLimitDialog() {
        _state.update { it.copy(showLimitDialog = false) }
    }
}
