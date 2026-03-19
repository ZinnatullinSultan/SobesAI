package com.example.sobesai.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.usecase.specialization.GetSpecializationsUseCase
import com.example.sobesai.domain.usecase.specialization.SortSpecializationsUseCase
import com.example.sobesai.domain.usecase.specialization.TogglePinUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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

class MainScreenViewModel(
    private val getSpecializationsUseCase: GetSpecializationsUseCase,
    private val togglePinUseCase: TogglePinUseCase,
    private val sortSpecializationsUseCase: SortSpecializationsUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
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

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearch() {
        _searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                refreshTrigger.onStart { emit(Unit) }.flatMapLatest {
                    currentPage = 0
                    isLastPage = false
                    allLoadedItems.clear()

                    flow {
                        emit(SpecializationsUiState.Loading)
                        val result = getSpecializationsUseCase(query, 0, pageSize)
                        emit(processFirstPage(result))
                    }
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
        val item = allLoadedItems.find { it.id == id } ?: return

        viewModelScope.launch {
            togglePinUseCase(item, pinOrderCounter)
                .onSuccess { updatedItem ->
                    val index = allLoadedItems.indexOfFirst { it.id == id }
                    if (index != -1) {
                        allLoadedItems[index] = updatedItem
                        // Если статус изменился на "закреплено", обновляем локальный счетчик
                        if (updatedItem.isPinned) {
                            pinOrderCounter = maxOf(pinOrderCounter, updatedItem.pinOrder ?: 0)
                        }
                        updateUiWithSorting()
                    }
                }
                .onFailure { error ->
                    Napier.e(tag = "PIN_DEBUG", throwable = error) { "Ошибка Pin" }
                }
        }
    }


    fun loadNextPage() {
        if (isLastPage || _uiState.value is SpecializationsUiState.Loading || isNextPageLoading()) return

        viewModelScope.launch {
            _uiState.value = (uiState.value as? SpecializationsUiState.Success)
                ?.copy(isNextPageLoading = true) ?: uiState.value

            currentPage++
            getSpecializationsUseCase(_searchQuery.value, currentPage, pageSize)
                .onSuccess { newItems ->
                    handleNewItems(newItems)
                    updateUiWithSorting()
                }
                .onFailure { updateUiWithSorting() }
        }
    }

    private fun isNextPageLoading() =
        (uiState.value as? SpecializationsUiState.Success)?.isNextPageLoading == true

    private fun updateUiWithSorting() {
        updateCounter++
        _uiState.value = SpecializationsUiState.Success(
            items = sortSpecializationsUseCase(allLoadedItems),
            isNextPageLoading = false,
            updateCount = updateCounter
        )
    }

    private fun processFirstPage(result: Result<List<Specialization>>): SpecializationsUiState {
        return result.fold(
            onSuccess = { items ->
                if (items.isEmpty()) SpecializationsUiState.Empty
                else {
                    handleNewItems(items)
                    SpecializationsUiState.Success(sortSpecializationsUseCase(allLoadedItems))
                }
            },
            onFailure = {
                SpecializationsUiState.Error(Res.string.main_query_error)
            }
        )
    }

    private fun handleNewItems(newItems: List<Specialization>) {
        if (newItems.isEmpty()) {
            isLastPage = true
        } else {
            updatePinOrderCounter(newItems)
            allLoadedItems.addAll(newItems)
            if (newItems.size < pageSize) isLastPage = true
        }
    }

    private fun updatePinOrderCounter(items: List<Specialization>) {
        val maxOrder = items.mapNotNull { it.pinOrder }.maxOrNull() ?: 0
        pinOrderCounter = maxOf(pinOrderCounter, maxOrder)
    }

    fun retry() {
        refreshTrigger.tryEmit(Unit)
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            refreshTrigger.tryEmit(Unit)
            _isRefreshing.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
