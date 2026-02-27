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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sobesai.data.Topic
import com.example.sobesai.data.TopicsRepository
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.main_title
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.Alignment

@Preview
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val topics by viewModel.topics.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            Text(
                text = stringResource(Res.string.main_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
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

@Composable
fun TopicCard(topic: Topic, onPinClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

    ) {
        Column(modifier = Modifier.padding(top = 5.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)) {
            Row( horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = topic.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onPinClick) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Закрепить",
                        tint = if (topic.isPinned) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(text = topic.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}