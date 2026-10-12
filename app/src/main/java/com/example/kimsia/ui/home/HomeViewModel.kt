package com.example.kimsia.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kimsia.KimsiaApplication
import com.example.kimsia.domain.model.LearningPath
import com.example.kimsia.domain.model.UserProfile
import com.example.kimsia.domain.repository.LearningRepository
import com.example.kimsia.domain.usecase.ObserveLearningPathUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val path: LearningPath? = null,
    val profile: UserProfile? = null,
)

class HomeViewModel(
    repository: LearningRepository,
    observeLearningPath: ObserveLearningPathUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        combine(observeLearningPath(), repository.observeProfile()) { path, profile ->
            HomeUiState(isLoading = path == null && profile == null, path = path, profile = profile)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        viewModelScope.launch { repository.seedIfNeeded() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as KimsiaApplication).container
                HomeViewModel(container.learningRepository, container.observeLearningPath)
            }
        }
    }
}
