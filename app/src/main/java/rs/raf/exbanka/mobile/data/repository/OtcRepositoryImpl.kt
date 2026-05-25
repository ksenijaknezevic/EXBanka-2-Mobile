package rs.raf.exbanka.mobile.data.repository

import rs.raf.exbanka.mobile.data.remote.api.OtcApi
import rs.raf.exbanka.mobile.domain.model.OtcNegotiation
import rs.raf.exbanka.mobile.domain.repository.OtcRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

class OtcRepositoryImpl @Inject constructor(
    private val otcApi: OtcApi
) : OtcRepository {

    override suspend fun getHistory(
        status: String?,
        from: String?,
        to: String?,
        counterpartId: Long?
    ): NetworkResult<List<OtcNegotiation>> {
        return try {
            val response = otcApi.getHistory(status, from, to, counterpartId)
            if (response.isSuccessful) {
                val items = response.body()?.negotiations?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(items)
            } else {
                NetworkResult.Error("Greška pri učitavanju OTC istorije (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }
}
