package rs.raf.exbanka.mobile.domain.repository

import rs.raf.exbanka.mobile.domain.model.Credit
import rs.raf.exbanka.mobile.domain.model.CreditDetail
import rs.raf.exbanka.mobile.util.NetworkResult

interface CreditRepository {
    suspend fun getClientCredits(): NetworkResult<List<Credit>>
    suspend fun getCreditDetails(id: String): NetworkResult<CreditDetail>
}
