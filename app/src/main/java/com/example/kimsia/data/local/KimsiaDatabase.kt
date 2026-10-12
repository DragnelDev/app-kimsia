package com.example.kimsia.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kimsia.data.local.dao.LearningDao
import com.example.kimsia.data.local.entity.ExerciseEntity
import com.example.kimsia.data.local.entity.ExerciseOptionEntity
import com.example.kimsia.data.local.entity.LanguageEntity
import com.example.kimsia.data.local.entity.LessonEntity
import com.example.kimsia.data.local.entity.ProfileEntity
import com.example.kimsia.data.local.entity.UnitEntity
import com.example.kimsia.data.local.entity.UserLessonProgressEntity
import com.example.kimsia.data.local.entity.UserStreakEntity

@Database(
    entities = [
        LanguageEntity::class,
        UnitEntity::class,
        LessonEntity::class,
        ExerciseEntity::class,
        ExerciseOptionEntity::class,
        ProfileEntity::class,
        UserLessonProgressEntity::class,
        UserStreakEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class KimsiaDatabase : RoomDatabase() {
    abstract fun learningDao(): LearningDao

    companion object {
        fun build(context: Context): KimsiaDatabase =
            Room.databaseBuilder(context, KimsiaDatabase::class.java, "kimsia.db").build()
    }
}
