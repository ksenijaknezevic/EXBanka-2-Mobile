package rs.raf.exbanka.mobile.data.repository

import rs.raf.exbanka.mobile.data.remote.api.FundApi
import rs.raf.exbanka.mobile.domain.model.FundPerformancePoint
import rs.raf.exbanka.mobile.domain.model.FundsOverview
import rs.raf.exbanka.mobile.domain.repository.FundRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

class FundRepositoryImpl @Inject constructor(
    private val fundApi: FundApi
) : FundRepository {

    override suspend fun getFunds(): NetworkResult<FundsOverview> {
        return try {
            val response = fundApi.getFunds()
            if (response.isSuccessful) {
                val overview = response.body()?.toDomain()
                    ?: return NetworkResult.Error("Prazan odgovor sa servera")
                NetworkResult.Success(overview)
            } else {
                NetworkResult.Error("Greška pri učitavanju fondova (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun getPerformance(
        fundId: String,
        period: String?
    ): NetworkResult<List<FundPerformancePoint>> {
        return try {
            val response = fundApi.getPerformance(fundId, period)
            if (response.isSuccessful) {
                val points = response.body()?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(points)
            } else {
                NetworkResult.Error("Greška pri učitavanju performansi (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }
}
