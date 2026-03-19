package com.example.sobesai.presentation.specialization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.usecase.specialization.GetSpecializationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DifficultyLevel { Junior, Middle, Senior }

data class SpecializationUiState(
    val specialization: Specialization? = null,
    val selectedLevel: DifficultyLevel = DifficultyLevel.Middle,
    val isLoading: Boolean = true,
    val error: String? = null
)

class SpecializationViewModel(
    private val getSpecializationUseCase: GetSpecializationUseCase,
    private val id: Long
) : ViewModel() {
    private val _state = MutableStateFlow(SpecializationUiState())
    val state = _state.asStateFlow()

    init {
        loadSpecialization()
    }

    private fun loadSpecialization() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
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
}