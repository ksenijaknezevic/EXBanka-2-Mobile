package rs.raf.exbanka.mobile.domain.repository

import kotlinx.coroutines.flow.Flow
import rs.raf.exbanka.mobile.util.NetworkResult

interface AuthRepository {
    /** Login with email and password. Returns access + refresh tokens on success. */
    suspend fun login(email: String, password: String): NetworkResult<Unit>

    /** Observe whether a valid access token is currently stored. */
    fun isLoggedIn(): Flow<Boolean>

    /** Clear all stored tokens (logout). */
    suspend fun logout()
}
