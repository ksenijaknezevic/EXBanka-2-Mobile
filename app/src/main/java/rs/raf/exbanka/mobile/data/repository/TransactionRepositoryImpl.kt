package rs.raf.exbanka.mobile.data.repository

import rs.raf.exbanka.mobile.data.remote.api.TransactionApi
import rs.raf.exbanka.mobile.domain.model.Transaction
import rs.raf.exbanka.mobile.domain.model.VerificationCode
import rs.raf.exbanka.mobile.domain.repository.TransactionRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import javax.inject.Inject

/**
 * Prava mrežna implementacija [TransactionRepository].
 * Komunicira sa bank-service putem [TransactionApi].
 */
class TransactionRepositoryImpl @Inject constructor(
    private val transactionApi: TransactionApi
) : TransactionRepository {

    override suspend fun getPendingTransactions(): NetworkResult<List<Transaction>> {
        return try {
            val response = transactionApi.getPendingTransactions()
            if (response.isSuccessful) {
                val transactions = response.body()?.transactions
                    ?.map { it.toDomain() }
                    ?: emptyList()
                NetworkResult.Success(transactions)
            } else {
                NetworkResult.Error("Greška pri učitavanju zahteva (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun getTransactionById(id: String): NetworkResult<Transaction> {
        return try {
            val response = transactionApi.getTransactionById(id)
            if (response.isSuccessful) {
                val transaction = response.body()?.transaction?.toDomain()
                if (transaction != null) {
                    NetworkResult.Success(transaction)
                } else {
                    NetworkResult.Error("Zahtev nije pronađen")
                }
            } else {
                NetworkResult.Error("Greška pri učitavanju zahteva (${response.code()})", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }

    override suspend fun approveTransaction(id: String): NetworkResult<VerificationCode> {
        return try {
            val response = transactionApi.approveTransaction(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body.toDomain(id))
                } else {
                    NetworkResult.Error("Prazan odgovor sa servera")
                }
            } else {
                val errorMsg = when (response.code()) {
                    404 -> "Zahtev nije pronađen"
                    409 -> "Zahtev je već obrađen"
                    403 -> "Zahtev je otkazan — previše neuspešnih pokušaja"
                    else -> "Greška pri odobravanju (${response.code()})"
                }
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Greška mreže")
        }
    }
}
