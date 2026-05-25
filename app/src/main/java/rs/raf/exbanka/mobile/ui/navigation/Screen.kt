package rs.raf.exbanka.mobile.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

    // ── Kartice / Računi (mobilni frontend za bank-service rute) ──────────────
    data object Cards : Screen("cards")

    data object Accounts : Screen("accounts")

    data object CardTransactions : Screen(
        "cards/{cardId}/transactions?racunId={racunId}&cardLabel={cardLabel}"
    ) {
        fun createRoute(cardId: String, racunId: String, cardLabel: String): String {
            val encodedLabel = URLEncoder.encode(cardLabel, StandardCharsets.UTF_8.toString())
            return "cards/$cardId/transactions?racunId=$racunId&cardLabel=$encodedLabel"
        }
    }

    // ── Menjačnica / Kursna lista ─────────────────────────────────────────────
    data object ExchangeRates : Screen("exchange/rates")
    data object ExchangeRateHistory : Screen("exchange/rates/history")
    data object CurrencyExchange : Screen("exchange/convert")

    // ── Krediti ───────────────────────────────────────────────────────────────
    data object Credits : Screen("credits")

    data object CreditDetail : Screen("credits/{creditId}") {
        fun createRoute(creditId: String) = "credits/$creditId"
    }

    // ── Quick Approve ─────────────────────────────────────────────────────────
    data object QuickApprove : Screen("quick-approve")

    // ── OTC istorija ──────────────────────────────────────────────────────────
    data object OtcHistory : Screen("otc/history")

    // ── Notifications inbox ───────────────────────────────────────────────────
    data object Notifications : Screen("notifications")

    // ── Fondovi ───────────────────────────────────────────────────────────────
    data object Funds : Screen("funds")

    data object FundDetail : Screen("funds/{fundId}?name={name}") {
        fun createRoute(fundId: String, name: String): String {
            val encoded = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
            return "funds/$fundId?name=$encoded"
        }
    }
}
