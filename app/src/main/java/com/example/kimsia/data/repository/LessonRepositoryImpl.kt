package com.example.kimsia.data.repository

import androidx.room.withTransaction
import com.example.kimsia.data.local.KimsiaDatabase
import com.example.kimsia.data.local.entity.UserLessonProgressEntity
import com.example.kimsia.data.mapper.toDomain
import com.example.kimsia.data.mapper.toEntity
import com.example.kimsia.data.remote.RemoteDataSource
import com.example.kimsia.data.remote.dto.UserLessonProgressDto
import com.example.kimsia.domain.model.LessonContent
import com.example.kimsia.domain.model.LessonResult
import com.example.kimsia.domain.repository.AudioRepository
import com.example.kimsia.domain.repository.AuthRepository
import com.example.kimsia.domain.repository.LessonRepository
import com.example.kimsia.domain.util.suspendRunCatching
import java.time.Instant
import java.util.UUID

class LessonRepositoryImpl(
    private val db: KimsiaDatabase,
    private val remote: RemoteDataSource?,
    private val auth: AuthRepository,
    private val audioRepository: AudioRepository,
) : LessonRepository {

    private val dao = db.learningDao()

    override suspend fun loadLesson(lessonId: Long): Result<LessonContent> = suspendRunCatching {
        // Refresco best-effort desde Supabase; si falla (sin red / sin credenciales) usamos Room.
        remote?.let { source ->
            val remoteExercises = suspendRunCatching { source.fetchExercises(lessonId) }.getOrNull().orEmpty()
            if (remoteExercises.isNotEmpty()) {
                db.withTransaction {
                    dao.deleteExercises(lessonId)
                    dao.upsertExercises(remoteExercises.map { it.toEntity() })
                    dao.upsertExerciseOptions(remoteExercises.flatMap { dto -> dto.options.map { it.toEntity() } })
                }
            }
        }

        val lesson = dao.getLesson(lessonId) ?: error("No se encontró la lección")
        val exercises = dao.getExercisesWithOptions(lessonId).map { row ->
            row.toDomain { path -> audioRepository.resolveUrl(path).getOrNull() }
        }
        if (exercises.isEmpty()) error("Esta lección todavía no tiene ejercicios")

        val hearts = dao.getProfile(auth.userId.value)?.hearts ?: DEFAULT_HEARTS
        LessonContent(lesson.toDomain(), exercises, hearts)
    }

    override suspend fun updateHearts(hearts: Int): Result<Unit> = suspendRunCatching {
        dao.setHearts(auth.userId.value, hearts.coerceAtLeast(0))
    }

    override suspend fun saveResult(result: LessonResult): Result<Unit> = suspendRunCatching {
        val userId = auth.userId.value
        val existing = dao.getProgress(userId, result.lessonId)

        var progress: UserLessonProgressEntity? = null
        db.withTransaction {
            if (result.completed) {
                progress = UserLessonProgressEntity(
                    id = existing?.id ?: UUID.randomUUID().toString(),
                    userId = userId,
                    lessonId = result.lessonId,
                    completed = true,
                    score = maxOf(existing?.score ?: 0, result.scorePercent),
                    completedAt = Instant.now().toString(),
                    synced = false,
                ).also { dao.upsertProgress(listOf(it)) }
            }
            dao.addXpAndSetHearts(userId, result.xpEarned, result.heartsRemaining.coerceAtLeast(0))
        }

        // Subida a Supabase: solo con sesión real y credenciales. Un fallo deja `synced = false`
        // para reintentar más adelante (la lección ya cuenta localmente).
        val source = remote
        if (source != null && !auth.isLocalUser) {
            suspendRunCatching {
                progress?.let { source.upsertProgress(it.toDto()) }
                dao.getProfile(userId)?.let { source.updateProfileStats(userId, it.totalXp, it.hearts) }
                progress?.let { dao.markProgressSynced(it.id) }
            }
        }
    }

    private fun UserLessonProgressEntity.toDto() =
        UserLessonProgressDto(id, userId, lessonId, completed, score, completedAt)

    private companion object {
        const val DEFAULT_HEARTS = 5
    }
}
