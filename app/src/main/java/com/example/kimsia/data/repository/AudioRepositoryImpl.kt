package com.example.kimsia.data.repository

import com.example.kimsia.domain.repository.AudioRepository
import com.example.kimsia.domain.util.suspendRunCatching
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration

class AudioRepositoryImpl(private val supabase: SupabaseClient?) : AudioRepository {

    override fun resolveUrl(pathOrUrl: String?): Result<String> {
        if (pathOrUrl.isNullOrBlank()) return Result.failure(NoSuchElementException("Sin audio"))
        if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) return Result.success(pathOrUrl)
        val client = supabase
            ?: return Result.failure(IllegalStateException("Supabase no está configurado"))
        return runCatching {
            client.storage.from(BUCKET).publicUrl(pathOrUrl.removePrefix("$BUCKET/"))
        }
    }

    override suspend fun createSignedUrl(path: String, expiresIn: Duration): Result<String> = suspendRunCatching {
        val client = supabase ?: throw IllegalStateException("Supabase no está configurado")
        client.storage.from(BUCKET).createSignedUrl(path.removePrefix("$BUCKET/"), expiresIn)
    }

    private companion object {
        const val BUCKET = "audio-lessons"
    }
}
