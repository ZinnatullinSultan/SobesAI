package com.example.sobesai.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.presentation.components.ProfileIcon
import com.example.sobesai.presentation.components.PullRefreshWrapper
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.theme.PinIconActive
import com.example.sobesai.presentation.theme.PinIconDefault
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.empty_list
import sobesai.composeapp.generated.resources.main_empty_state_text
import sobesai.composeapp.generated.resources.main_pin_icon_description
import sobesai.composeapp.generated.resources.main_refresh_button
import sobesai.composeapp.generated.resources.main_search_placeholder

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

    Scaffold(
        topBar = {
            SearchTopBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
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
            when (val state = uiState) {
                is SpecializationsUiState.Loading -> {
                    LoadingState()
                }

                is SpecializationsUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.retry() }
                    )
                }

                is SpecializationsUiState.Empty -> {
                    EmptyState()
                }

                is SpecializationsUiState.Success -> {
                    PullRefreshWrapper(
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refresh() }
                    ) {
                        SpecializationList(
                            items = state.items,
                            isNextPageLoading = state.isNextPageLoading,
                            onItemClick = onSpecializationClick,
                            onPinClick = { id ->
                                viewModel.onPinClicked(id)
                            },
                            onLoadNextPage = { viewModel.loadNextPage() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecializationList(
    items: List<Specialization>,
    isNextPageLoading: Boolean,
    onPinClick: (Long) -> Unit,
    onLoadNextPage: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    val shouldLoadNextPage = remember {
        derivedStateOf {
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 2 && totalItemsCount > 0
        }
    }
    LaunchedEffect(shouldLoadNextPage.value) {
        if (shouldLoadNextPage.value) {
            onLoadNextPage()
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "scroll_anchor") {
            Spacer(modifier = Modifier.height(AppDimens.Padding.Tiny))
        }

        items(
            items = items,
            key = { it.id }
        ) { specialization ->
            SpecializationCard(
                specialization = specialization,
                onPinClick = { onPinClick(specialization.id) },
                modifier = Modifier.animateItem(),
                onItemClick = { onItemClick(specialization.id) }
            )
        }
        if (isNextPageLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.Padding.Normal),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(AppDimens.Components.ProgressIndicatorSize))
                }
            }
        }
    }
}

@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onProfileClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = AppDimens.Padding.Normal,
                end = AppDimens.Padding.Normal,
                top = AppDimens.Padding.Normal
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(Res.string.main_search_placeholder)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        ProfileIcon(onProfileClick)
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(message: StringResource, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(message),
            style = AppTypography.titleSmall
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = AppDimens.Padding.Normal)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(AppDimens.SpacerHeight.ExtraTiny))
            Text(stringResource(Res.string.main_refresh_button))
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_list),
            contentDescription = null
        )
        Text(
            stringResource(Res.string.main_empty_state_text),
            style = AppTypography.titleSmall
        )
    }
}

@Composable
fun SpecializationCard(
    specialization: Specialization,
    onPinClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppDimens.Padding.Small)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.Components.CardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),

        ) {
        Column(
            modifier = Modifier.padding(
                top = AppDimens.Padding.Tiny,
                bottom = AppDimens.Padding.Normal,
                start = AppDimens.Padding.Normal,
                end = AppDimens.Padding.Normal,
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = specialization.title,
                    style = AppTypography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onPinClick() }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(Res.string.main_pin_icon_description),
                        tint = if (specialization.isPinned) PinIconActive else PinIconDefault
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.ExtraTiny))
            Text(
                text = specialization.description,
                style = AppTypography.labelSmall
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMainScreen() {
    MainScreen(onSpecializationClick = {}, onProfileClick = {})
}
