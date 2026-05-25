package rs.raf.exbanka.mobile.domain.model

/** Domain model za klijentski račun (vidi GET /bank/client/accounts). */
data class Account(
    val id: String,
    val brojRacuna: String,
    val nazivRacuna: String,
    val kategorijaRacuna: String,
    val vrstaRacuna: String,
    val valuta: String,
    val stanjeRacuna: Double,
    val rezervisanaSredstva: Double,
    val raspolozivoStanje: Double
)
