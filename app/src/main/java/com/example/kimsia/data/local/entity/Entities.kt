package com.example.kimsia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Contenido (ids numéricos de Supabase) -------------------------------------------------

@Entity(tableName = "languages")
data class LanguageEntity(
    @PrimaryKey val id: Long,
    val code: String,
    val name: String,
    @ColumnInfo(name = "flag_icon_url") val flagIconUrl: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
)

@Entity(
    tableName = "units",
    foreignKeys = [ForeignKey(LanguageEntity::class, ["id"], ["language_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("language_id")],
)
data class UnitEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "language_id") val languageId: Long,
    val title: String,
    val description: String,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
)

@Entity(
    tableName = "lessons",
    foreignKeys = [ForeignKey(UnitEntity::class, ["id"], ["unit_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("unit_id")],
)
data class LessonEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "unit_id") val unitId: Long,
    val title: String,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    @ColumnInfo(name = "total_xp") val totalXp: Int,
)

/** type: 'multiple_choice' | 'audio_listen' | 'translate' | 'pairs' */
@Entity(
    tableName = "exercises",
    foreignKeys = [ForeignKey(LessonEntity::class, ["id"], ["lesson_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("lesson_id")],
)
data class ExerciseEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "lesson_id") val lessonId: Long,
    val type: String,
    @ColumnInfo(name = "question_text") val questionText: String,
    @ColumnInfo(name = "audio_url") val audioUrl: String?,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
)

@Entity(
    tableName = "exercise_options",
    foreignKeys = [ForeignKey(ExerciseEntity::class, ["id"], ["exercise_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("exercise_id")],
)
data class ExerciseOptionEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    @ColumnInfo(name = "option_text") val optionText: String,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "is_correct") val isCorrect: Boolean,
)

// Usuario (ids uuid de Supabase Auth) ---------------------------------------------------

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val username: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "streak_days") val streakDays: Int,
    val hearts: Int,
    @ColumnInfo(name = "total_xp") val totalXp: Int,
    @ColumnInfo(name = "created_at") val createdAt: String?,
)

/** [synced] = false marca progreso hecho offline pendiente de subir a Supabase (Fase 4). */
@Entity(
    tableName = "user_lesson_progress",
    indices = [Index(value = ["user_id", "lesson_id"], unique = true)],
)
data class UserLessonProgressEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "lesson_id") val lessonId: Long,
    val completed: Boolean,
    val score: Int,
    @ColumnInfo(name = "completed_at") val completedAt: String?,
    val synced: Boolean = true,
)

@Entity(tableName = "user_streaks")
data class UserStreakEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "last_active_date") val lastActiveDate: String?,
    @ColumnInfo(name = "current_streak") val currentStreak: Int,
)
