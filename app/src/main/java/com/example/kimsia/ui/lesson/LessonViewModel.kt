package com.example.kimsia.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kimsia.KimsiaApplication
import com.example.kimsia.domain.model.Exercise
import com.example.kimsia.domain.model.LessonContent
import com.example.kimsia.domain.model.LessonResult
import com.example.kimsia.domain.repository.LessonRepository
import com.example.kimsia.domain.usecase.calculateXp
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LessonViewModel(
    private val lessonId: Long,
    private val repository: LessonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LessonUiState>(LessonUiState.Loading)
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LessonEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<LessonEvent> = _events.asSharedFlow()

    private var content: LessonContent? = null
    private var exercises: List<Exercise> = emptyList()
    private var index = 0
    private var hearts = 0
    private var correctAnswers = 0

    init {
        load()
    }

    fun load() {
        _uiState.value = LessonUiState.Loading
        viewModelScope.launch {
            repository.loadLesson(lessonId).fold(
                onSuccess = { loaded ->
                    content = loaded
                    // Orden aleatorio de opciones, fijado una sola vez por sesión.
                    exercises = loaded.exercises.map { it.copy(options = it.options.shuffled()) }
                    index = 0
                    correctAnswers = 0
                    hearts = loaded.startingHearts
                    if (hearts <= 0) _uiState.value = LessonUiState.OutOfHearts else showExercise()
                },
                onFailure = { _uiState.value = LessonUiState.Error(it.message ?: "No se pudo cargar la lección") },
            )
        }
    }

    fun selectOption(optionId: Long) {
        val state = _uiState.value as? LessonUiState.ExerciseActive ?: return
        _uiState.value = state.copy(selectedOptionId = optionId)
    }

    fun checkAnswer() {
        val state = _uiState.value as? LessonUiState.ExerciseActive ?: return
        val selected = state.exercise.options.firstOrNull { it.id == state.selectedOptionId } ?: return
        val correctOption = state.exercise.options.firstOrNull { it.isCorrect } ?: return

        val isCorrect = selected.isCorrect
        if (isCorrect) {
            correctAnswers++
            _events.tryEmit(LessonEvent.AnswerCorrect)
        } else {
            hearts = (hearts - 1).coerceAtLeast(0)
            _events.tryEmit(LessonEvent.AnswerWrong)
            persistHearts()
        }

        _uiState.value = LessonUiState.AnswerVerified(
            exercise = state.exercise,
            index = state.index,
            total = state.total,
            selectedOptionId = selected.id,
            correctOption = correctOption,
            isCorrect = isCorrect,
            hearts = hearts,
            progress = (state.index + 1f) / state.total,
        )
    }

    /** Botón "Continuar" / "Entendido" de la hoja de retroalimentación. */
    fun continueLesson() {
        if (_uiState.value !is LessonUiState.AnswerVerified) return
        when {
            hearts <= 0 -> finish(completed = false)
            index + 1 < exercises.size -> {
                index++
                showExercise()
            }
            else -> finish(completed = true)
        }
    }

    private fun showExercise() {
        _uiState.value = LessonUiState.ExerciseActive(
            exercise = exercises[index],
            index = index,
            total = exercises.size,
            selectedOptionId = null,
            hearts = hearts,
            progress = index.toFloat() / exercises.size,
        )
    }

    private fun finish(completed: Boolean) {
        val lesson = content?.lesson ?: return
        val total = exercises.size
        val xp = if (completed) calculateXp(lesson.totalXp, correctAnswers, total) else 0
        val result = LessonResult(
            lessonId = lesson.id,
            correctAnswers = correctAnswers,
            totalExercises = total,
            xpEarned = xp,
            heartsRemaining = hearts,
            completed = completed,
        )
        viewModelScope.launch {
            // NonCancellable: el progreso se guarda aunque el usuario salga de la pantalla.
            withContext(NonCancellable) { repository.saveResult(result) }
            _uiState.value = if (completed) {
                LessonUiState.LessonCompleted(xp, correctAnswers, total, result.scorePercent)
            } else {
                LessonUiState.OutOfHearts
            }
        }
    }

    private fun persistHearts() {
        val current = hearts
        viewModelScope.launch { withContext(NonCancellable) { repository.updateHearts(current) } }
    }

    companion object {
        fun factory(lessonId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as KimsiaApplication).container
                LessonViewModel(lessonId, container.lessonRepository)
            }
        }
    }
}
