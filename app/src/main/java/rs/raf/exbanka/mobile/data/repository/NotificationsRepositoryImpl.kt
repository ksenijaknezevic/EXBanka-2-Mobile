package rs.raf.exbanka.mobile.data.repository

import rs.raf.exbanka.mobile.data.remote.api.NotificationsApi
import rs.raf.exbanka.mobile.data.remote.dto.RegisterDeviceRequestDto
import rs.raf.exbanka.mobile.data.remote.dto.UnregisterDeviceRequestDto
import rs.raf.exbanka.mobile.domain.model.NotificationsList
import rs.raf.exbanka.mobile.domain.repository.NotificationsRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val notificationsApi: NotificationsApi
) : NotificationsRepository {

    override suspend fun registerDevice(fcmToken: String, deviceId: String?): NetworkResult<Unit> {
        return try {
            val response = notificationsApi.registerDevice(
                RegisterDeviceRequestDto(fcmToken = fcmToken, deviceId = deviceId, platform = "ANDROID")
            )
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Registracija uređaja nije uspela (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun unregisterDevice(fcmToken: String): NetworkResult<Unit> {
        return try {
            val response = notificationsApi.unregisterDevice(UnregisterDeviceRequestDto(fcmToken))
            if (response.isSuccessful) NetworkResult.Success(Unit)
            else NetworkResult.Error("Odjava uređaja nije uspela (${response.code()})", response.code())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun getNotifications(unreadOnly: Boolean, limit: Int): NetworkResult<NotificationsList> {
        return try {
            val response = notificationsApi.getNotifications(
                unreadOnly = if (unreadOnly) true else null,
                limit = limit
            )
            if (response.isSuccessful) {
                val body = response.body()?.toDomain()
                    ?: return NetworkResult.Error("Prazan odgovor sa servera")
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error("Greška pri učitavanju notifikacija (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun markRead(id: Long): NetworkResult<Unit> {
        return try {
            val response = notificationsApi.markRead(id)
            if (response.isSuccessful) NetworkResult.Success(Unit)
            else NetworkResult.Error("Označavanje kao pročitano nije uspelo (${response.code()})", response.code())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun markAllRead(): NetworkResult<Unit> {
        return try {
            val response = notificationsApi.markAllRead()
            if (response.isSuccessful) NetworkResult.Success(Unit)
            else NetworkResult.Error("Označavanje svih kao pročitano nije uspelo (${response.code()})", response.code())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }
}
