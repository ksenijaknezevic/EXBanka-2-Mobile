package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.NotificationItem
import rs.raf.exbanka.mobile.domain.model.NotificationsList

// POST /bank/user/devices
data class RegisterDeviceRequestDto(
    @SerializedName("fcm_token") val fcmToken: String,
    @SerializedName("device_id") val deviceId: String? = null,
    @SerializedName("platform")  val platform: String? = "ANDROID"
)

data class RegisterDeviceResponseDto(
    @SerializedName("id")           val id: Long?,
    @SerializedName("device_id")    val deviceId: String?,
    @SerializedName("platform")     val platform: String?,
    @SerializedName("last_seen_at") val lastSeenAt: String?
)

// DELETE /bank/user/devices
data class UnregisterDeviceRequestDto(
    @SerializedName("fcm_token") val fcmToken: String
)

// GET /bank/user/notifications
data class NotificationItemDto(
    @SerializedName("id")         val id: Long?,
    @SerializedName("type")       val type: String?,
    @SerializedName("title")      val title: String?,
    @SerializedName("body")       val body: String?,
    @SerializedName("data")       val data: String?,
    @SerializedName("read_at")    val readAt: String?,
    @SerializedName("created_at") val createdAt: String?
) {
    fun toDomain(): NotificationItem = NotificationItem(
        id = id ?: 0L,
        type = type.orEmpty(),
        title = title.orEmpty(),
        body = body.orEmpty(),
        dataJson = data.orEmpty(),
        readAt = readAt?.takeIf { it.isNotBlank() },
        createdAt = createdAt.orEmpty()
    )
}

data class NotificationsResponseDto(
    @SerializedName("notifications") val notifications: List<NotificationItemDto>?,
    @SerializedName("unreadCount")   val unreadCount: Long?
) {
    fun toDomain(): NotificationsList = NotificationsList(
        notifications = notifications?.map { it.toDomain() } ?: emptyList(),
        unreadCount = unreadCount ?: 0L
    )
}
