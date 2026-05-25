package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rs.raf.exbanka.mobile.data.remote.dto.FundPerformancePointDto
import rs.raf.exbanka.mobile.data.remote.dto.FundsResponseDto

/** Retrofit interfejs za fondove (bank-service). */
interface FundApi {

    /** GET /bank/funds — lista fondova: client positions (klijent) ili managed funds (supervizor). */
    @GET("bank/funds")
    suspend fun getFunds(): Response<FundsResponseDto>

    /**
     * GET /bank/funds/{id}/performance — istorijska vrednost fonda (lista tačaka).
     * Query param: period=monthly|quarterly|yearly (default: monthly)
     * Odgovor je čist niz: [{ period, value }, ...]
     */
    @GET("bank/funds/{id}/performance")
    suspend fun getPerformance(
        @Path("id") fundId: String,
        @Query("period") period: String? = null
    ): Response<List<FundPerformancePointDto>>
}
