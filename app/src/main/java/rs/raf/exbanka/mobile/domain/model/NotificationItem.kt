package rs.raf.exbanka.mobile.domain.model

data class NotificationItem(
    val id: Long,
    val type: String,         // PENDING_APPROVAL | OTC_COUNTER_OFFER | OTC_ACCEPTED | OTC_DECLINED | OTC_CONTRACT_EXPIRING
    val title: String,
    val body: String,
    val dataJson: String,     // raw JSON payload za deep link / dodatne info
    val readAt: String?,      // ISO 8601 ili null
    val createdAt: String     // ISO 8601
)

data class NotificationsList(
    val notifications: List<NotificationItem>,
    val unreadCount: Long
)
