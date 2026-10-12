package com.example.kimsia.data.mapper

import com.example.kimsia.data.local.entity.ExerciseEntity
import com.example.kimsia.data.local.entity.ExerciseOptionEntity
import com.example.kimsia.data.local.entity.ExerciseWithOptions
import com.example.kimsia.data.local.entity.LanguageEntity
import com.example.kimsia.data.local.entity.LessonEntity
import com.example.kimsia.data.local.entity.ProfileEntity
import com.example.kimsia.data.local.entity.UnitEntity
import com.example.kimsia.data.local.entity.UserLessonProgressEntity
import com.example.kimsia.data.local.entity.UserStreakEntity
import com.example.kimsia.data.remote.dto.ExerciseDto
import com.example.kimsia.data.remote.dto.ExerciseOptionDto
import com.example.kimsia.data.remote.dto.LanguageDto
import com.example.kimsia.data.remote.dto.LessonDto
import com.example.kimsia.data.remote.dto.ProfileDto
import com.example.kimsia.data.remote.dto.UnitDto
import com.example.kimsia.data.remote.dto.UserLessonProgressDto
import com.example.kimsia.data.remote.dto.UserStreakDto
import com.example.kimsia.domain.model.Exercise
import com.example.kimsia.domain.model.ExerciseOption
import com.example.kimsia.domain.model.ExerciseType
import com.example.kimsia.domain.model.Language
import com.example.kimsia.domain.model.LearningUnit
import com.example.kimsia.domain.model.Lesson
import com.example.kimsia.domain.model.UserProfile

// DTO -> Entity (sincronización remota -> Room) -------------------------------------
fun LanguageDto.toEntity() = LanguageEntity(id, code, name, flagIconUrl, isActive)
fun UnitDto.toEntity() = UnitEntity(id, languageId, title, description, orderIndex)
fun LessonDto.toEntity() = LessonEntity(id, unitId, title, orderIndex, totalXp)
fun ExerciseDto.toEntity() = ExerciseEntity(id, lessonId, type, questionText, audioUrl, orderIndex)
fun ExerciseOptionDto.toEntity() = ExerciseOptionEntity(id, exerciseId, optionText, imageUrl, isCorrect)
fun ProfileDto.toEntity() = ProfileEntity(id, username, avatarUrl, streakDays, hearts, totalXp, createdAt)
fun UserLessonProgressDto.toEntity() = UserLessonProgressEntity(id, userId, lessonId, completed, score, completedAt, synced = true)
fun UserStreakDto.toEntity() = UserStreakEntity(id, userId, lastActiveDate, currentStreak)

// Entity -> Domain -------------------------------------------------------------------
fun LanguageEntity.toDomain() = Language(id, code, name, flagIconUrl, isActive)
fun UnitEntity.toDomain() = LearningUnit(id, languageId, title, description, orderIndex)
fun LessonEntity.toDomain() = Lesson(id, unitId, title, orderIndex, totalXp)
fun ProfileEntity.toDomain() = UserProfile(id, username, avatarUrl, streakDays, hearts, totalXp)

fun ExerciseOptionEntity.toDomain() = ExerciseOption(id, optionText, imageUrl, isCorrect)

/** [resolveAudio] convierte la ruta guardada en una URL reproducible (Supabase Storage). */
fun ExerciseWithOptions.toDomain(resolveAudio: (String?) -> String?) = Exercise(
    id = exercise.id,
    lessonId = exercise.lessonId,
    type = ExerciseType.fromApi(exercise.type),
    questionText = exercise.questionText,
    audioUrl = resolveAudio(exercise.audioUrl),
    orderIndex = exercise.orderIndex,
    options = options.sortedBy { it.id }.map { it.toDomain() },
)
