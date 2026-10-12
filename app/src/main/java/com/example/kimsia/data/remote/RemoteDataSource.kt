package com.example.kimsia.data.remote

import com.example.kimsia.data.remote.dto.ExerciseDto
import com.example.kimsia.data.remote.dto.LanguageDto
import com.example.kimsia.data.remote.dto.LessonDto
import com.example.kimsia.data.remote.dto.ProfileDto
import com.example.kimsia.data.remote.dto.UnitDto
import com.example.kimsia.data.remote.dto.UserLessonProgressDto

/** Contrato de acceso remoto (PostgREST). Los repositorios solo conocen esta interfaz. */
interface RemoteDataSource {
    suspend fun fetchLanguages(): List<LanguageDto>
    suspend fun fetchUnits(): List<UnitDto>
    suspend fun fetchLessons(): List<LessonDto>
    suspend fun fetchExercises(lessonId: Long): List<ExerciseDto>
    suspend fun fetchProfile(userId: String): ProfileDto?
    suspend fun fetchProgress(userId: String): List<UserLessonProgressDto>
    suspend fun upsertProgress(progress: UserLessonProgressDto)
    suspend fun updateProfileStats(userId: String, totalXp: Int, hearts: Int)
}
