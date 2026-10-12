package com.example.kimsia.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    /** Id del usuario de Supabase, o [LOCAL_USER_ID] si no hay sesión (modo offline/invitado). */
    val userId: StateFlow<String>

    val isLocalUser: Boolean get() = userId.value == LOCAL_USER_ID

    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmail(email: String, password: String, username: String): Result<Unit>
    suspend fun signOut(): Result<Unit>

    companion object {
        const val LOCAL_USER_ID = "local-user"
    }
}
