package rs.raf.exbanka.mobile.data.repository

import kotlinx.coroutines.flow.Flow
import rs.raf.exbanka.mobile.data.local.SessionDataStore
import rs.raf.exbanka.mobile.data.remote.api.AuthApi
import rs.raf.exbanka.mobile.data.remote.dto.LoginRequestDto
import rs.raf.exbanka.mobile.domain.repository.AuthRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionDataStore: SessionDataStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): NetworkResult<Unit> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    sessionDataStore.saveTokens(body.accessToken, body.refreshToken)
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error("Empty response from server", response.code())
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Invalid email or password"
                    400 -> "Please check your credentials"
                    else -> "Login failed (${response.code()})"
                }
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error. Check your connection.")
        }
    }

    override fun isLoggedIn(): Flow<Boolean> = sessionDataStore.isLoggedIn

    override suspend fun logout() {
        sessionDataStore.clearTokens()
    }
}
