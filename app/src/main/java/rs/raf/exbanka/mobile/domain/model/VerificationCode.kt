package rs.raf.exbanka.mobile.domain.model

import java.time.Instant

/**
 * Domain model for a verification code returned after approving a transaction.
 * The user enters this code on the laptop/web browser to confirm the payment.
 *
 * Flow:
 *  1. User clicks "Approve Transaction" on mobile
 *  2. Backend generates a 6-digit code (valid for 5 minutes / 300 seconds)
 *  3. Mobile shows the code with a countdown timer
 *  4. User types the code on the laptop
 *  5. After 3 wrong attempts the transaction is automatically cancelled
 */
data class VerificationCode(
    val transactionId: String,
    val code: String,
    val expiresAt: Instant,
    /** Remaining seconds when code was first received. Typically 300 (5 min). */
    val expiresInSeconds: Int
)
