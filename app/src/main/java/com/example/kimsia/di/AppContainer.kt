package com.example.kimsia.di

import android.content.Context
import com.example.kimsia.data.local.KimsiaDatabase
import com.example.kimsia.data.repository.LearningRepositoryImpl
import com.example.kimsia.domain.repository.LearningRepository
import com.example.kimsia.domain.usecase.ObserveLearningPathUseCase

/** Contenedor de dependencias manual (sin plugins extra). Se puede migrar a Hilt/Koin más adelante. */
class AppContainer(context: Context) {
    private val database = KimsiaDatabase.build(context)

    val learningRepository: LearningRepository = LearningRepositoryImpl(database.learningDao())
    val observeLearningPath = ObserveLearningPathUseCase(learningRepository)
}
