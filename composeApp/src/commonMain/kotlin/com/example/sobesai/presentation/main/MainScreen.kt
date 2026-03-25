package com.example.sobesai.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.presentation.components.PullRefreshWrapper
import com.example.sobesai.presentation.main.components.EmptyState
import com.example.sobesai.presentation.main.components.ErrorState
import com.example.sobesai.presentation.main.components.LoadingState
import com.example.sobesai.presentation.main.components.SearchTopBar
import com.example.sobesai.presentation.main.components.SpecializationList
import com.example.sobesai.presentation.theme.AppDimens
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
    onSpecializationClick: (Long) -> Unit,
    onProfileClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    MainScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        isRefreshing = isRefreshing,
        onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
        onRefresh = { viewModel.refresh() },
        onRetry = { viewModel.retry() },
        onItemClick = onSpecializationClick,
        onPinClick = { viewModel.onPinClicked(it) },
        onLoadNextPage = { viewModel.loadNextPage() },
        onProfileClick = onProfileClick
    )

}

@Composable
private fun MainScreenContent(
    uiState: SpecializationsUiState,
    searchQuery: String,
    isRefreshing: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onItemClick: (Long) -> Unit,
    onPinClick: (Long) -> Unit,
    onLoadNextPage: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(AppDimens.Padding.Normal)
        ) {
            when (uiState) {
                is SpecializationsUiState.Loading -> {
                    LoadingState()
                }

                is SpecializationsUiState.Error -> {
                    ErrorState(
                        message = uiState.message,
                        onRetry = onRetry
                    )
                }

                is SpecializationsUiState.Empty -> {
                    EmptyState()
                }

                is SpecializationsUiState.Success -> {
                    PullRefreshWrapper(
                        isRefreshing = isRefreshing,
                        onRefresh = onRefresh
                    ) {
                        SpecializationList(
                            items = uiState.items,
                            isNextPageLoading = uiState.isNextPageLoading,
                            onItemClick = onItemClick,
                            onPinClick = onPinClick,
                            onLoadNextPage = onLoadNextPage
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMainScreen() {
    MainScreenContent(
        uiState = SpecializationsUiState.Success(
            items = listOf(
                Specialization(
                    id = 1,
                    title = "Android Developer",
                    description = "Разработка мобильных приложений на Kotlin и Jetpack Compose.",
                    isPinned = true,
                    pinOrder = 1
                )
            )
        ),
        searchQuery = "",
        isRefreshing = false,
        onSearchQueryChange = {},
        onRefresh = {},
        onRetry = {},
        onItemClick = {},
        onPinClick = {},
        onLoadNextPage = {},
        onProfileClick = {},
    )
}
