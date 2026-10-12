package com.example.kimsia.data.repository

import androidx.room.withTransaction
import com.example.kimsia.data.local.KimsiaDatabase
import com.example.kimsia.data.local.SampleData
import com.example.kimsia.data.mapper.toDomain
import com.example.kimsia.data.mapper.toEntity
import com.example.kimsia.data.remote.RemoteDataSource
import com.example.kimsia.domain.model.Language
import com.example.kimsia.domain.model.LearningUnit
import com.example.kimsia.domain.model.Lesson
import com.example.kimsia.domain.model.UserProfile
import com.example.kimsia.domain.repository.AuthRepository
import com.example.kimsia.domain.repository.LearningRepository
import com.example.kimsia.domain.util.suspendRunCatching
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/** Room es la fuente de verdad (offline-first); Supabase solo refresca la caché. */
class LearningRepositoryImpl(
    private val db: KimsiaDatabase,
    private val remote: RemoteDataSource?,
    private val auth: AuthRepository,
) : LearningRepository {

    private val dao = db.learningDao()
    private val selectedLanguageCode = MutableStateFlow(SampleData.DEFAULT_LANGUAGE_CODE)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSelectedLanguage(): Flow<Language?> =
        selectedLanguageCode.flatMapLatest { dao.observeLanguageByCode(it) }.map { it?.toDomain() }

    override fun observeUnits(languageId: Long): Flow<List<LearningUnit>> =
        dao.observeUnits(languageId).map { list -> list.map { it.toDomain() } }

    override fun observeLessons(unitId: Long): Flow<List<Lesson>> =
        dao.observeLessons(unitId).map { list -> list.map { it.toDomain() } }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCompletedLessonIds(): Flow<Set<Long>> =
        auth.userId.flatMapLatest { dao.observeCompletedLessonIds(it) }.map { it.toSet() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeProfile(): Flow<UserProfile?> =
        auth.userId.flatMapLatest { dao.observeProfile(it) }.map { it?.toDomain() }

    override fun selectLanguage(code: String) {
        selectedLanguageCode.value = code
    }

    override suspend fun refresh(): Result<Unit> = suspendRunCatching {
        // 1) Que la UI nunca quede vacía: datos de ejemplo solo si Room está vacío.
        if (dao.countLanguages() == 0) SampleData.insertInto(dao)

        // 2) Sincronización remota (si hay credenciales). Cualquier fallo → seguimos con la caché.
        val source = remote ?: return@suspendRunCatching
        val languages = source.fetchLanguages()
        if (languages.isEmpty()) return@suspendRunCatching // catálogo remoto aún vacío: conservar local

        val units = source.fetchUnits()
        val lessons = source.fetchLessons()
        db.withTransaction {
            dao.clearCatalog()
            dao.upsertLanguages(languages.map { it.toEntity() })
            dao.upsertUnits(units.map { it.toEntity() })
            dao.upsertLessons(lessons.map { it.toEntity() })
        }

        if (!auth.isLocalUser) {
            val uid = auth.userId.value
            source.fetchProfile(uid)?.let { dao.upsertProfile(it.toEntity()) }
            dao.upsertProgress(source.fetchProgress(uid).map { it.toEntity() })
        }
    }
}
