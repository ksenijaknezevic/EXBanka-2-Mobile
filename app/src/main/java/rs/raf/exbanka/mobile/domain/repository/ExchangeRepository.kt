package rs.raf.exbanka.mobile.domain.repository

import rs.raf.exbanka.mobile.domain.model.ExchangeConversion
import rs.raf.exbanka.mobile.domain.model.ExchangeExecution
import rs.raf.exbanka.mobile.domain.model.ExchangeRate
import rs.raf.exbanka.mobile.domain.model.ExchangeRateHistoryPoint
import rs.raf.exbanka.mobile.util.NetworkResult

interface ExchangeRepository {
    suspend fun getRates(): NetworkResult<List<ExchangeRate>>

    suspend fun getHistory(
        oznaka: String? = null,
        days: Int? = 30
    ): NetworkResult<List<ExchangeRateHistoryPoint>>

    suspend fun convert(
        from: String,
        to: String,
        amount: Double
    ): NetworkResult<ExchangeConversion>

    suspend fun execute(
        sourceAccountId: Long,
        targetAccountId: Long,
        fromOznaka: String,
        toOznaka: String,
        amount: Double
    ): NetworkResult<ExchangeExecution>
}
