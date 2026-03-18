package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Matches the actual backend response from POST /login.
 * gRPC-Gateway v2 serializes proto fields as camelCase JSON by default:
 * {
 *   "accessToken":  "eyJ...",
 *   "refreshToken": "eyJ...",
 *   "tokenType":    "Bearer",
 *   "expiresIn":    900
 * }
 */
data class LoginResponseDto(
    @SerializedName("accessToken")  val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("tokenType")    val tokenType: String?,
    @SerializedName("expiresIn")    val expiresIn: Int?
)
