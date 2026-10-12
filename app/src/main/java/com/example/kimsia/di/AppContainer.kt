package com.example.kimsia.di

import android.content.Context
import com.example.kimsia.data.local.KimsiaDatabase
import com.example.kimsia.data.remote.RemoteDataSource
import com.example.kimsia.data.remote.SupabaseProvider
import com.example.kimsia.data.remote.SupabaseRemoteDataSource
import com.example.kimsia.data.repository.AudioRepositoryImpl
import com.example.kimsia.data.repository.AuthRepositoryImpl
import com.example.kimsia.data.repository.LearningRepositoryImpl
import com.example.kimsia.data.repository.LessonRepositoryImpl
import com.example.kimsia.domain.repository.AudioRepository
import com.example.kimsia.domain.repository.AuthRepository
import com.example.kimsia.domain.repository.LearningRepository
import com.example.kimsia.domain.repository.LessonRepository
import com.example.kimsia.domain.usecase.ObserveLearningPathUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** Contenedor de dependencias manual (sin plugins extra). Se puede migrar a Hilt/Koin más adelante. */
class AppContainer(context: Context) {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val database = KimsiaDatabase.build(context.applicationContext)

    // null si no hay credenciales en local.properties → modo 100% offline
    private val supabase = SupabaseProvider.create()
    private val remote: RemoteDataSource? = supabase?.let { SupabaseRemoteDataSource(it) }

    val authRepository: AuthRepository = AuthRepositoryImpl(supabase, appScope)
    val audioRepository: AudioRepository = AudioRepositoryImpl(supabase)
    val learningRepository: LearningRepository = LearningRepositoryImpl(database, remote, authRepository)
    val lessonRepository: LessonRepository = LessonRepositoryImpl(database, remote, authRepository, audioRepository)

    val observeLearningPath = ObserveLearningPathUseCase(learningRepository)
}
