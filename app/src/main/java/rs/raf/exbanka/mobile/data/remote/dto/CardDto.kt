package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.Card

/**
 * Odgovara karticaDTO iz bank-service (GET /bank/cards/my).
 * gRPC-Gateway/REST handler vraća id-ove kao stringove.
 */
data class CardDto(
    @SerializedName("id")           val id: String,
    @SerializedName("brojKartice")  val brojKartice: String,
    @SerializedName("tipKartice")   val tipKartice: String,
    @SerializedName("vrstaKartice") val vrstaKartice: String,
    @SerializedName("datumIsteka")  val datumIsteka: String,
    @SerializedName("status")       val status: String,
    @SerializedName("racunId")      val racunId: String,
    @SerializedName("nazivRacuna")  val nazivRacuna: String,
    @SerializedName("brojRacuna")   val brojRacuna: String
) {
    fun toDomain(): Card = Card(
        id = id,
        brojKartice = brojKartice,
        tipKartice = tipKartice,
        vrstaKartice = vrstaKartice,
        datumIsteka = datumIsteka,
        status = status,
        racunId = racunId,
        nazivRacuna = nazivRacuna,
        brojRacuna = brojRacuna
    )
}

data class CardsResponseDto(
    @SerializedName("kartice") val kartice: List<CardDto>?
)
