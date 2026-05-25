package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.OtcHistoryEntry
import rs.raf.exbanka.mobile.domain.model.OtcNegotiation

data class OtcHistoryEntryDto(
    @SerializedName("id")                val id: Long?,
    @SerializedName("action")            val action: String?,
    @SerializedName("changedBy")         val changedBy: Long?,
    @SerializedName("amount")            val amount: Int?,
    @SerializedName("pricePerStock")     val pricePerStock: Double?,
    @SerializedName("premium")           val premium: Double?,
    @SerializedName("settlementDate")    val settlementDate: String?,
    @SerializedName("oldAmount")         val oldAmount: Int?,
    @SerializedName("oldPricePerStock")  val oldPricePerStock: Double?,
    @SerializedName("oldPremium")        val oldPremium: Double?,
    @SerializedName("oldSettlementDate") val oldSettlementDate: String?,
    @SerializedName("newStatus")         val newStatus: String?,
    @SerializedName("createdAt")         val createdAt: String?
) {
    fun toDomain(): OtcHistoryEntry = OtcHistoryEntry(
        id = id ?: 0L,
        action = action.orEmpty(),
        changedBy = changedBy ?: 0L,
        amount = amount,
        pricePerStock = pricePerStock,
        premium = premium,
        settlementDate = settlementDate,
        oldAmount = oldAmount,
        oldPricePerStock = oldPricePerStock,
        oldPremium = oldPremium,
        oldSettlementDate = oldSettlementDate,
        newStatus = newStatus,
        createdAt = createdAt.orEmpty()
    )
}

data class OtcNegotiationDto(
    @SerializedName("offerId")      val offerId: Long?,
    @SerializedName("listingId")    val listingId: Long?,
    @SerializedName("ticker")       val ticker: String?,
    @SerializedName("stockName")    val stockName: String?,
    @SerializedName("buyerId")      val buyerId: Long?,
    @SerializedName("sellerId")     val sellerId: Long?,
    @SerializedName("finalStatus")  val finalStatus: String?,
    @SerializedName("createdAt")    val createdAt: String?,
    @SerializedName("lastModified") val lastModified: String?,
    @SerializedName("history")      val history: List<OtcHistoryEntryDto>?
) {
    fun toDomain(): OtcNegotiation = OtcNegotiation(
        offerId = offerId ?: 0L,
        listingId = listingId ?: 0L,
        ticker = ticker.orEmpty(),
        stockName = stockName.orEmpty(),
        buyerId = buyerId ?: 0L,
        sellerId = sellerId ?: 0L,
        finalStatus = finalStatus.orEmpty(),
        createdAt = createdAt.orEmpty(),
        lastModified = lastModified.orEmpty(),
        history = history?.map { it.toDomain() } ?: emptyList()
    )
}

data class OtcHistoryResponseDto(
    @SerializedName("negotiations") val negotiations: List<OtcNegotiationDto>?
)
