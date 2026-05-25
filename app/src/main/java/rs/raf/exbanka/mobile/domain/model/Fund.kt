package rs.raf.exbanka.mobile.domain.model

/** Pozicija klijenta u fondu (sa udelom i profitom). */
data class ClientFund(
    val id: String,
    val name: String,
    val description: String,
    val fundValueRsd: Double,
    val sharePercent: Double,
    val shareRsd: Double,
    val profit: Double,
    val investedRsd: Double
)

/** Fond koji menadžer/supervizor administrira. */
data class ManagedFund(
    val id: String,
    val name: String,
    val description: String,
    val fundValueRsd: Double,
    val liquidityRsd: Double
)

data class FundsOverview(
    val clientFunds: List<ClientFund>,
    val managedFunds: List<ManagedFund>
)

/** Jedna tačka istorijske performanse fonda. */
data class FundPerformancePoint(
    val period: String,
    val value: Double
)
