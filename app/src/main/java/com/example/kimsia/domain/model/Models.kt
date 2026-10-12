package com.example.kimsia.domain.model

data class Language(
    val id: Long,
    val code: String,
    val name: String,
    val flagIconUrl: String?,
    val isActive: Boolean,
)

data class LearningUnit(
    val id: Long,
    val languageId: Long,
    val title: String,
    val description: String,
    val orderIndex: Int,
)

data class Lesson(
    val id: Long,
    val unitId: Long,
    val title: String,
    val orderIndex: Int,
    val totalXp: Int,
)

data class UserProfile(
    val id: String,
    val username: String,
    val avatarUrl: String?,
    val streakDays: Int,
    val hearts: Int,
    val totalXp: Int,
)

enum class NodeType { LESSON, CHEST }

enum class NodeStatus { COMPLETED, CURRENT, LOCKED }

/** Nodo de la ruta serpenteante (una lección o el cofre final). */
data class PathNode(
    val id: Long,
    val title: String,
    val type: NodeType,
    val status: NodeStatus,
    val xp: Int,
)

data class LearningPath(
    val language: Language,
    val unit: LearningUnit,
    val nodes: List<PathNode>,
)
