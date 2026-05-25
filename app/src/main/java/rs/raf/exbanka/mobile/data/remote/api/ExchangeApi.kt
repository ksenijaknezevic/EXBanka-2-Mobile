package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rs.raf.exbanka.mobile.data.remote.dto.ExchangeConvertResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.ExchangeExecuteRequestDto
import rs.raf.exbanka.mobile.data.remote.dto.ExchangeExecuteResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.ExchangeRateHistoryResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.ExchangeRatesResponseDto

/** Retrofit interfejs za menjačnicu i kursnu listu (bank-service). */
interface ExchangeApi {

    /** GET /bank/exchange-rates — trenutna kursna lista. */
    @GET("bank/exchange-rates")
    suspend fun getRates(): Response<ExchangeRatesResponseDto>

    /** GET /bank/exchange-rates?from=X&to=Y&amount=Z — informativna konverzija. */
    @GET("bank/exchange-rates")
    suspend fun convert(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): Response<ExchangeConvertResponseDto>

    /** POST /bank/exchange-rates/execute — izvršenje konverzije između dva računa. */
    @POST("bank/exchange-rates/execute")
    suspend fun execute(
        @Body request: ExchangeExecuteRequestDto
    ): Response<ExchangeExecuteResponseDto>

    /** GET /bank/exchange-rates/history?oznaka=&days= — istorija kursne liste. */
    @GET("bank/exchange-rates/history")
    suspend fun getHistory(
        @Query("oznaka") oznaka: String? = null,
        @Query("days") days: Int? = 30,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<ExchangeRateHistoryResponseDto>
}
