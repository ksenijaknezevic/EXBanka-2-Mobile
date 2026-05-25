package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.PATCH
import retrofit2.http.GET
import retrofit2.http.Path
import rs.raf.exbanka.mobile.data.remote.dto.CardsResponseDto

/**
 * Retrofit interfejs za klijentske kartice (bank-service).
 * Authorization Bearer token dodaje AuthInterceptor.
 */
interface CardApi {

    /** GET /bank/cards/my — sve kartice ulogovanog klijenta. */
    @GET("bank/cards/my")
    suspend fun getMyCards(): Response<CardsResponseDto>

    /**
     * PATCH /bank/cards/{id}/block — blokira aktivnu karticu klijenta.
     * Telo zahteva je prazan JSON objekat (handler ga ne čita).
     */
    @PATCH("bank/cards/{id}/block")
    suspend fun blockCard(@Path("id") id: String): Response<Unit>
}
