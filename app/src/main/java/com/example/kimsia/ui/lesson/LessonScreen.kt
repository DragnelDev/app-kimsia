package com.example.kimsia.ui.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kimsia.R
import com.example.kimsia.audio.AudioPlaybackState
import com.example.kimsia.audio.SoundEffect
import com.example.kimsia.audio.rememberAudioPlayerManager
import com.example.kimsia.audio.rememberSoundEffectPlayer
import com.example.kimsia.domain.model.Exercise
import com.example.kimsia.domain.model.ExerciseOption
import com.example.kimsia.domain.model.ExerciseType
import com.example.kimsia.ui.theme.Cream
import com.example.kimsia.ui.theme.Emerald
import com.example.kimsia.ui.theme.EmeraldDark
import com.example.kimsia.ui.theme.EmeraldSoft
import com.example.kimsia.ui.theme.Ink
import com.example.kimsia.ui.theme.InkSoft
import com.example.kimsia.ui.theme.KimsiaTheme
import com.example.kimsia.ui.theme.SunYellow
import com.example.kimsia.ui.theme.SunYellowDark
import com.example.kimsia.ui.theme.SunYellowSoft
import com.example.kimsia.ui.theme.Terracotta
import com.example.kimsia.ui.theme.TrackGray

@Composable
fun LessonScreen(
    lessonId: Long,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LessonViewModel = viewModel(key = "lesson-$lessonId", factory = LessonViewModel.factory(lessonId)),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val audio = rememberAudioPlayerManager()
    val playback by audio.state.collectAsStateWithLifecycle()
    val soundEffects = rememberSoundEffectPlayer()

    // Sonidos de acierto / error
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            soundEffects.play(if (event is LessonEvent.AnswerCorrect) SoundEffect.CORRECT else SoundEffect.WRONG)
        }
    }

    // Al cambiar de ejercicio: detener lo anterior y autoreproducir si es "escucha y selecciona".
    val activeExercise = (state as? LessonUiState.ExerciseActive)?.exercise
    LaunchedEffect(activeExercise?.id) {
        audio.stop()
        if (activeExercise != null && activeExercise.type == ExerciseType.AUDIO_LISTEN) {
            activeExercise.audioUrl?.let { audio.play(it) }
        }
    }

    var showExitDialog by remember { mutableStateOf(false) }
    val inProgress = state is LessonUiState.ExerciseActive || state is LessonUiState.AnswerVerified
    BackHandler(enabled = inProgress) { showExitDialog = true }

    LessonScreenContent(
        state = state,
        playback = playback,
        onClose = { if (inProgress) showExitDialog = true else onExit() },
        onSelect = viewModel::selectOption,
        onCheck = viewModel::checkAnswer,
        onContinue = viewModel::continueLesson,
        onToggleAudio = audio::toggle,
        onRetry = viewModel::load,
        onFinish = onExit,
        modifier = modifier,
    )

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("¿Salir de la lección?") },
            text = { Text("Perderás el avance de esta lección. Los corazones perdidos no se recuperan.") },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; onExit() }) { Text("Salir", color = Terracotta) }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Seguir aprendiendo", color = EmeraldDark) }
            },
        )
    }
}

/** Versión sin estado (previsualizable y testeable). */
@Composable
fun LessonScreenContent(
    state: LessonUiState,
    playback: AudioPlaybackState,
    onClose: () -> Unit,
    onSelect: (Long) -> Unit,
    onCheck: () -> Unit,
    onContinue: () -> Unit,
    onToggleAudio: (String) -> Unit,
    onRetry: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(Cream)) {
        when (state) {
            LessonUiState.Loading -> Column(Modifier.fillMaxSize()) {
                LessonTopBar(progress = 0f, hearts = null, onClose = onClose)
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Emerald)
                }
            }

            is LessonUiState.Error -> MessagePage(
                title = "No pudimos abrir la lección",
                message = state.message,
                primaryLabel = "Reintentar",
                onPrimary = onRetry,
                secondaryLabel = "Volver",
                onSecondary = onClose,
            )

            is LessonUiState.ExerciseActive, is LessonUiState.AnswerVerified -> ExercisePage(
                state = state,
                playback = playback,
                onClose = onClose,
                onSelect = onSelect,
                onCheck = onCheck,
                onContinue = onContinue,
                onToggleAudio = onToggleAudio,
            )

            is LessonUiState.LessonCompleted -> CompletedPage(state, onFinish)

            LessonUiState.OutOfHearts -> MessagePage(
                title = "Te quedaste sin corazones",
                message = "Vuelve más tarde o repasa lecciones anteriores para seguir practicando.",
                primaryLabel = "Volver a la ruta",
                onPrimary = onFinish,
            )
        }
    }
}

@Composable
private fun ExercisePage(
    state: LessonUiState,
    playback: AudioPlaybackState,
    onClose: () -> Unit,
    onSelect: (Long) -> Unit,
    onCheck: () -> Unit,
    onContinue: () -> Unit,
    onToggleAudio: (String) -> Unit,
) {
    val active = state as? LessonUiState.ExerciseActive
    val verified = state as? LessonUiState.AnswerVerified
    val exercise = active?.exercise ?: verified?.exercise ?: return
    val selectedId = active?.selectedOptionId ?: verified?.selectedOptionId

    // Conserva el último resultado para que la hoja pueda animar su salida.
    var lastVerified by remember { mutableStateOf<LessonUiState.AnswerVerified?>(null) }
    LaunchedEffect(verified) { if (verified != null) lastVerified = verified }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            LessonTopBar(
                progress = active?.progress ?: verified?.progress ?: 0f,
                hearts = active?.hearts ?: verified?.hearts,
                onClose = onClose,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                when (exercise.type) {
                    ExerciseType.AUDIO_LISTEN -> ListenAndSelectExercise(exercise, selectedId, verified, playback, onToggleAudio, onSelect)
                    else -> MultipleChoiceExercise(exercise, selectedId, verified, playback, onToggleAudio, onSelect)
                }
                Spacer(Modifier.height(24.dp))
            }
            // Botón inferior; queda tapado por la hoja de retroalimentación al comprobar.
            Button(
                onClick = onCheck,
                enabled = active?.selectedOptionId != null,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Terracotta,
                    contentColor = Color.White,
                    disabledContainerColor = TrackGray,
                    disabledContentColor = InkSoft,
                ),
            ) {
                Text("Comprobar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        FeedbackBottomSheet(
            visible = verified != null,
            state = verified ?: lastVerified,
            onContinue = onContinue,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun CompletedPage(state: LessonUiState.LessonCompleted, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.kimsia_logo),
            contentDescription = null,
            modifier = Modifier.size(140.dp).clip(CircleShape).border(4.dp, SunYellow, CircleShape),
        )
        Spacer(Modifier.height(20.dp))
        Text("¡Lección completada!", color = EmeraldDark, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        Text("Tu capibara está orgulloso de ti", color = InkSoft, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("+${state.xpEarned} XP", "EXPERIENCIA", SunYellowSoft, SunYellowDark, Modifier.weight(1f))
            StatCard("${state.accuracyPercent}%", "PRECISIÓN", EmeraldSoft, EmeraldDark, Modifier.weight(1f))
        }
        Spacer(Modifier.height(32.dp))
        PrimaryButton("Continuar", onFinish)
    }
}

@Composable
private fun StatCard(value: String, label: String, container: Color, content: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = content, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = content.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MessagePage(
    title: String,
    message: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.kimsia_logo),
            contentDescription = null,
            modifier = Modifier.size(100.dp).clip(CircleShape),
        )
        Spacer(Modifier.height(16.dp))
        Text(title, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(message, color = InkSoft, fontSize = 15.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))
        PrimaryButton(primaryLabel, onPrimary)
        if (secondaryLabel != null) {
            TextButton(onClick = onSecondary) { Text(secondaryLabel, color = EmeraldDark) }
        }
    }
}

@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = Color.White),
    ) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ---- Previews ---------------------------------------------------------------------------

private val previewExercise = Exercise(
    id = 1, lessonId = 32, type = ExerciseType.MULTIPLE_CHOICE,
    questionText = "¿Cómo se dice \"Buenos días\" en Guaraní?",
    audioUrl = null, orderIndex = 1,
    options = listOf(
        ExerciseOption(1, "Ko'ẽ porã", null, true),
        ExerciseOption(2, "Ka'aru porã", null, false),
        ExerciseOption(3, "Pyhare porã", null, false),
        ExerciseOption(4, "Aguyje", null, false),
    ),
)

@Preview(showBackground = true, widthDp = 380, heightDp = 780)
@Composable
private fun LessonActivePreview() = KimsiaTheme {
    LessonScreenContent(
        state = LessonUiState.ExerciseActive(previewExercise, 0, 4, selectedOptionId = 2, hearts = 3, progress = 0.25f),
        playback = AudioPlaybackState(), onClose = {}, onSelect = {}, onCheck = {}, onContinue = {},
        onToggleAudio = {}, onRetry = {}, onFinish = {},
    )
}

@Preview(showBackground = true, widthDp = 380, heightDp = 780)
@Composable
private fun LessonWrongPreview() = KimsiaTheme {
    LessonScreenContent(
        state = LessonUiState.AnswerVerified(
            previewExercise, 0, 4, selectedOptionId = 2, correctOption = previewExercise.options[0],
            isCorrect = false, hearts = 2, progress = 0.25f,
        ),
        playback = AudioPlaybackState(), onClose = {}, onSelect = {}, onCheck = {}, onContinue = {},
        onToggleAudio = {}, onRetry = {}, onFinish = {},
    )
}
