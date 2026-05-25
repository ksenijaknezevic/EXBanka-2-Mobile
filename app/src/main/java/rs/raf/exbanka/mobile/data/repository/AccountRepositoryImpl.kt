package rs.raf.exbanka.mobile.data.repository

import rs.raf.exbanka.mobile.data.remote.api.AccountApi
import rs.raf.exbanka.mobile.domain.model.Account
import rs.raf.exbanka.mobile.domain.model.AccountTransaction
import rs.raf.exbanka.mobile.domain.repository.AccountRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountApi: AccountApi
) : AccountRepository {

    override suspend fun getMyAccounts(): NetworkResult<List<Account>> {
        return try {
            val response = accountApi.getMyAccounts()
            if (response.isSuccessful) {
                val accounts = response.body()?.accounts?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(accounts)
            } else {
                NetworkResult.Error("Greška pri učitavanju računa (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun getAccountTransactions(
        racunId: String,
        currency: String
    ): NetworkResult<List<AccountTransaction>> {
        return try {
            val response = accountApi.getAccountTransactions(racunId)
            if (response.isSuccessful) {
                val txs = response.body()?.transactions
                    ?.map { it.toDomain(currency) }
                    ?: emptyList()
                NetworkResult.Success(txs)
            } else {
                NetworkResult.Error("Greška pri učitavanju transakcija (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }
}
