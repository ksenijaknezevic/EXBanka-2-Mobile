package rs.raf.exbanka.mobile.domain.model

/** Domain model za karticu klijenta (vidi GET /bank/cards/my). */
data class Card(
    val id: String,
    val brojKartice: String,
    val tipKartice: String,
    val vrstaKartice: String,
    val datumIsteka: String,
    val status: String,
    val racunId: String,
    val nazivRacuna: String,
    val brojRacuna: String
) {
    val isAktivna: Boolean get() = status.equals("AKTIVNA", ignoreCase = true)

    /** Maskira broj kartice u "•••• •••• •••• 1234" oblik (čuva poslednje 4 cifre). */
    val maskiraniBroj: String
        get() {
            val digits = brojKartice.filter { it.isDigit() }
            if (digits.length < 4) return brojKartice
            val last4 = digits.takeLast(4)
            return "•••• •••• •••• $last4"
        }
}
