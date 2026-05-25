package rs.raf.exbanka.mobile.data.repository

import rs.raf.exbanka.mobile.data.remote.api.ExchangeApi
import rs.raf.exbanka.mobile.data.remote.dto.ExchangeExecuteRequestDto
import rs.raf.exbanka.mobile.domain.model.ExchangeConversion
import rs.raf.exbanka.mobile.domain.model.ExchangeExecution
import rs.raf.exbanka.mobile.domain.model.ExchangeRate
import rs.raf.exbanka.mobile.domain.model.ExchangeRateHistoryPoint
import rs.raf.exbanka.mobile.domain.repository.ExchangeRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeApi: ExchangeApi
) : ExchangeRepository {

    override suspend fun getRates(): NetworkResult<List<ExchangeRate>> {
        return try {
            val response = exchangeApi.getRates()
            if (response.isSuccessful) {
                val rates = response.body()?.rates?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(rates)
            } else {
                NetworkResult.Error("Greška pri učitavanju kursne liste (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun convert(
        from: String,
        to: String,
        amount: Double
    ): NetworkResult<ExchangeConversion> {
        return try {
            val response = exchangeApi.convert(from, to, amount)
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return NetworkResult.Error("Prazan odgovor sa servera")
                NetworkResult.Success(body.toDomain())
            } else {
                NetworkResult.Error("Konverzija nije uspela (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun getHistory(
        oznaka: String?,
        days: Int?
    ): NetworkResult<List<ExchangeRateHistoryPoint>> {
        return try {
            val response = exchangeApi.getHistory(oznaka = oznaka, days = days)
            if (response.isSuccessful) {
                val points = response.body()?.history?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(points)
            } else {
                NetworkResult.Error("Greška pri učitavanju istorije kursne liste (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun execute(
        sourceAccountId: Long,
        targetAccountId: Long,
        fromOznaka: String,
        toOznaka: String,
        amount: Double
    ): NetworkResult<ExchangeExecution> {
        return try {
            val response = exchangeApi.execute(
                ExchangeExecuteRequestDto(
                    sourceAccountId = sourceAccountId,
                    targetAccountId = targetAccountId,
                    fromOznaka = fromOznaka,
                    toOznaka = toOznaka,
                    amount = amount
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return NetworkResult.Error("Prazan odgovor sa servera")
                NetworkResult.Success(body.toDomain())
            } else {
                NetworkResult.Error("Izvršenje konverzije nije uspelo (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }
}
