package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import rs.raf.exbanka.mobile.data.remote.dto.OtcHistoryResponseDto

/** Retrofit interfejs za OTC pregovaranje (bank-service). */
interface OtcApi {

    /**
     * GET /api/otc/history — istorija završenih pregovora.
     * Filteri (svi opcioni):
     *   - status: ACCEPTED | REJECTED | DEACTIVATED
     *   - from / to: YYYY-MM-DD
     *   - counterpartId: ID druge strane (buyer ili seller)
     */
    @GET("api/otc/history")
    suspend fun getHistory(
        @Query("status") status: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("counterpartId") counterpartId: Long? = null
    ): Response<OtcHistoryResponseDto>
}
