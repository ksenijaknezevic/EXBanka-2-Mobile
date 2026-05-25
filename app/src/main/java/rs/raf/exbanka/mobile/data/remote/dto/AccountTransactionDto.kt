package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.AccountTransaction

// GET /bank/client/accounts/{racunId}/transactions — projekcija proto.banka.Transakcija.
data class AccountTransactionDto(
    @SerializedName("id")                val id: String,
    @SerializedName("racunId")           val racunId: String?,
    @SerializedName("tipTransakcije")    val tipTransakcije: String,
    @SerializedName("iznos")             val iznos: String?,
    @SerializedName("opis")              val opis: String?,
    @SerializedName("vremeIzvrsavanja") val vremeIzvrsavanja: String?,
    @SerializedName("status")            val status: String?
) {
    fun toDomain(currency: String): AccountTransaction = AccountTransaction(
        id = id,
        tipTransakcije = tipTransakcije,
        iznos = iznos?.toDoubleOrNull() ?: 0.0,
        valuta = currency,
        opis = opis.orEmpty(),
        vremeIzvrsavanja = vremeIzvrsavanja.orEmpty(),
        status = status.orEmpty()
    )
}

data class AccountTransactionsResponseDto(
    @SerializedName("transactions") val transactions: List<AccountTransactionDto>?
)
