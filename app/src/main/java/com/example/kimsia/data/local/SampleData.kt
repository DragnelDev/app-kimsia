package com.example.kimsia.data.local

import com.example.kimsia.data.local.dao.LearningDao
import com.example.kimsia.data.local.entity.ExerciseEntity
import com.example.kimsia.data.local.entity.ExerciseOptionEntity
import com.example.kimsia.data.local.entity.LanguageEntity
import com.example.kimsia.data.local.entity.LessonEntity
import com.example.kimsia.data.local.entity.ProfileEntity
import com.example.kimsia.data.local.entity.UnitEntity
import com.example.kimsia.data.local.entity.UserLessonProgressEntity
import com.example.kimsia.data.local.entity.UserStreakEntity
import com.example.kimsia.domain.repository.AuthRepository

/**
 * Datos de ejemplo para desarrollar sin Supabase. Se usan solo si Room está vacío.
 * ⚠️ El vocabulario es provisional: debe ser validado por hablantes nativos antes de publicar.
 * Los `audio_url` son rutas relativas al bucket `audio-lessons` (si no existen, la UI muestra el estado de error).
 */
object SampleData {
    const val LOCAL_USER_ID = AuthRepository.LOCAL_USER_ID
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

        insertExercises(dao)

        dao.upsertProfile(ProfileEntity(LOCAL_USER_ID, "Estudiante Kimsia", null, streakDays = 5, hearts = 3, totalXp = 1240, createdAt = null))
        dao.upsertStreak(UserStreakEntity("streak-local", LOCAL_USER_ID, null, currentStreak = 5))
        dao.upsertProgress(listOf(UserLessonProgressEntity("p-31", LOCAL_USER_ID, 31, completed = true, score = 100, completedAt = null)))
    }

    private class Q(
        val lessonId: Long,
        val type: String,
        val question: String,
        val audio: String?,
        val correct: String,
        val wrong: List<String>,
    )

    private val questions = listOf(
        // Guaraní · Lección 2 (id 32)
        Q(32, "multiple_choice", "¿Cómo se dice \"Buenos días\" en Guaraní?", null, "Ko'ẽ porã", listOf("Ka'aru porã", "Pyhare porã", "Aguyje")),
        Q(32, "audio_listen", "Escucha y selecciona lo que oyes", "gn/maitei.mp3", "Maitei", listOf("Aguyje", "Iporã", "Jajotopata")),
        Q(32, "multiple_choice", "¿Qué significa \"Mba'éichapa\"?", "gn/mbaeichapa.mp3", "¿Cómo estás?", listOf("Gracias", "Hasta luego", "Buenas noches")),
        Q(32, "multiple_choice", "¿Cómo se dice \"Gracias\" en Guaraní?", null, "Aguyje", listOf("Maitei", "Iporã", "Jajotopata")),
        // Quechua · Lección 2 (id 12)
        Q(12, "multiple_choice", "¿Cómo se dice \"Hola, ¿cómo estás?\" en Quechua?", null, "Allillanchu", listOf("Tupananchiskama", "Yupaychani", "Sumaq punchaw")),
        Q(12, "audio_listen", "Escucha y selecciona lo que oyes", "qu/tupananchiskama.mp3", "Tupananchiskama", listOf("Allillanchu", "Yupaychani", "Sumaq punchaw")),
        Q(12, "multiple_choice", "¿Cómo se dice \"Gracias\" en Quechua?", null, "Yupaychani", listOf("Allillanchu", "Tupananchiskama", "Sumaq punchaw")),
        // Aimara · Lección 2 (id 22)
        Q(22, "multiple_choice", "¿Cómo se dice \"¿Cómo estás?\" en Aimara?", null, "Kamisaraki", listOf("Yuspajara", "Jikisiñkama", "Jallalla")),
        Q(22, "audio_listen", "Escucha y selecciona lo que oyes", "ay/jikisinkama.mp3", "Jikisiñkama", listOf("Kamisaraki", "Yuspajara", "Jallalla")),
        Q(22, "multiple_choice", "¿Cómo se dice \"Gracias\" en Aimara?", null, "Yuspajara", listOf("Kamisaraki", "Jikisiñkama", "Jallalla")),
    )

    private suspend fun insertExercises(dao: LearningDao) {
        val exercises = mutableListOf<ExerciseEntity>()
        val options = mutableListOf<ExerciseOptionEntity>()
        questions.forEachIndexed { index, q ->
            val exerciseId = 1000L + index
            exercises += ExerciseEntity(exerciseId, q.lessonId, q.type, q.question, q.audio, orderIndex = index + 1)
            (listOf(q.correct to true) + q.wrong.map { it to false }).forEachIndexed { k, (text, ok) ->
                options += ExerciseOptionEntity(exerciseId * 10 + k, exerciseId, text, null, ok)
            }
        }
        dao.upsertExercises(exercises)
        dao.upsertExerciseOptions(options)
    }
}
