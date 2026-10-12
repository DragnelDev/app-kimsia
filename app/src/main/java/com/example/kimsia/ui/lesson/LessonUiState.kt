package com.example.kimsia.ui.lesson

import com.example.kimsia.domain.model.Exercise
import com.example.kimsia.domain.model.ExerciseOption

sealed interface LessonUiState {

    data object Loading : LessonUiState

    data class Error(val message: String) : LessonUiState

    /** El usuario está respondiendo. [progress] es el avance ya consolidado (0f..1f). */
    data class ExerciseActive(
        val exercise: Exercise,
        val index: Int,
        val total: Int,
        val selectedOptionId: Long?,
        val hearts: Int,
        val progress: Float,
    ) : LessonUiState

    /** La respuesta fue comprobada: se muestra la hoja de retroalimentación. */
    data class AnswerVerified(
        val exercise: Exercise,
        val index: Int,
        val total: Int,
        val selectedOptionId: Long,
        val correctOption: ExerciseOption,
        val isCorrect: Boolean,
        val hearts: Int,
        val progress: Float,
    ) : LessonUiState

    data class LessonCompleted(
        val xpEarned: Int,
        val correctAnswers: Int,
        val total: Int,
        val accuracyPercent: Int,
    ) : LessonUiState

    /** Se acabaron los corazones antes de terminar la lección. */
    data object OutOfHearts : LessonUiState
}

/** Eventos de una sola vez (sonidos); no forman parte del estado de la pantalla. */
sealed interface LessonEvent {
    data object AnswerCorrect : LessonEvent
    data object AnswerWrong : LessonEvent
}
