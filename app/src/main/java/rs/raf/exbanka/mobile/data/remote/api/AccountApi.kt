package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rs.raf.exbanka.mobile.data.remote.dto.AccountTransactionsResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.AccountsResponseDto

/** Retrofit interfejs za klijentske račune i njihove transakcije (bank-service). */
interface AccountApi {

    /** GET /bank/client/accounts — svi računi ulogovanog klijenta. */
    @GET("bank/client/accounts")
    suspend fun getMyAccounts(): Response<AccountsResponseDto>

    /** GET /bank/client/accounts/{racunId}/transactions — istorija transakcija po računu. */
    @GET("bank/client/accounts/{racunId}/transactions")
    suspend fun getAccountTransactions(
        @Path("racunId") racunId: String,
        @Query("sort_by") sortBy: String? = "datum",
        @Query("order") order: String? = "DESC"
    ): Response<AccountTransactionsResponseDto>
}
