package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import rs.raf.exbanka.mobile.data.remote.dto.LoginRequestDto
import rs.raf.exbanka.mobile.data.remote.dto.LoginResponseDto

interface AuthApi {
    /**
     * POST /login
     * Actual backend endpoint confirmed in the backend source code.
     */
    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>
}
