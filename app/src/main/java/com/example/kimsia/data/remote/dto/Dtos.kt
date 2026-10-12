package com.example.kimsia.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTOs para PostgREST de Supabase (snake_case). Se usarán desde la Fase 2.

@Serializable
data class LanguageDto(
    val id: Long,
    val code: String,
    val name: String,
    @SerialName("flag_icon_url") val flagIconUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
)

@Serializable
data class UnitDto(
    val id: Long,
    @SerialName("language_id") val languageId: Long,
    val title: String,
    val description: String = "",
    @SerialName("order_index") val orderIndex: Int,
)

@Serializable
data class LessonDto(
    val id: Long,
    @SerialName("unit_id") val unitId: Long,
    val title: String,
    @SerialName("order_index") val orderIndex: Int,
    @SerialName("total_xp") val totalXp: Int = 0,
)

/** Soporta select anidado: `exercises?select=*,exercise_options(*)`. */
@Serializable
data class ExerciseDto(
    val id: Long,
    @SerialName("lesson_id") val lessonId: Long,
    val type: String,
    @SerialName("question_text") val questionText: String,
    @SerialName("audio_url") val audioUrl: String? = null,
    @SerialName("order_index") val orderIndex: Int,
    @SerialName("exercise_options") val options: List<ExerciseOptionDto> = emptyList(),
)

@Serializable
data class ExerciseOptionDto(
    val id: Long,
    @SerialName("exercise_id") val exerciseId: Long,
    @SerialName("option_text") val optionText: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_correct") val isCorrect: Boolean = false,
)

@Serializable
data class ProfileDto(
    val id: String,
    val username: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("streak_days") val streakDays: Int = 0,
    val hearts: Int = 5,
    @SerialName("total_xp") val totalXp: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class UserLessonProgressDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("lesson_id") val lessonId: Long,
    val completed: Boolean = false,
    val score: Int = 0,
    @SerialName("completed_at") val completedAt: String? = null,
)

@Serializable
data class UserStreakDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("last_active_date") val lastActiveDate: String? = null,
    @SerialName("current_streak") val currentStreak: Int = 0,
)
