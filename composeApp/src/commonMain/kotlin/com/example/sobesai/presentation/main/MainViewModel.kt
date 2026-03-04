package com.example.sobesai.presentation.main

import androidx.lifecycle.ViewModel
import com.example.sobesai.data.Topic
import com.example.sobesai.data.TopicsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val repository: TopicsRepository = TopicsRepository()
) : ViewModel() {
    private val _topics = MutableStateFlow(repository.getList())
    val topics = _topics.asStateFlow()
    private var pinOrderCounter = 0

    fun onPinClicked(topicId: Int) {
        _topics.update {
            val updatedList = it.map { topic ->
                if (topic.id == topicId) {
                    val newPinnedState = !topic.isPinned
                    topic.copy(
                        isPinned = newPinnedState,
                        pinOrder = if (newPinnedState) ++pinOrderCounter else null
                    )
                } else {
                    topic
                }
            }
            updatedList.sortedWith(
                compareByDescending<Topic> { it.isPinned }
                    .thenByDescending { it.pinOrder ?: 0 }
                    .thenBy { it.id }
            )
        }
    }
}
