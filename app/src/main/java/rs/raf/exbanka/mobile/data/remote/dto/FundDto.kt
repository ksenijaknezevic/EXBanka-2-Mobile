package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.ClientFund
import rs.raf.exbanka.mobile.domain.model.FundPerformancePoint
import rs.raf.exbanka.mobile.domain.model.FundsOverview
import rs.raf.exbanka.mobile.domain.model.ManagedFund

data class ClientFundDto(
    @SerializedName("id")           val id: String?,
    @SerializedName("name")         val name: String?,
    @SerializedName("description")  val description: String?,
    @SerializedName("fundValueRsd") val fundValueRsd: Double?,
    @SerializedName("sharePercent") val sharePercent: Double?,
    @SerializedName("shareRsd")     val shareRsd: Double?,
    @SerializedName("profit")       val profit: Double?,
    @SerializedName("investedRsd")  val investedRsd: Double?
) {
    fun toDomain(): ClientFund = ClientFund(
        id = id.orEmpty(),
        name = name.orEmpty(),
        description = description.orEmpty(),
        fundValueRsd = fundValueRsd ?: 0.0,
        sharePercent = sharePercent ?: 0.0,
        shareRsd = shareRsd ?: 0.0,
        profit = profit ?: 0.0,
        investedRsd = investedRsd ?: 0.0
    )
}

data class ManagedFundDto(
    @SerializedName("id")           val id: String?,
    @SerializedName("name")         val name: String?,
    @SerializedName("description")  val description: String?,
    @SerializedName("fundValueRsd") val fundValueRsd: Double?,
    @SerializedName("liquidityRsd") val liquidityRsd: Double?
) {
    fun toDomain(): ManagedFund = ManagedFund(
        id = id.orEmpty(),
        name = name.orEmpty(),
        description = description.orEmpty(),
        fundValueRsd = fundValueRsd ?: 0.0,
        liquidityRsd = liquidityRsd ?: 0.0
    )
}

data class FundsResponseDto(
    @SerializedName("clientFunds")  val clientFunds: List<ClientFundDto>?,
    @SerializedName("managedFunds") val managedFunds: List<ManagedFundDto>?
) {
    fun toDomain(): FundsOverview = FundsOverview(
        clientFunds = clientFunds?.map { it.toDomain() } ?: emptyList(),
        managedFunds = managedFunds?.map { it.toDomain() } ?: emptyList()
    )
}

data class FundPerformancePointDto(
    @SerializedName("period") val period: String?,
    @SerializedName("value")  val value: Double?
) {
    fun toDomain(): FundPerformancePoint = FundPerformancePoint(
        period = period.orEmpty(),
        value = value ?: 0.0
    )
}
