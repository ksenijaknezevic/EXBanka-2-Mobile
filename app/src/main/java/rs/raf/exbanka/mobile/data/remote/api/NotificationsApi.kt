package rs.raf.exbanka.mobile.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rs.raf.exbanka.mobile.data.remote.dto.NotificationsResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.RegisterDeviceRequestDto
import rs.raf.exbanka.mobile.data.remote.dto.RegisterDeviceResponseDto
import rs.raf.exbanka.mobile.data.remote.dto.UnregisterDeviceRequestDto

/** Retrofit interfejs za FCM device registraciju i in-app notifications inbox. */
interface NotificationsApi {

    /** POST /bank/user/devices — upsert FCM tokena za trenutnog korisnika. */
    @POST("bank/user/devices")
    suspend fun registerDevice(
        @Body request: RegisterDeviceRequestDto
    ): Response<RegisterDeviceResponseDto>

    /** DELETE /bank/user/devices — odjavi (logout) konkretni FCM token. */
    @HTTP(method = "DELETE", path = "bank/user/devices", hasBody = true)
    suspend fun unregisterDevice(
        @Body request: UnregisterDeviceRequestDto
    ): Response<Unit>

    /** GET /bank/user/notifications — lista in-app notifikacija. */
    @GET("bank/user/notifications")
    suspend fun getNotifications(
        @Query("unreadOnly") unreadOnly: Boolean? = null,
        @Query("limit") limit: Int? = null
    ): Response<NotificationsResponseDto>

    /** POST /bank/user/notifications/{id}/read — označava jednu kao pročitanu. */
    @POST("bank/user/notifications/{id}/read")
    suspend fun markRead(@Path("id") id: Long): Response<Unit>

    /** POST /bank/user/notifications/read-all — označava sve kao pročitane. */
    @POST("bank/user/notifications/read-all")
    suspend fun markAllRead(): Response<Unit>
}
