package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import rs.raf.exbanka.mobile.data.remote.dto.ClientCreditsResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.CreditDetailsResponseDto

/** Retrofit interfejs za kredite ulogovanog klijenta (bank-service). */
interface CreditApi {

    /** GET /api/v1/client/credits — svi krediti ulogovanog klijenta. */
    @GET("api/v1/client/credits")
    suspend fun getClientCredits(): Response<ClientCreditsResponseDto>

    /** GET /api/v1/client/credits/{id} — detalji jednog kredita sa ratama. */
    @GET("api/v1/client/credits/{id}")
    suspend fun getCreditDetails(@Path("id") id: String): Response<CreditDetailsResponseDto>
}
