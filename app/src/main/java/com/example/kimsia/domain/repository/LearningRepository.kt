package com.example.kimsia.domain.repository

import com.example.kimsia.domain.model.Language
import com.example.kimsia.domain.model.LearningUnit
import com.example.kimsia.domain.model.Lesson
import com.example.kimsia.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface LearningRepository {
    fun observeSelectedLanguage(): Flow<Language?>
    fun observeUnits(languageId: Long): Flow<List<LearningUnit>>
    fun observeLessons(unitId: Long): Flow<List<Lesson>>
    fun observeCompletedLessonIds(): Flow<Set<Long>>
    fun observeProfile(): Flow<UserProfile?>

    fun selectLanguage(code: String)

    /**
     * Garantiza datos locales (ejemplo si Room está vacío) y, si Supabase está configurado,
     * sincroniza el catálogo y los datos del usuario. Un fallo de red no rompe la app.
     */
    suspend fun refresh(): Result<Unit>
}
