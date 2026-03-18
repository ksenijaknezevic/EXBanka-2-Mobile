package rs.raf.exbanka.mobile.ui.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 * The [route] string is used by Navigation Compose.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object PendingTransactions : Screen("transactions/pending")

    data object TransactionDetail : Screen("transactions/{transactionId}") {
        fun createRoute(transactionId: String) = "transactions/$transactionId"
    }

    data object VerificationCode : Screen(
        "transactions/{transactionId}/verification?code={code}&expiresIn={expiresIn}"
    ) {
        fun createRoute(transactionId: String, code: String, expiresIn: Int) =
            "transactions/$transactionId/verification?code=$code&expiresIn=$expiresIn"
    }
}
