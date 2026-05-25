package rs.raf.exbanka.mobile.domain.repository

import rs.raf.exbanka.mobile.domain.model.FundPerformancePoint
import rs.raf.exbanka.mobile.domain.model.FundsOverview
import rs.raf.exbanka.mobile.util.NetworkResult

interface FundRepository {
    suspend fun getFunds(): NetworkResult<FundsOverview>
    suspend fun getPerformance(fundId: String, period: String? = null): NetworkResult<List<FundPerformancePoint>>
}
