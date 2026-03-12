package com.example.sobesai.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sobesai.data.Topic
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.theme.PinIconActive
import com.example.sobesai.presentation.theme.PinIconDefault
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.main_icon_description
import sobesai.composeapp.generated.resources.main_title

@Preview
@Composable
fun PreviewMainScreen(){
    MainScreen()
}
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val topics by viewModel.topics.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(AppDimens.Padding.Normal)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.main_title),
                    style = AppTypography.titleSmall,
                )
                Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Small))
            }

            items(
                items = topics,
                key = { topic -> topic.id },
            ) { topic ->
                TopicCard(
                    topic = topic,
                    onPinClick = { viewModel.onPinClicked(topic.id) }
                )
            }
        }
    }

}

@Composable
fun TopicCard(
    topic: Topic,
    onPinClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppDimens.Padding.Small),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.Components.CardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

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
                    text = topic.title,
                    style = AppTypography.titleSmall
                )
                IconButton(onClick = onPinClick) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(Res.string.main_icon_description),
                        tint = if (topic.isPinned) PinIconActive else PinIconDefault
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.ExtraTiny))
            Text(
                text = topic.description,
                style = AppTypography.labelSmall
            )
        }
    }
}