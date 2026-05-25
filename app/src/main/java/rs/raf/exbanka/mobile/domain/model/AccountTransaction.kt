package rs.raf.exbanka.mobile.domain.model

/**
 * Domain model za jednu istorijsku transakciju računa
 * (vidi GET /bank/client/accounts/{id}/transactions).
 * Valuta nije sastavni deo proto poruke pa je popunjavamo na osnovu računa
 * kome transakcija pripada (pozivalac prosleđuje currency u toDomain).
 */
data class AccountTransaction(
    val id: String,
    val tipTransakcije: String,
    val iznos: Double,
    val valuta: String,
    val opis: String,
    val vremeIzvrsavanja: String,
    val status: String
)
