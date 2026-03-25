package com.example.sobesai.presentation.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.presentation.theme.AppDimens

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
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppDimens.Components.ProgressIndicatorSize)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSpecializationList() {
    SpecializationList(
        items = listOf(
            Specialization(
                id = 1,
                title = "Android Developer",
                description = "Разработка мобильных приложений на Kotlin и Jetpack Compose.",
                isPinned = true,
                pinOrder = 1
            ),
            Specialization(
                id = 2,
                title = "2Android Developer",
                description = "2Разработка мобильных приложений на Kotlin и Jetpack Compose.",
                isPinned = false,
                pinOrder = 2
            )
        ),
        isNextPageLoading = false,
        onPinClick = {},
        onItemClick = {},
        onLoadNextPage = {}
    )
}
