package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.ExchangeRate
import rs.raf.exbanka.mobile.domain.model.ExchangeConversion
import rs.raf.exbanka.mobile.domain.model.ExchangeExecution

// GET /bank/exchange-rates → kursna lista
data class ExchangeRateItemDto(
    @SerializedName("oznaka")   val oznaka: String,
    @SerializedName("naziv")    val naziv: String?,
    @SerializedName("kupovni")  val kupovni: Double?,
    @SerializedName("srednji")  val srednji: Double?,
    @SerializedName("prodajni") val prodajni: Double?
) {
    fun toDomain(): ExchangeRate = ExchangeRate(
        oznaka = oznaka,
        naziv = naziv.orEmpty(),
        kupovni = kupovni ?: 0.0,
        srednji = srednji ?: 0.0,
        prodajni = prodajni ?: 0.0
    )
}

data class ExchangeRatesResponseDto(
    @SerializedName("rates") val rates: List<ExchangeRateItemDto>?
)

// GET /bank/exchange-rates?from=X&to=Y&amount=Z → informativna konverzija
data class ExchangeConvertResponseDto(
    @SerializedName("result")    val result: Double?,
    @SerializedName("bruto")     val bruto: Double?,
    @SerializedName("provizija") val provizija: Double?,
    @SerializedName("viaRsd")    val viaRsd: Boolean?,
    @SerializedName("rateNote")  val rateNote: String?
) {
    fun toDomain(): ExchangeConversion = ExchangeConversion(
        result = result ?: 0.0,
        bruto = bruto ?: 0.0,
        provizija = provizija ?: 0.0,
        viaRsd = viaRsd ?: false,
        rateNote = rateNote.orEmpty()
    )
}

// POST /bank/exchange-rates/execute
data class ExchangeExecuteRequestDto(
    @SerializedName("sourceAccountId") val sourceAccountId: Long,
    @SerializedName("targetAccountId") val targetAccountId: Long,
    @SerializedName("fromOznaka")      val fromOznaka: String,
    @SerializedName("toOznaka")        val toOznaka: String,
    @SerializedName("amount")          val amount: Double
)

// GET /bank/exchange-rates/history?oznaka=&from=&to=&days=
data class ExchangeRateHistoryPointDto(
    @SerializedName("date")     val date: String?,
    @SerializedName("oznaka")   val oznaka: String?,
    @SerializedName("naziv")    val naziv: String?,
    @SerializedName("kupovni")  val kupovni: Double?,
    @SerializedName("srednji")  val srednji: Double?,
    @SerializedName("prodajni") val prodajni: Double?
) {
    fun toDomain(): rs.raf.exbanka.mobile.domain.model.ExchangeRateHistoryPoint =
        rs.raf.exbanka.mobile.domain.model.ExchangeRateHistoryPoint(
            date = date.orEmpty(),
            oznaka = oznaka.orEmpty(),
            naziv = naziv.orEmpty(),
            kupovni = kupovni ?: 0.0,
            srednji = srednji ?: 0.0,
            prodajni = prodajni ?: 0.0
        )
}

data class ExchangeRateHistoryResponseDto(
    @SerializedName("from")    val from: String?,
    @SerializedName("to")      val to: String?,
    @SerializedName("oznaka")  val oznaka: String?,
    @SerializedName("history") val history: List<ExchangeRateHistoryPointDto>?
)

data class ExchangeExecuteResponseDto(
    @SerializedName("referenceId")     val referenceId: String?,
    @SerializedName("sourceAccountId") val sourceAccountId: Long?,
    @SerializedName("targetAccountId") val targetAccountId: Long?,
    @SerializedName("fromOznaka")      val fromOznaka: String?,
    @SerializedName("toOznaka")        val toOznaka: String?,
    @SerializedName("originalAmount")  val originalAmount: Double?,
    @SerializedName("grossAmount")     val grossAmount: Double?,
    @SerializedName("provizija")       val provizija: Double?,
    @SerializedName("netAmount")       val netAmount: Double?,
    @SerializedName("viaRsd")          val viaRsd: Boolean?,
    @SerializedName("rateNote")        val rateNote: String?
) {
    fun toDomain(): ExchangeExecution = ExchangeExecution(
        referenceId = referenceId.orEmpty(),
        originalAmount = originalAmount ?: 0.0,
        grossAmount = grossAmount ?: 0.0,
        provizija = provizija ?: 0.0,
        netAmount = netAmount ?: 0.0,
        fromOznaka = fromOznaka.orEmpty(),
        toOznaka = toOznaka.orEmpty(),
        viaRsd = viaRsd ?: false,
        rateNote = rateNote.orEmpty()
    )
}
