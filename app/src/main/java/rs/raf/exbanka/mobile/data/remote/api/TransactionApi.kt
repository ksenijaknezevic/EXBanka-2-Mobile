package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import rs.raf.exbanka.mobile.data.remote.dto.ApproveResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.PendingTransactionsResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.SingleTransactionResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.VerifyActionRequestDto

/**
 * Konekcija na bank-service (BANK_BASE_URL).
 * Sve rute imaju /bank/ prefiks koji gRPC-Gateway zahteva.
 */
interface TransactionApi {

    /**
     * GET /bank/transactions/pending
     * Vraća sve pending akcije koje čekaju mobilnu verifikaciju.
     * Authorization: Bearer <access_token> (dodaje AuthInterceptor)
     */
    @GET("bank/transactions/pending")
    suspend fun getPendingTransactions(): Response<PendingTransactionsResponseDto>

    /**
     * GET /bank/transactions/{id}
     * Vraća detalje jedne pending akcije.
     */
    @GET("bank/transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Response<SingleTransactionResponseDto>

    /**
     * POST /bank/transactions/{id}/approve
     * Odobrava pending akciju i vraća 6-cifreni verifikacioni kod.
     * Korisnik mora uneti kod na laptopu u roku od 5 minuta.
     * Nakon 3 pogrešna unosa akcija se otkazuje.
     */
    @POST("bank/transactions/{id}/approve")
    suspend fun approveTransaction(@Path("id") id: String): Response<ApproveResponseDto>

    /**
     * POST /bank/client/pending-actions/{id}/verify
     * Verifikuje 6-cifreni kod i izvršava akciju. Koristi se za Quick Approve
     * gde mobilna aplikacija sama unosi kod koji je dobila iz approve odgovora,
     * pa se akcija završi bez kucanja na laptopu.
     */
    @POST("bank/client/pending-actions/{id}/verify")
    suspend fun verifyAction(
        @Path("id") id: String,
        @Body body: VerifyActionRequestDto
    ): Response<Unit>
}
