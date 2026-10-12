package com.example.kimsia.audio

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class PlaybackStatus { IDLE, LOADING, PLAYING, PAUSED, ENDED, ERROR }

data class AudioPlaybackState(
    val url: String? = null,
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val errorMessage: String? = null,
)

/**
 * Reproductor de pronunciaciones (.mp3 en Supabase Storage) sobre Media3 ExoPlayer.
 * Debe usarse desde el hilo principal. Obtén una instancia con [rememberAudioPlayerManager]
 * para que se libere automáticamente con el ciclo de vida de Compose.
 */
class AudioPlayerManager(context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                .build(),
            /* handleAudioFocus = */ true,
        )
        .setHandleAudioBecomingNoisy(true)
        .build()

    private val _state = MutableStateFlow(AudioPlaybackState())
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> setStatus(PlaybackStatus.LOADING)
                Player.STATE_READY -> setStatus(
                    if (player.isPlaying) PlaybackStatus.PLAYING
                    else if (player.playWhenReady) PlaybackStatus.LOADING
                    else PlaybackStatus.PAUSED,
                )
                Player.STATE_ENDED -> setStatus(PlaybackStatus.ENDED)
                Player.STATE_IDLE -> Unit // tras un error ya publicamos ERROR
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) setStatus(PlaybackStatus.PLAYING)
            else if (player.playbackState == Player.STATE_READY) setStatus(PlaybackStatus.PAUSED)
        }

        override fun onPlayerError(error: PlaybackException) {
            _state.update {
                it.copy(status = PlaybackStatus.ERROR, errorMessage = "No se pudo reproducir el audio")
            }
        }
    }

    init {
        player.addListener(listener)
    }

    /** Reproduce [url] desde el inicio (o reanuda si es el mismo audio en pausa). */
    fun play(url: String) {
        val current = _state.value
        if (current.url == url) {
            when (current.status) {
                PlaybackStatus.PAUSED -> { player.play(); return }
                PlaybackStatus.PLAYING, PlaybackStatus.LOADING -> return
                PlaybackStatus.ENDED -> { player.seekTo(0); player.play(); return }
                else -> Unit // IDLE / ERROR → volver a cargar
            }
        }
        _state.value = AudioPlaybackState(url = url, status = PlaybackStatus.LOADING)
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
    }

    /** Un solo botón: reproduce / pausa / repite según el estado actual. */
    fun toggle(url: String) {
        val current = _state.value
        if (current.url == url && current.status == PlaybackStatus.PLAYING) pause() else play(url)
    }

    fun pause() {
        if (player.isPlaying) player.pause()
    }

    fun stop() {
        player.stop()
        player.clearMediaItems()
        _state.value = AudioPlaybackState()
    }

    fun release() {
        player.removeListener(listener)
        player.release()
        _state.value = AudioPlaybackState()
    }

    private fun setStatus(status: PlaybackStatus) {
        _state.update { it.copy(status = status, errorMessage = if (status == PlaybackStatus.ERROR) it.errorMessage else null) }
    }
}

/** Crea el manager, lo pausa al pasar a segundo plano y lo libera al salir de la composición. */
@Composable
fun rememberAudioPlayerManager(): AudioPlayerManager {
    val context = LocalContext.current
    val manager = remember { AudioPlayerManager(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, manager) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) manager.pause()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            manager.release()
        }
    }
    return manager
}
