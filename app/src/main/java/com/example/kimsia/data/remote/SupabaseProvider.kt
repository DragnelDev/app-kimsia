package com.example.kimsia.data.remote

import com.example.kimsia.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseProvider {
    /**
     * Devuelve null si faltan `supabase.url` / `supabase.anonKey` en local.properties;
     * en ese caso la app trabaja 100% offline con Room.
     */
    fun create(): SupabaseClient? {
        val url = BuildConfig.SUPABASE_URL
        val key = BuildConfig.SUPABASE_ANON_KEY
        if (url.isBlank() || key.isBlank()) return null
        return createSupabaseClient(supabaseUrl = url, supabaseKey = key) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
