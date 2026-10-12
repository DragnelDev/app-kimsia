package com.example.kimsia.domain.repository

import kotlin.time.Duration

interface AudioRepository {
    /** Convierte una ruta del bucket (`gn/maitei.mp3`) o una URL absoluta en una URL reproducible. */
    fun resolveUrl(pathOrUrl: String?): Result<String>

    /** Alternativa para buckets privados. */
    suspend fun createSignedUrl(path: String, expiresIn: Duration): Result<String>
}
