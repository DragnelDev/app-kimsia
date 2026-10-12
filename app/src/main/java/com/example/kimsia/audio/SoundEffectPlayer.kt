package com.example.kimsia.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

enum class SoundEffect { CORRECT, WRONG }

/** Efectos cortos (acierto / error) con SoundPool: baja latencia y sin competir con el ExoPlayer. */
class SoundEffectPlayer(context: Context) {
    private val pool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val soundIds = HashMap<SoundEffect, Int>()
    private val loaded = HashSet<Int>()

    init {
        pool.setOnLoadCompleteListener { _, sampleId, status -> if (status == 0) loaded += sampleId }
        try {
            val correctId = context.resources.getIdentifier("sfx_correct", "raw", context.packageName)
            if (correctId != 0) {
                soundIds[SoundEffect.CORRECT] = pool.load(context.applicationContext, correctId, 1)
            }
        } catch (_: Exception) {}
        try {
            val wrongId = context.resources.getIdentifier("sfx_wrong", "raw", context.packageName)
            if (wrongId != 0) {
                soundIds[SoundEffect.WRONG] = pool.load(context.applicationContext, wrongId, 1)
            }
        } catch (_: Exception) {}
    }

    fun play(effect: SoundEffect) {
        val id = soundIds[effect] ?: return
        if (id in loaded) pool.play(id, 1f, 1f, 1, 0, 1f)
    }

    fun release() = pool.release()
}

@Composable
fun rememberSoundEffectPlayer(): SoundEffectPlayer {
    val context = LocalContext.current
    val player = remember { SoundEffectPlayer(context) }
    DisposableEffect(player) { onDispose { player.release() } }
    return player
}
