package com.example.kimsia.data.repository

import com.example.kimsia.domain.repository.AuthRepository
import com.example.kimsia.domain.util.suspendRunCatching
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AuthRepositoryImpl(
    private val supabase: SupabaseClient?,
    scope: CoroutineScope,
) : AuthRepository {

    private val _userId = MutableStateFlow(AuthRepository.LOCAL_USER_ID)
    override val userId: StateFlow<String> = _userId.asStateFlow()

    init {
        supabase?.let { client ->
            scope.launch {
                client.auth.sessionStatus.collect { status ->
                    _userId.value = (status as? SessionStatus.Authenticated)?.session?.user?.id
                        ?: AuthRepository.LOCAL_USER_ID
                }
            }
        }
    }

    private fun client(): SupabaseClient =
        supabase ?: throw IllegalStateException("Supabase no está configurado (local.properties)")

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> = suspendRunCatching {
        client().auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, username: String): Result<Unit> =
        suspendRunCatching {
            client().auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject { put("username", JsonPrimitive(username)) }
            }
            Unit
        }

    override suspend fun signOut(): Result<Unit> = suspendRunCatching { client().auth.signOut() }
}
