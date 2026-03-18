package rs.raf.exbanka.mobile.domain.model

import java.time.Instant

/**
 * Domain model for a pending payment transaction.
 * Represents a transaction initiated on the web (laptop) that
 * requires mobile approval.
 */
data class Transaction(
    val id: String,
    val recipientName: String,
    val recipientAccount: String,
    val amount: Double,
    val currency: String,
    val purpose: String,
    val createdAt: Instant,
    val status: TransactionStatus
)

enum class TransactionStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED,
    EXPIRED
}
