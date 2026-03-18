package rs.raf.exbanka.mobile.data.remote.interceptor

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import rs.raf.exbanka.mobile.data.local.SessionDataStore
import javax.inject.Inject

/**
 * OkHttp interceptor that appends the stored Bearer access token to every request.
 * Endpoints that don't require auth (e.g. /login) simply receive the header;
 * they ignore it, so there is no harm in always attaching it.
 */
class AuthInterceptor @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            sessionDataStore.accessToken.firstOrNull()
        }

        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
