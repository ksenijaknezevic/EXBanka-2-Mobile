package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.Credit
import rs.raf.exbanka.mobile.domain.model.CreditDetail
import rs.raf.exbanka.mobile.domain.model.CreditInstallment

/**
 * gRPC-Gateway emituje camelCase polja, ali web frontend mapira i snake_case kao fallback.
 * Da ostanemo robusni, koristimo isti pristup — gledamo oba polja gde je potrebno.
 */
data class CreditDto(
    @SerializedName(value = "id")                                                                  val id: String?,
    @SerializedName(value = "brojKredita",         alternate = ["broj_kredita"])                   val brojKredita: String?,
    @SerializedName(value = "brojRacuna",          alternate = ["broj_racuna"])                    val brojRacuna: String?,
    @SerializedName(value = "vrstaKredita",        alternate = ["vrsta_kredita"])                  val vrstaKredita: String?,
    @SerializedName(value = "iznosKredita",        alternate = ["iznos_kredita"])                  val iznosKredita: String?,
    @SerializedName(value = "periodOtplate",       alternate = ["period_otplate"])                 val periodOtplate: Int?,
    @SerializedName(value = "nominalnaKamatnaStopa", alternate = ["nominalna_kamatna_stopa"])      val nominalnaKamatnaStopa: String?,
    @SerializedName(value = "efektivnaKamatnaStopa", alternate = ["efektivna_kamatna_stopa"])      val efektivnaKamatnaStopa: String?,
    @SerializedName(value = "datumUgovaranja",     alternate = ["datum_ugovaranja"])               val datumUgovaranja: String?,
    @SerializedName(value = "datumIsplate",        alternate = ["datum_isplate"])                  val datumIsplate: String?,
    @SerializedName(value = "iznosMesecneRate",    alternate = ["iznos_mesecne_rate"])             val iznosMesecneRate: String?,
    @SerializedName(value = "datumSledeceRate",    alternate = ["datum_sledece_rate"])             val datumSledeceRate: String?,
    @SerializedName(value = "preostaloDugovanje",  alternate = ["preostalo_dugovanje"])            val preostaloDugovanje: String?,
    @SerializedName(value = "valuta")                                                              val valuta: String?,
    @SerializedName(value = "status")                                                              val status: String?,
    @SerializedName(value = "tipKamate",           alternate = ["tip_kamate"])                     val tipKamate: String?
) {
    fun toDomain(): Credit = Credit(
        id = id.orEmpty(),
        brojKredita = brojKredita.orEmpty(),
        brojRacuna = brojRacuna.orEmpty(),
        vrstaKredita = vrstaKredita.orEmpty(),
        iznosKredita = iznosKredita?.toDoubleOrNull() ?: 0.0,
        periodOtplate = periodOtplate ?: 0,
        nominalnaKamatnaStopa = nominalnaKamatnaStopa?.toDoubleOrNull() ?: 0.0,
        efektivnaKamatnaStopa = efektivnaKamatnaStopa?.toDoubleOrNull() ?: 0.0,
        datumUgovaranja = datumUgovaranja.orEmpty(),
        datumIsplate = datumIsplate.orEmpty(),
        iznosMesecneRate = iznosMesecneRate?.toDoubleOrNull() ?: 0.0,
        datumSledeceRate = datumSledeceRate.orEmpty(),
        preostaloDugovanje = preostaloDugovanje?.toDoubleOrNull() ?: 0.0,
        valuta = valuta.orEmpty(),
        status = status.orEmpty(),
        tipKamate = tipKamate.orEmpty()
    )
}

data class InstallmentDto(
    @SerializedName(value = "id")                                                                 val id: String?,
    @SerializedName(value = "kreditId",              alternate = ["kredit_id"])                    val kreditId: String?,
    @SerializedName(value = "iznosRate",             alternate = ["iznos_rate"])                   val iznosRate: String?,
    @SerializedName(value = "iznosKamatneStope",     alternate = ["iznos_kamatne_stope"])          val iznosKamatneStope: String?,
    @SerializedName(value = "valuta")                                                              val valuta: String?,
    @SerializedName(value = "ocekivaniDatumDospeca", alternate = ["ocekivani_datum_dospeca"])      val ocekivaniDatumDospeca: String?,
    @SerializedName(value = "praviDatumDospeca",     alternate = ["pravi_datum_dospeca"])          val praviDatumDospeca: String?,
    @SerializedName(value = "statusPlacanja",        alternate = ["status_placanja"])              val statusPlacanja: String?
) {
    fun toDomain(): CreditInstallment = CreditInstallment(
        id = id.orEmpty(),
        kreditId = kreditId.orEmpty(),
        iznosRate = iznosRate?.toDoubleOrNull() ?: 0.0,
        iznosKamatneStope = iznosKamatneStope?.toDoubleOrNull() ?: 0.0,
        valuta = valuta.orEmpty(),
        ocekivaniDatumDospeca = ocekivaniDatumDospeca.orEmpty(),
        praviDatumDospeca = praviDatumDospeca?.takeIf { it.isNotBlank() },
        statusPlacanja = statusPlacanja.orEmpty()
    )
}

data class ClientCreditsResponseDto(
    @SerializedName(value = "credits", alternate = ["krediti"]) val credits: List<CreditDto>?
)

data class CreditDetailsResponseDto(
    @SerializedName(value = "kredit", alternate = ["credit"])               val kredit: CreditDto?,
    @SerializedName(value = "rate",   alternate = ["rata", "installments"]) val rate: List<InstallmentDto>?
) {
    fun toDomain(): CreditDetail? {
        val c = kredit?.toDomain() ?: return null
        val rateList = rate?.map { it.toDomain() } ?: emptyList()
        return CreditDetail(credit = c, rate = rateList)
    }
}
