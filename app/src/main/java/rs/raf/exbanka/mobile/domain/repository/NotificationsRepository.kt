package rs.raf.exbanka.mobile.domain.repository

import rs.raf.exbanka.mobile.domain.model.NotificationsList
import rs.raf.exbanka.mobile.util.NetworkResult

interface NotificationsRepository {
    /** Registruje FCM token (po startup-u aplikacije). */
    suspend fun registerDevice(fcmToken: String, deviceId: String? = null): NetworkResult<Unit>

    /** Uklanja FCM token (po logout-u). */
    suspend fun unregisterDevice(fcmToken: String): NetworkResult<Unit>

    /** Dohvata in-app notifikacije. */
    suspend fun getNotifications(unreadOnly: Boolean = false, limit: Int = 50): NetworkResult<NotificationsList>

    /** Označava jednu notifikaciju kao pročitanu. */
    suspend fun markRead(id: Long): NetworkResult<Unit>

    /** Označava sve notifikacije korisnika kao pročitane. */
    suspend fun markAllRead(): NetworkResult<Unit>
}
