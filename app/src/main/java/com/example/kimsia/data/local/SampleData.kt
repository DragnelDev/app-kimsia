package com.example.kimsia.data.local

import com.example.kimsia.data.local.dao.LearningDao
import com.example.kimsia.data.local.entity.LanguageEntity
import com.example.kimsia.data.local.entity.LessonEntity
import com.example.kimsia.data.local.entity.ProfileEntity
import com.example.kimsia.data.local.entity.UnitEntity
import com.example.kimsia.data.local.entity.UserLessonProgressEntity
import com.example.kimsia.data.local.entity.UserStreakEntity

/** Datos de ejemplo de la Fase 1 (equivalen a los de las capturas). Se eliminan al conectar Supabase. */
object SampleData {
    const val LOCAL_USER_ID = "local-user"
    const val DEFAULT_LANGUAGE_CODE = "gn"

    suspend fun insertInto(dao: LearningDao) {
        val languages = listOf(
            LanguageEntity(1, "qu", "Quechua", null, true),
            LanguageEntity(2, "ay", "Aimara", null, true),
            LanguageEntity(3, "gn", "Guaraní", null, true),
        )
        dao.upsertLanguages(languages)

        // Un ID de unidad por idioma; 3 lecciones por unidad (ids 11..13, 21..23, 31..33).
        dao.upsertUnits(languages.map {
            UnitEntity(it.id, it.id, "Saludos y Presentaciones", "Primeros saludos y cómo presentarte", 1)
        })
        val titles = listOf("Lección 1: Presentaciones", "Lección 2: Saludos", "Lección 3: Familia")
        dao.upsertLessons(languages.flatMap { lang ->
            titles.mapIndexed { i, title ->
                LessonEntity(id = lang.id * 10 + i + 1, unitId = lang.id, title = title, orderIndex = i + 1, totalXp = 20)
            }
        })

        dao.upsertProfile(ProfileEntity(LOCAL_USER_ID, "Estudiante Kimsia", null, streakDays = 5, hearts = 3, totalXp = 1240, createdAt = null))
        dao.upsertStreak(UserStreakEntity("streak-local", LOCAL_USER_ID, null, currentStreak = 5))
        dao.upsertProgress(listOf(UserLessonProgressEntity("p-31", LOCAL_USER_ID, 31, completed = true, score = 100, completedAt = null)))
    }
}
