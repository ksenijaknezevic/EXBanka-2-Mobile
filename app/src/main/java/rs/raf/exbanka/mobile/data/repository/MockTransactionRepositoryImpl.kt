package rs.raf.exbanka.mobile.data.repository

import kotlinx.coroutines.delay
import rs.raf.exbanka.mobile.domain.model.Transaction
import rs.raf.exbanka.mobile.domain.model.TransactionStatus
import rs.raf.exbanka.mobile.domain.model.VerificationCode
import rs.raf.exbanka.mobile.domain.repository.TransactionRepository
import rs.raf.exbanka.mobile.util.NetworkResult
import java.time.Instant
import javax.inject.Inject

/**
 * Mock implementation of [TransactionRepository].
 * Used when [BuildConfig.USE_MOCK_API] == true (default in debug builds).
 *
 * Provides realistic sample data so the full UI flow can be tested
 * without a running backend. Switch to [TransactionRepositoryImpl]
 * in [rs.raf.exbanka.mobile.di.RepositoryModule] when the backend is ready.
 */
class MockTransactionRepositoryImpl @Inject constructor() : TransactionRepository {

    private val sampleTransactions = listOf(
        Transaction(
            id = "txn-001",
            recipientName = "Nikola Jovanović",
            recipientAccount = "RS35105008123123123173",
            amount = 15000.00,
            currency = "RSD",
            purpose = "Stanarina april 2024",
            createdAt = Instant.now().minusSeconds(300),
            status = TransactionStatus.PENDING
        ),
        Transaction(
            id = "txn-002",
            recipientName = "Ana Petrović",
            recipientAccount = "RS35265110000012345678",
            amount = 250.50,
            currency = "EUR",
            purpose = "Povrat duga",
            createdAt = Instant.now().minusSeconds(1800),
            status = TransactionStatus.PENDING
        ),
        Transaction(
            id = "txn-003",
            recipientName = "Tech Solutions d.o.o.",
            recipientAccount = "RS35160005080003100143",
            amount = 89999.00,
            currency = "RSD",
            purpose = "Faktura br. 2024-047",
            createdAt = Instant.now().minusSeconds(7200),
            status = TransactionStatus.PENDING
        )
    )

    override suspend fun getPendingTransactions(): NetworkResult<List<Transaction>> {
        delay(800) // simulate network latency
        return NetworkResult.Success(sampleTransactions)
    }

    override suspend fun getTransactionById(id: String): NetworkResult<Transaction> {
        delay(400)
        val txn = sampleTransactions.find { it.id == id }
        return if (txn != null) {
            NetworkResult.Success(txn)
        } else {
            NetworkResult.Error("Transaction not found: $id")
        }
    }

    override suspend fun approveTransaction(id: String): NetworkResult<VerificationCode> {
        delay(1000) // simulate approval processing
        return NetworkResult.Success(
            VerificationCode(
                transactionId = id,
                // Generate a random 6-digit code for demo purposes
                code = (100000..999999).random().toString(),
                expiresAt = Instant.now().plusSeconds(300),
                expiresInSeconds = 300
            )
        )
    }
}
