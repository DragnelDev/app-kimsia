package com.example.kimsia.ui.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kimsia.audio.AudioPlaybackState
import com.example.kimsia.domain.model.Exercise
import com.example.kimsia.ui.components.AudioPlayCard
import com.example.kimsia.ui.components.AudioPlayHero
import com.example.kimsia.ui.components.OptionCard
import com.example.kimsia.ui.components.OptionVisualState
import com.example.kimsia.ui.theme.EmeraldDark
import com.example.kimsia.ui.theme.EmeraldSoft
import com.example.kimsia.ui.theme.Ink

/** Opción múltiple (también cubre TRANSLATE/PAIRS como fallback hasta tener su UI propia). */
@Composable
fun MultipleChoiceExercise(
    exercise: Exercise,
    selectedOptionId: Long?,
    verified: LessonUiState.AnswerVerified?,
    playback: AudioPlaybackState,
    onToggleAudio: (String) -> Unit,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ExerciseLabel("Elige la traducción correcta")
        ExerciseTitle(exercise.questionText)
        // Las preguntas con audio muestran el reproductor con el capibara.
        if (exercise.audioUrl != null) {
            AudioPlayCard(
                audioUrl = exercise.audioUrl,
                playback = playback,
                onClick = { onToggleAudio(exercise.audioUrl) },
            )
        }
        Spacer(Modifier.height(4.dp))
        OptionsList(exercise, selectedOptionId, verified, onSelect)
    }
}

/** Escucha el audio nativo y selecciona el texto (o imagen) que corresponde. */
@Composable
fun ListenAndSelectExercise(
    exercise: Exercise,
    selectedOptionId: Long?,
    verified: LessonUiState.AnswerVerified?,
    playback: AudioPlaybackState,
    onToggleAudio: (String) -> Unit,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ExerciseLabel("Escucha y selecciona")
        ExerciseTitle(exercise.questionText)
        AudioPlayHero(
            audioUrl = exercise.audioUrl,
            playback = playback,
            onClick = { exercise.audioUrl?.let(onToggleAudio) },
        )
        OptionsList(exercise, selectedOptionId, verified, onSelect)
    }
}

@Composable
private fun OptionsList(
    exercise: Exercise,
    selectedOptionId: Long?,
    verified: LessonUiState.AnswerVerified?,
    onSelect: (Long) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        exercise.options.forEachIndexed { i, option ->
            val visual = when {
                verified != null && option.id == verified.correctOption.id -> OptionVisualState.CORRECT
                verified != null && option.id == verified.selectedOptionId -> OptionVisualState.WRONG
                verified != null -> OptionVisualState.DIMMED
                option.id == selectedOptionId -> OptionVisualState.SELECTED
                else -> OptionVisualState.IDLE
            }
            OptionCard(
                label = ('A' + i).toString(),
                text = option.text,
                imageUrl = option.imageUrl,
                number = i + 1,
                state = visual,
                enabled = verified == null,
                onClick = { onSelect(option.id) },
            )
        }
    }
}

@Composable
private fun ExerciseLabel(text: String) {
    Text(
        text.uppercase(),
        color = EmeraldDark,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(EmeraldSoft)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    )
}

@Composable
private fun ExerciseTitle(text: String) {
    Text(text, color = Ink, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp)
}
