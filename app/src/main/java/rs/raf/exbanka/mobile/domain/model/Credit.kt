package rs.raf.exbanka.mobile.domain.model

data class Credit(
    val id: String,
    val brojKredita: String,
    val brojRacuna: String,
    val vrstaKredita: String,
    val iznosKredita: Double,
    val periodOtplate: Int,
    val nominalnaKamatnaStopa: Double,
    val efektivnaKamatnaStopa: Double,
    val datumUgovaranja: String,
    val datumIsplate: String,
    val iznosMesecneRate: Double,
    val datumSledeceRate: String,
    val preostaloDugovanje: Double,
    val valuta: String,
    val status: String,
    val tipKamate: String
)

data class CreditInstallment(
    val id: String,
    val kreditId: String,
    val iznosRate: Double,
    val iznosKamatneStope: Double,
    val valuta: String,
    val ocekivaniDatumDospeca: String,
    val praviDatumDospeca: String?,
    val statusPlacanja: String
)

data class CreditDetail(
    val credit: Credit,
    val rate: List<CreditInstallment>
)
