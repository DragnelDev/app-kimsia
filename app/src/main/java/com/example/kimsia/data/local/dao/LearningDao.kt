package com.example.kimsia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.kimsia.data.local.entity.ExerciseEntity
import com.example.kimsia.data.local.entity.ExerciseOptionEntity
import com.example.kimsia.data.local.entity.ExerciseWithOptions
import com.example.kimsia.data.local.entity.LanguageEntity
import com.example.kimsia.data.local.entity.LessonEntity
import com.example.kimsia.data.local.entity.ProfileEntity
import com.example.kimsia.data.local.entity.UnitEntity
import com.example.kimsia.data.local.entity.UserLessonProgressEntity
import com.example.kimsia.data.local.entity.UserStreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningDao {

    // Lectura ------------------------------------------------------------------------
    @Query("SELECT COUNT(*) FROM languages")
    suspend fun countLanguages(): Int

    @Query("SELECT * FROM languages WHERE code = :code LIMIT 1")
    fun observeLanguageByCode(code: String): Flow<LanguageEntity?>

    @Query("SELECT * FROM units WHERE language_id = :languageId ORDER BY order_index")
    fun observeUnits(languageId: Long): Flow<List<UnitEntity>>

    @Query("SELECT * FROM lessons WHERE unit_id = :unitId ORDER BY order_index")
    fun observeLessons(unitId: Long): Flow<List<LessonEntity>>

    @Query("SELECT lesson_id FROM user_lesson_progress WHERE user_id = :userId AND completed = 1")
    fun observeCompletedLessonIds(userId: String): Flow<List<Long>>

    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    fun observeProfile(userId: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    suspend fun getLesson(lessonId: Long): LessonEntity?

    @Transaction
    @Query("SELECT * FROM exercises WHERE lesson_id = :lessonId ORDER BY order_index")
    suspend fun getExercisesWithOptions(lessonId: Long): List<ExerciseWithOptions>

    @Query("SELECT * FROM profiles WHERE id = :userId LIMIT 1")
    suspend fun getProfile(userId: String): ProfileEntity?

    @Query("SELECT * FROM user_lesson_progress WHERE user_id = :userId AND lesson_id = :lessonId LIMIT 1")
    suspend fun getProgress(userId: String, lessonId: Long): UserLessonProgressEntity?

    // Escritura puntual -------------------------------------------------------------
    @Query("UPDATE profiles SET hearts = :hearts WHERE id = :userId")
    suspend fun setHearts(userId: String, hearts: Int)

    @Query("UPDATE profiles SET total_xp = total_xp + :xp, hearts = :hearts WHERE id = :userId")
    suspend fun addXpAndSetHearts(userId: String, xp: Int, hearts: Int)

    @Query("UPDATE user_lesson_progress SET synced = 1 WHERE id = :progressId")
    suspend fun markProgressSynced(progressId: String)

    @Query("DELETE FROM exercises WHERE lesson_id = :lessonId")
    suspend fun deleteExercises(lessonId: Long)

    /** Borra el catálogo completo (unidades, lecciones y ejercicios caen en cascada). No toca el progreso. */
    @Query("DELETE FROM languages")
    suspend fun clearCatalog()

    // Escritura (upsert) -------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLanguages(items: List<LanguageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUnits(items: List<UnitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessons(items: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExercises(items: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExerciseOptions(items: List<ExerciseOptionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(item: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(items: List<UserLessonProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStreak(item: UserStreakEntity)
}
