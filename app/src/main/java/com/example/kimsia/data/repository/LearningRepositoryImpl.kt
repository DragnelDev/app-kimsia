package com.example.kimsia.data.repository

import com.example.kimsia.data.local.SampleData
import com.example.kimsia.data.local.dao.LearningDao
import com.example.kimsia.data.mapper.toDomain
import com.example.kimsia.domain.model.Language
import com.example.kimsia.domain.model.LearningUnit
import com.example.kimsia.domain.model.Lesson
import com.example.kimsia.domain.model.UserProfile
import com.example.kimsia.domain.repository.LearningRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/** Room es la fuente de verdad (offline-first). En la Fase 2 se añade el RemoteDataSource de Supabase. */
class LearningRepositoryImpl(private val dao: LearningDao) : LearningRepository {

    private val selectedLanguageCode = MutableStateFlow(SampleData.DEFAULT_LANGUAGE_CODE)
    private val userId = SampleData.LOCAL_USER_ID // Fase 2: id real de Supabase Auth

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSelectedLanguage(): Flow<Language?> =
        selectedLanguageCode.flatMapLatest { dao.observeLanguageByCode(it) }.map { it?.toDomain() }

    override fun observeUnits(languageId: Long): Flow<List<LearningUnit>> =
        dao.observeUnits(languageId).map { list -> list.map { it.toDomain() } }

    override fun observeLessons(unitId: Long): Flow<List<Lesson>> =
        dao.observeLessons(unitId).map { list -> list.map { it.toDomain() } }

    override fun observeCompletedLessonIds(): Flow<Set<Long>> =
        dao.observeCompletedLessonIds(userId).map { it.toSet() }

    override fun observeProfile(): Flow<UserProfile?> =
        dao.observeProfile(userId).map { it?.toDomain() }

    override fun selectLanguage(code: String) {
        selectedLanguageCode.value = code
    }

    override suspend fun seedIfNeeded() {
        if (dao.countLanguages() == 0) SampleData.insertInto(dao)
    }
}
