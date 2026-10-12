package com.example.kimsia.ui.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kimsia.ui.theme.Emerald
import com.example.kimsia.ui.theme.EmeraldDark
import com.example.kimsia.ui.theme.EmeraldSoft
import com.example.kimsia.ui.theme.Terracotta

private val WrongContainer = Color(0xFFFFEDD5)
private val CorrectTitles = listOf("¡Excelente!", "¡Muy bien!", "¡Correcto!", "¡Así se habla!")

/**
 * Hoja de retroalimentación anclada abajo (no descartable: se cierra con Continuar / Entendido).
 * Correcto → verde #10B981 · Incorrecto → naranja terracota #EA580C.
 */
@Composable
fun FeedbackBottomSheet(
    visible: Boolean,
    state: LessonUiState.AnswerVerified?,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
    ) {
        val data = state ?: return@AnimatedVisibility
        val ok = data.isCorrect
        val accent = if (ok) Emerald else Terracotta
        val titleColor = if (ok) EmeraldDark else Terracotta

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = if (ok) EmeraldSoft else WrongContainer,
            shadowElevation = 16.dp,
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (ok) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (ok) CorrectTitles[data.index % CorrectTitles.size] else "Respuesta incorrecta",
                        color = titleColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (ok) "«${data.correctOption.text}» es la respuesta correcta."
                    else "Respuesta correcta: «${data.correctOption.text}»",
                    color = titleColor,
                    fontSize = 15.sp,
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.White),
                ) {
                    Text(if (ok) "Continuar" else "Entendido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
