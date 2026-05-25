package rs.raf.exbanka.mobile.domain.repository

import rs.raf.exbanka.mobile.domain.model.OtcNegotiation
import rs.raf.exbanka.mobile.util.NetworkResult

interface OtcRepository {
    suspend fun getHistory(
        status: String? = null,
        from: String? = null,
        to: String? = null,
        counterpartId: Long? = null
    ): NetworkResult<List<OtcNegotiation>>
}
