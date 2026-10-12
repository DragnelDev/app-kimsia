package com.example.kimsia.domain.model

enum class ExerciseType(val apiValue: String) {
    MULTIPLE_CHOICE("multiple_choice"),
    AUDIO_LISTEN("audio_listen"),
    TRANSLATE("translate"),
    PAIRS("pairs");

    companion object {
        fun fromApi(value: String): ExerciseType =
            entries.firstOrNull { it.apiValue == value } ?: MULTIPLE_CHOICE
    }
}

data class ExerciseOption(
    val id: Long,
    val text: String,
    val imageUrl: String?,
    val isCorrect: Boolean,
)

data class Exercise(
    val id: Long,
    val lessonId: Long,
    val type: ExerciseType,
    val questionText: String,
    /** URL ya resuelta y reproducible (o null si el ejercicio no tiene audio / no se pudo resolver). */
    val audioUrl: String?,
    val orderIndex: Int,
    val options: List<ExerciseOption>,
)

data class LessonContent(
    val lesson: Lesson,
    val exercises: List<Exercise>,
    val startingHearts: Int,
)

data class LessonResult(
    val lessonId: Long,
    val correctAnswers: Int,
    val totalExercises: Int,
    val xpEarned: Int,
    val heartsRemaining: Int,
    val completed: Boolean,
) {
    val scorePercent: Int get() = if (totalExercises == 0) 0 else correctAnswers * 100 / totalExercises
}
