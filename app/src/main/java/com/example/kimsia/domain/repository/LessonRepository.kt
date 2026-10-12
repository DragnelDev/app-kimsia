package com.example.kimsia.domain.repository

import com.example.kimsia.domain.model.LessonContent
import com.example.kimsia.domain.model.LessonResult

interface LessonRepository {
    /** Intenta refrescar desde Supabase y, si no hay red, usa la caché de Room. */
    suspend fun loadLesson(lessonId: Long): Result<LessonContent>

    /** Persiste de inmediato los corazones (para que salir de la lección no los "devuelva"). */
    suspend fun updateHearts(hearts: Int): Result<Unit>

    /** Guarda progreso + XP en Room y lo sube a Supabase si hay sesión y red (offline-first). */
    suspend fun saveResult(result: LessonResult): Result<Unit>
}
