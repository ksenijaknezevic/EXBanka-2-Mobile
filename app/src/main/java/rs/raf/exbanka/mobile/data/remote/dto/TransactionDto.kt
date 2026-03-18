package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.Transaction
import rs.raf.exbanka.mobile.domain.model.TransactionStatus
import java.time.Instant

/**
 * DTO koji odgovara PendingActionItem proto poruci iz bank-service.
 * gRPC-Gateway vraca int64 kao JSON string, pa je id Long.
 *
 * Primer odgovora GET /bank/transactions/pending:
 * {
 *   "transactions": [{
 *     "id": "42",
 *     "actionType": "PROMENA_LIMITA",
 *     "recipientName": "Promena limita",
 *     "recipientAccount": "6660001110000012",
 *     "amount": 5000.0,
 *     "currency": "RSD",
 *     "purpose": "Dnevni: 5000.00 RSD / Mesečni: 50000.00 RSD",
 *     "createdAt": "2024-01-15T10:30:00Z",
 *     "status": "PENDING"
 *   }]
 * }
 */
data class TransactionDto(
    @SerializedName("id")               val id: String,
    @SerializedName("recipientName")    val recipientName: String,
    @SerializedName("recipientAccount") val recipientAccount: String,
    @SerializedName("amount")           val amount: Double,
    @SerializedName("currency")         val currency: String,
    @SerializedName("purpose")          val purpose: String,
    @SerializedName("createdAt")        val createdAt: String,
    @SerializedName("status")           val status: String
) {
    fun toDomain(): Transaction = Transaction(
        id = id,
        recipientName = recipientName,
        recipientAccount = recipientAccount,
        amount = amount,
        currency = currency,
        purpose = purpose,
        createdAt = runCatching { Instant.parse(createdAt) }.getOrDefault(Instant.now()),
        status = TransactionStatus.PENDING
    )
}

data class PendingTransactionsResponseDto(
    @SerializedName("transactions") val transactions: List<TransactionDto>?
)

data class SingleTransactionResponseDto(
    @SerializedName("transaction") val transaction: TransactionDto?
)
