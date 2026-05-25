package rs.raf.exbanka.mobile.data.repository

import rs.raf.exbanka.mobile.data.remote.api.CreditApi
import rs.raf.exbanka.mobile.domain.model.Credit
import rs.raf.exbanka.mobile.domain.model.CreditDetail
import rs.raf.exbanka.mobile.domain.repository.CreditRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

class CreditRepositoryImpl @Inject constructor(
    private val creditApi: CreditApi
) : CreditRepository {

    override suspend fun getClientCredits(): NetworkResult<List<Credit>> {
        return try {
            val response = creditApi.getClientCredits()
            if (response.isSuccessful) {
                val credits = response.body()?.credits?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(credits)
            } else {
                NetworkResult.Error("Greška pri učitavanju kredita (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun getCreditDetails(id: String): NetworkResult<CreditDetail> {
        return try {
            val response = creditApi.getCreditDetails(id)
            if (response.isSuccessful) {
                val detail = response.body()?.toDomain()
                    ?: return NetworkResult.Error("Prazan odgovor sa servera")
                NetworkResult.Success(detail)
            } else {
                NetworkResult.Error("Greška pri učitavanju detalja kredita (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }
}
