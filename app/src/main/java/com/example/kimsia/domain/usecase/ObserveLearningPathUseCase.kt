package com.example.kimsia.domain.usecase

import com.example.kimsia.domain.model.Language
import com.example.kimsia.domain.model.LearningPath
import com.example.kimsia.domain.model.LearningUnit
import com.example.kimsia.domain.model.Lesson
import com.example.kimsia.domain.model.NodeStatus
import com.example.kimsia.domain.model.NodeType
import com.example.kimsia.domain.model.PathNode
import com.example.kimsia.domain.repository.LearningRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveLearningPathUseCase(private val repository: LearningRepository) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<LearningPath?> =
        repository.observeSelectedLanguage().flatMapLatest { language ->
            if (language == null) {
                flowOf(null)
            } else {
                repository.observeUnits(language.id).flatMapLatest unitsFlow@{ units ->
                    val unit = units.firstOrNull() ?: return@unitsFlow flowOf(null)
                    combine(
                        repository.observeLessons(unit.id),
                        repository.observeCompletedLessonIds(),
                    ) { lessons, completed ->
                        buildLearningPath(language, unit, lessons, completed)
                    }
                }
            }
        }
}

/**
 * Regla de la ruta: lecciones completadas → la primera pendiente es la actual → el resto bloqueadas.
 * El cofre final se activa cuando todas las lecciones están completas.
 */
fun buildLearningPath(
    language: Language,
    unit: LearningUnit,
    lessons: List<Lesson>,
    completedLessonIds: Set<Long>,
): LearningPath {
    val sorted = lessons.sortedBy { it.orderIndex }
    val currentIndex = sorted.indexOfFirst { it.id !in completedLessonIds }

    val lessonNodes = sorted.mapIndexed { index, lesson ->
        PathNode(
            id = lesson.id,
            title = lesson.title,
            type = NodeType.LESSON,
            status = when {
                lesson.id in completedLessonIds -> NodeStatus.COMPLETED
                index == currentIndex -> NodeStatus.CURRENT
                else -> NodeStatus.LOCKED
            },
            xp = lesson.totalXp,
        )
    }

    val allDone = sorted.isNotEmpty() && currentIndex == -1
    val chest = PathNode(
        id = -unit.id,
        title = "Cofre Final",
        type = NodeType.CHEST,
        status = if (allDone) NodeStatus.CURRENT else NodeStatus.LOCKED,
        xp = 0,
    )
    return LearningPath(language, unit, lessonNodes + chest)
}
