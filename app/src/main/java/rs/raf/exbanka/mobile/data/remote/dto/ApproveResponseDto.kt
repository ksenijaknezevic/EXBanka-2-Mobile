package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import rs.raf.exbanka.mobile.domain.model.VerificationCode
import java.time.Instant

/**
 * Odgovara ApprovePendingActionResponse proto poruci iz bank-service.
 * gRPC-Gateway konvertuje snake_case u camelCase.
 *
 * POST /bank/transactions/{id}/approve → odgovor:
 * {
 *   "verificationCode":  "847293",
 *   "expiresAt":         "2024-01-15T10:35:00Z",
 *   "expiresInSeconds":  300
 * }
 */
data class ApproveResponseDto(
    @SerializedName("verificationCode") val verificationCode: String,
    @SerializedName("expiresAt")        val expiresAt: String,
    @SerializedName("expiresInSeconds") val expiresInSeconds: Int
) {
    fun toDomain(transactionId: String): VerificationCode = VerificationCode(
        transactionId = transactionId,
        code = verificationCode,
        expiresAt = runCatching { Instant.parse(expiresAt) }
            .getOrDefault(Instant.now().plusSeconds(expiresInSeconds.toLong())),
        expiresInSeconds = expiresInSeconds
    )
}
