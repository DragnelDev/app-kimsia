package com.example.kimsia.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kimsia.R
import com.example.kimsia.audio.AudioPlaybackState
import com.example.kimsia.audio.PlaybackStatus
import com.example.kimsia.ui.theme.Emerald
import com.example.kimsia.ui.theme.EmeraldDark
import com.example.kimsia.ui.theme.EmeraldSoft
import com.example.kimsia.ui.theme.InkSoft
import com.example.kimsia.ui.theme.Terracotta

/** Estado efectivo para ESTE audio: si el reproductor está con otro archivo, este botón está en reposo. */
private fun effectiveStatus(audioUrl: String?, playback: AudioPlaybackState): PlaybackStatus =
    if (audioUrl != null && playback.url == audioUrl) playback.status else PlaybackStatus.IDLE

private fun statusText(audioUrl: String?, status: PlaybackStatus): String = when {
    audioUrl == null -> "Audio no disponible"
    status == PlaybackStatus.LOADING -> "Cargando…"
    status == PlaybackStatus.PLAYING -> "Reproduciendo"
    status == PlaybackStatus.PAUSED -> "En pausa · toca para seguir"
    status == PlaybackStatus.ERROR -> "No se pudo reproducir · toca para reintentar"
    status == PlaybackStatus.ENDED -> "Toca para repetir"
    else -> "Toca para escuchar"
}

/** Tarjeta compacta: capibara + título + estado (se usa en preguntas de opción múltiple con audio). */
@Composable
fun AudioPlayCard(
    audioUrl: String?,
    playback: AudioPlaybackState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Escucha la pronunciación nativa",
) {
    val status = effectiveStatus(audioUrl, playback)
    Surface(
        onClick = onClick,
        enabled = audioUrl != null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = EmeraldSoft.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Emerald.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp).alpha(if (audioUrl == null) 0.5f else 1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CapybaraAudioButton(status = status, size = 56.dp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = EmeraldDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    statusText(audioUrl, status),
                    color = if (status == PlaybackStatus.ERROR) Terracotta else EmeraldDark,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

/** Versión grande y centrada para ejercicios "escucha y selecciona". */
@Composable
fun AudioPlayHero(
    audioUrl: String?,
    playback: AudioPlaybackState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val status = effectiveStatus(audioUrl, playback)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = audioUrl != null, role = Role.Button, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.alpha(if (audioUrl == null) 0.5f else 1f)) {
            CapybaraAudioButton(status = status, size = 124.dp)
        }
        Spacer(Modifier.size(10.dp))
        Text(
            statusText(audioUrl, status),
            color = if (status == PlaybackStatus.ERROR) Terracotta else InkSoft,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}

/** El capibara es el botón: late mientras suena y muestra una insignia con el estado de reproducción. */
@Composable
private fun CapybaraAudioButton(status: PlaybackStatus, size: Dp) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale",
    )
    val badge = size * 0.42f

    Box(contentAlignment = Alignment.BottomEnd) {
        Image(
            painter = painterResource(R.drawable.kimsia_logo),
            contentDescription = "Capibara: reproducir pronunciación",
            modifier = Modifier
                .size(size)
                .scale(if (status == PlaybackStatus.PLAYING) pulse else 1f)
                .clip(CircleShape)
                .border(3.dp, if (status == PlaybackStatus.PLAYING) Emerald else Color.White, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(badge.coerceAtLeast(24.dp))
                .clip(CircleShape)
                .background(if (status == PlaybackStatus.ERROR) Terracotta else EmeraldDark)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (status == PlaybackStatus.LOADING) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
            } else {
                Icon(badgeIcon(status), contentDescription = null, tint = Color.White, modifier = Modifier.size(badge.coerceAtLeast(24.dp) * 0.6f))
            }
        }
    }
}

private fun badgeIcon(status: PlaybackStatus): ImageVector = when (status) {
    PlaybackStatus.PLAYING -> Icons.Filled.Pause
    PlaybackStatus.PAUSED -> Icons.Filled.PlayArrow
    PlaybackStatus.ERROR, PlaybackStatus.ENDED -> Icons.Filled.Refresh
    else -> Icons.AutoMirrored.Filled.VolumeUp
}
