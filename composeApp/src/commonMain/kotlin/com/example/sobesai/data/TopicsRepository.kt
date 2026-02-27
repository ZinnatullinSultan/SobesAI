package com.example.sobesai.data

data class Topic(
    val id: Int,
    val title: String,
    val description: String,
    val isPinned: Boolean = false,
    val pinOrder: Int? = null
)

class TopicsRepository {
    fun getList(): List<Topic> {
        return listOf(
            Topic(1, "Android Разработка", "Kotlin, Jetpack Compose, Архитектура, Корутины"),
            Topic(2, "iOS Разработка", "Swift, SwiftUI, UIKit, CoreData, Многопоточность"),
            Topic(3, "Frontend Разработка", "JavaScript, TypeScript, React, Vue, HTML/CSS"),
            Topic(4, "Backend Разработка", "Java, Spring, Python, Go, Базы данных, REST API"),
            Topic(5, "QA (Тестирование)", "Ручное тестирование, Автоматизация, Postman, SQL"),
            Topic(6, "Системный Анализ", "Сбор требований, BPMN, UML, Работа с заказчиком")
        )
    }
}
