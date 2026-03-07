package com.example.sobesai.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.data.repository.SpecializationsRepository
import com.example.sobesai.domain.model.Specialization
import io.github.aakira.napier.Napier
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.main_query_error

sealed interface SpecializationsUiState {
    object Loading : SpecializationsUiState
    object Empty : SpecializationsUiState
    data class Success(
        val items: List<Specialization>,
        val isNextPageLoading: Boolean = false
    ) : SpecializationsUiState

    data class Error(val message: StringResource) : SpecializationsUiState
}

class MainViewModel(
    private val repository: SpecializationsRepository = SpecializationsRepository()
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var allLoadedItems = mutableListOf<Specialization>()

    private var currentPage = 0
    private val pageSize = 10
    private var isLastPage = false
//    private var pinOrderCounter = 0

    private val _uiState = MutableStateFlow<SpecializationsUiState>(SpecializationsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        _searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                currentPage = 0
                isLastPage = false
                allLoadedItems.clear()

                flow {
                    emit(SpecializationsUiState.Loading)
                    val result = repository.getSpecializations(query, 0, pageSize)
                    emit(processFirstPage(result))
                }
            }
            .catch { e ->
                Napier.e(throwable = e) { "Ошибка запроса" }
                emit(SpecializationsUiState.Error(Res.string.main_query_error))
            }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    private fun processFirstPage(result: Result<List<Specialization>>): SpecializationsUiState {
        return result.fold(
            onSuccess = { items ->
                if (items.isEmpty()) {
                    SpecializationsUiState.Empty
                } else {
                    allLoadedItems.addAll(items)
                    if (items.size < pageSize) isLastPage = true
                    SpecializationsUiState.Success(allLoadedItems.toList())
                }
            },
            onFailure = {
                SpecializationsUiState.Error(Res.string.main_query_error)
            }
        )
    }

    fun loadNextPage() {
        if (isLastPage || _uiState.value is SpecializationsUiState.Loading ||
            (_uiState.value as? SpecializationsUiState.Success)?.isNextPageLoading == true
        ) return
        viewModelScope.launch {
            val currentItems = allLoadedItems.toList()
            _uiState.value = SpecializationsUiState.Success(currentItems, isNextPageLoading = true)

            currentPage++
            val offset = currentPage * pageSize
            val result = repository.getSpecializations(_searchQuery.value, offset, pageSize)
            result.onSuccess { newItems ->
                if (newItems.isEmpty()) {
                    isLastPage = true
                } else {
                    allLoadedItems.addAll(newItems)
                    if (newItems.size < pageSize) isLastPage = true
                }
                _uiState.value = SpecializationsUiState.Success(allLoadedItems.toList(), false)
            }.onFailure {
                _uiState.value = SpecializationsUiState.Success(allLoadedItems.toList(), false)
            }
        }
    }

    fun retry() {
        _uiState.value = SpecializationsUiState.Loading

        val currentQuery = _searchQuery.value
        _searchQuery.value = ""
        _searchQuery.value = currentQuery
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

//    fun onPinClicked(specializationId: Int) {
//        _specializations.update {
//            val updatedList = it.map { specialization ->
//                if (specialization.id == specializationId) {
//                    val newPinnedState = !specialization.isPinned
//                    specialization.copy(
//                        isPinned = newPinnedState,
//                        pinOrder = if (newPinnedState) ++pinOrderCounter else null
//                    )
//                } else {
//                    specialization
//                }
//            }
//            updatedList.sortedWith(
//                compareByDescending<Specialization> { it.isPinned }
//                    .thenByDescending { it.pinOrder ?: 0 }
//                    .thenBy { it.id }
//            )
//        }
//    }
}
