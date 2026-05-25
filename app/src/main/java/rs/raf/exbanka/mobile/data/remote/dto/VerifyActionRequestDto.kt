package rs.raf.exbanka.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName

// POST /bank/client/pending-actions/{id}/verify
data class VerifyActionRequestDto(
    @SerializedName("code") val code: String
)
