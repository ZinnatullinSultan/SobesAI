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
        val isNextPageLoading: Boolean = false,
        val updateCount: Int = 0
    ) : SpecializationsUiState

    data class Error(val message: StringResource) : SpecializationsUiState
}

class MainViewModel(
    private val repository: SpecializationsRepository = SpecializationsRepository()
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val allLoadedItems = mutableListOf<Specialization>()

    private var currentPage = 0
    private val pageSize = 10
    private var isLastPage = false
    private var pinOrderCounter = 0
    private var updateCounter = 0

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

    fun onPinClicked(id: Long) {
        val itemIndex = allLoadedItems.indexOfFirst { it.id == id }
        if (itemIndex == -1) return

        val currentItem = allLoadedItems[itemIndex]
        val nextPinnedState = !currentItem.isPinned
        val nextPinOrder = if (nextPinnedState) ++pinOrderCounter else null

        viewModelScope.launch {
            val result = repository.updatePinStatus(id, nextPinnedState, nextPinOrder)

            result.onSuccess {
                allLoadedItems[itemIndex] = currentItem.copy(
                    isPinned = nextPinnedState,
                    pinOrder = nextPinOrder
                )
                updateUiWithSorting()
                Napier.d(tag = "PIN_DEBUG") { "Элемент ${currentItem.title} успешно обновлен"}
            }.onFailure { error ->
                Napier.e(tag = "PIN_DEBUG", throwable = error) { "Ошибка при обновлении Pin статуса" }
            }
        }
    }

    private fun updateUiWithSorting() {
        updateCounter++
        _uiState.value = SpecializationsUiState.Success(
            items = getSortedList(),
            isNextPageLoading = false,
            updateCount = updateCounter
        )
    }

    private fun getSortedList(): List<Specialization> {
        return allLoadedItems.toList().sortedWith(
            compareByDescending<Specialization> { it.isPinned }
                .thenByDescending { it.pinOrder ?: 0 }
                .thenBy { it.id }
        )
    }

    private fun processFirstPage(result: Result<List<Specialization>>): SpecializationsUiState {
        return result.fold(
            onSuccess = { items ->
                allLoadedItems.clear()
                if (items.isEmpty()) {
                    SpecializationsUiState.Empty
                } else {
                    updatePinOrderCounter(items)
                    allLoadedItems.addAll(items)
                    if (items.size < pageSize) isLastPage = true
                    SpecializationsUiState.Success(getSortedList())
                }
            },
            onFailure = {
                SpecializationsUiState.Error(Res.string.main_query_error)
            }
        )
    }

    private fun updatePinOrderCounter(items: List<Specialization>) {
        val maxOrder = items.mapNotNull { it.pinOrder }.maxOrNull() ?: 0
        if (maxOrder > pinOrderCounter) {
            pinOrderCounter = maxOrder
        }
    }

    fun loadNextPage() {
        if (isLastPage || _uiState.value is SpecializationsUiState.Loading ||
            (_uiState.value as? SpecializationsUiState.Success)?.isNextPageLoading == true
        ) return
        viewModelScope.launch {
            val currentItems = getSortedList()
            _uiState.value = SpecializationsUiState.Success(currentItems, isNextPageLoading = true)

            currentPage++
            val offset = currentPage * pageSize
            val result = repository.getSpecializations(_searchQuery.value, offset, pageSize)
            result.onSuccess { newItems ->
                if (newItems.isEmpty()) {
                    isLastPage = true
                } else {
                    updatePinOrderCounter(newItems)
                    allLoadedItems.addAll(newItems)
                    if (newItems.size < pageSize) isLastPage = true
                }
                updateUiWithSorting()
            }.onFailure {
                updateUiWithSorting()
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
}
