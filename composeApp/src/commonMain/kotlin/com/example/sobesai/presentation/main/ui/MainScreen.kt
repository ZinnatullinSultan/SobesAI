package com.example.sobesai.presentation.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.presentation.main.MainScreenViewModel
import com.example.sobesai.presentation.main.SpecializationsUiState
import com.example.sobesai.presentation.main.ui.widgets.SearchTopBar
import com.example.sobesai.presentation.main.ui.widgets.SpecializationList
import com.example.sobesai.presentation.main.ui.widgets.state.EmptyState
import com.example.sobesai.presentation.main.ui.widgets.state.ErrorState
import com.example.sobesai.presentation.main.ui.widgets.state.LoadingState
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.widgets.PullRefreshWrapper
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSpecializationClick: (Long) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: MainScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    MainScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        isRefreshing = isRefreshing,
        onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
        onRefresh = { viewModel.load(isRefresh = true) },
        onRetry = { viewModel.load(isRefresh = false) },
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
                onProfileClick = onProfileClick,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )
            )
        },
    ) { innerPadding ->
        PullRefreshWrapper(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimens.Padding.Normal)
            ) {
                when (uiState) {
                    is SpecializationsUiState.Loading -> {
                        LoadingState()
                    }

                    is SpecializationsUiState.Error -> {
                        ErrorState(
                            message = stringResource(uiState.message),
                            onRetry = onRetry
                        )
                    }

                    is SpecializationsUiState.Empty -> {
                        EmptyState()
                    }

                    is SpecializationsUiState.Success -> {
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
