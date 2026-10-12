package com.example.kimsia.data.remote

import com.example.kimsia.data.remote.dto.ExerciseDto
import com.example.kimsia.data.remote.dto.LanguageDto
import com.example.kimsia.data.remote.dto.LessonDto
import com.example.kimsia.data.remote.dto.ProfileDto
import com.example.kimsia.data.remote.dto.UnitDto
import com.example.kimsia.data.remote.dto.UserLessonProgressDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class SupabaseRemoteDataSource(private val client: SupabaseClient) : RemoteDataSource {

    override suspend fun fetchLanguages(): List<LanguageDto> =
        client.from("languages").select().decodeList<LanguageDto>()

    override suspend fun fetchUnits(): List<UnitDto> =
        client.from("units").select().decodeList<UnitDto>()

    override suspend fun fetchLessons(): List<LessonDto> =
        client.from("lessons").select().decodeList<LessonDto>()

    /** Select anidado: cada ejercicio trae sus `exercise_options`. */
    override suspend fun fetchExercises(lessonId: Long): List<ExerciseDto> =
        client.from("exercises")
            .select(Columns.raw("*, exercise_options(*)")) {
                filter { eq("lesson_id", lessonId) }
            }
            .decodeList<ExerciseDto>()

    override suspend fun fetchProfile(userId: String): ProfileDto? =
        client.from("profiles")
            .select { filter { eq("id", userId) } }
            .decodeSingleOrNull<ProfileDto>()

    override suspend fun fetchProgress(userId: String): List<UserLessonProgressDto> =
        client.from("user_lesson_progress")
            .select { filter { eq("user_id", userId) } }
            .decodeList<UserLessonProgressDto>()

    override suspend fun upsertProgress(progress: UserLessonProgressDto) {
        client.from("user_lesson_progress").upsert(progress) {
            onConflict = "user_id,lesson_id"
        }
    }

    override suspend fun updateProfileStats(userId: String, totalXp: Int, hearts: Int) {
        client.from("profiles").update({
            set("total_xp", totalXp)
            set("hearts", hearts)
        }) {
            filter { eq("id", userId) }
        }
    }
}
