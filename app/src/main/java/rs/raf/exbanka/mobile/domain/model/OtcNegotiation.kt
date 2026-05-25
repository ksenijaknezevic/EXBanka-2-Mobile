package rs.raf.exbanka.mobile.domain.model

data class OtcNegotiation(
    val offerId: Long,
    val listingId: Long,
    val ticker: String,
    val stockName: String,
    val buyerId: Long,
    val sellerId: Long,
    val finalStatus: String,    // ACCEPTED | REJECTED | DEACTIVATED
    val createdAt: String,
    val lastModified: String,
    val history: List<OtcHistoryEntry>
)

data class OtcHistoryEntry(
    val id: Long,
    val action: String,
    val changedBy: Long,
    val amount: Int?,
    val pricePerStock: Double?,
    val premium: Double?,
    val settlementDate: String?,
    val oldAmount: Int?,
    val oldPricePerStock: Double?,
    val oldPremium: Double?,
    val oldSettlementDate: String?,
    val newStatus: String?,
    val createdAt: String
)
