package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.Account

// GET /bank/client/accounts — gRPC-Gateway zna da serijalizuje
// numeričke vrednosti kao stringove, pa koristimo string + parser.
data class AccountDto(
    @SerializedName("id")                  val id: String,
    @SerializedName("brojRacuna")          val brojRacuna: String,
    @SerializedName("nazivRacuna")         val nazivRacuna: String?,
    @SerializedName("kategorijaRacuna")    val kategorijaRacuna: String?,
    @SerializedName("vrstaRacuna")         val vrstaRacuna: String?,
    @SerializedName("valutaOznaka")        val valutaOznaka: String,
    @SerializedName("stanjeRacuna")        val stanjeRacuna: String?,
    @SerializedName("rezervisanaSredstva") val rezervisanaSredstva: String?,
    @SerializedName("raspolozivoStanje")   val raspolozivoStanje: String?
) {
    fun toDomain(): Account = Account(
        id = id,
        brojRacuna = brojRacuna,
        nazivRacuna = nazivRacuna.orEmpty(),
        kategorijaRacuna = kategorijaRacuna.orEmpty(),
        vrstaRacuna = vrstaRacuna.orEmpty(),
        valuta = valutaOznaka,
        stanjeRacuna = parseAmount(stanjeRacuna),
        rezervisanaSredstva = parseAmount(rezervisanaSredstva),
        raspolozivoStanje = parseAmount(raspolozivoStanje)
    )

    private fun parseAmount(v: String?): Double = v?.toDoubleOrNull() ?: 0.0
}

data class AccountsResponseDto(
    @SerializedName("accounts") val accounts: List<AccountDto>?
)
