package rs.raf.exbanka.mobile.domain.model

data class ExchangeRate(
    val oznaka: String,
    val naziv: String,
    val kupovni: Double,
    val srednji: Double,
    val prodajni: Double
)

data class ExchangeConversion(
    val result: Double,
    val bruto: Double,
    val provizija: Double,
    val viaRsd: Boolean,
    val rateNote: String
)

data class ExchangeRateHistoryPoint(
    val date: String,    // YYYY-MM-DD
    val oznaka: String,
    val naziv: String,
    val kupovni: Double,
    val srednji: Double,
    val prodajni: Double
)

data class ExchangeExecution(
    val referenceId: String,
    val originalAmount: Double,
    val grossAmount: Double,
    val provizija: Double,
    val netAmount: Double,
    val fromOznaka: String,
    val toOznaka: String,
    val viaRsd: Boolean,
    val rateNote: String
)
