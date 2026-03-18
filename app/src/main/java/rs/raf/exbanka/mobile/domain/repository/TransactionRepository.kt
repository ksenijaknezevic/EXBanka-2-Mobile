package rs.raf.exbanka.mobile.domain.repository

import rs.raf.exbanka.mobile.domain.model.Transaction
import rs.raf.exbanka.mobile.domain.model.VerificationCode
import rs.raf.exbanka.mobile.util.NetworkResult

interface TransactionRepository {
    /** Fetch all transactions with PENDING status for the authenticated user. */
    suspend fun getPendingTransactions(): NetworkResult<List<Transaction>>

    /** Fetch details for a single transaction by ID. */
    suspend fun getTransactionById(id: String): NetworkResult<Transaction>

    /**
     * Approve a pending transaction.
     * Returns a [VerificationCode] that the user must enter on the laptop.
     *
     * NOTE (ASSUMPTION): This endpoint does not yet exist in the backend.
     * Assumed route: POST /transactions/{id}/approve
     * Expected response: { "verification_code": "...", "expires_at": "...", "expires_in_seconds": 300 }
     *
     * When the backend implements this endpoint, only the data layer (Retrofit API + DTO)
     * needs to be updated — this interface and the ViewModel remain unchanged.
     */
    suspend fun approveTransaction(id: String): NetworkResult<VerificationCode>
}
