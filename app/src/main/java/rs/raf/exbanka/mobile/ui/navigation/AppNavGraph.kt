package rs.raf.exbanka.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import rs.raf.exbanka.mobile.ui.screens.accounts.AccountsScreen
import rs.raf.exbanka.mobile.ui.screens.cards.CardsScreen
import rs.raf.exbanka.mobile.ui.screens.cardtransactions.CardTransactionsScreen
import rs.raf.exbanka.mobile.ui.screens.creditdetail.CreditDetailScreen
import rs.raf.exbanka.mobile.ui.screens.credits.CreditsScreen
import rs.raf.exbanka.mobile.ui.screens.currencyexchange.CurrencyExchangeScreen
import rs.raf.exbanka.mobile.ui.screens.exchangeratehistory.ExchangeRateHistoryScreen
import rs.raf.exbanka.mobile.ui.screens.exchangerates.ExchangeRatesScreen
import rs.raf.exbanka.mobile.ui.screens.funddetail.FundDetailScreen
import rs.raf.exbanka.mobile.ui.screens.funds.FundsScreen
import rs.raf.exbanka.mobile.ui.screens.login.LoginScreen
import rs.raf.exbanka.mobile.ui.screens.login.LoginViewModel
import rs.raf.exbanka.mobile.ui.screens.otchistory.OtcHistoryScreen
import rs.raf.exbanka.mobile.ui.screens.quickapprove.QuickApproveScreen
import rs.raf.exbanka.mobile.ui.screens.transactiondetail.TransactionDetailScreen
import rs.raf.exbanka.mobile.ui.screens.transactions.PendingTransactionsScreen
import rs.raf.exbanka.mobile.ui.screens.verificationcode.VerificationCodeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // Determine start destination based on login state
    val loginViewModel: LoginViewModel = hiltViewModel()
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)

    // Wait until login state is known
    if (isLoggedIn == null) return

    val startDestination = if (isLoggedIn == true) {
        Screen.PendingTransactions.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ── Login ─────────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.PendingTransactions.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Pending Transactions List (home) ──────────────────────────────────
        composable(Screen.PendingTransactions.route) {
            PendingTransactionsScreen(
                onTransactionClick = { transactionId ->
                    navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onOpenCards = { navController.navigate(Screen.Cards.route) },
                onOpenAccounts = { navController.navigate(Screen.Accounts.route) },
                onOpenExchangeRates = { navController.navigate(Screen.ExchangeRates.route) },
                onOpenCurrencyExchange = { navController.navigate(Screen.CurrencyExchange.route) },
                onOpenCredits = { navController.navigate(Screen.Credits.route) },
                onOpenQuickApprove = { navController.navigate(Screen.QuickApprove.route) },
                onOpenOtcHistory = { navController.navigate(Screen.OtcHistory.route) },
                onOpenFunds = { navController.navigate(Screen.Funds.route) }
            )
        }

        // ── Transaction Detail ────────────────────────────────────────────────
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: return@composable
            TransactionDetailScreen(
                transactionId = transactionId,
                onApproved = { code, expiresIn ->
                    navController.navigate(
                        Screen.VerificationCode.createRoute(transactionId, code, expiresIn)
                    ) {
                        popUpTo(Screen.TransactionDetail.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Verification Code Display ─────────────────────────────────────────
        composable(
            route = Screen.VerificationCode.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType },
                navArgument("expiresIn") {
                    type = NavType.IntType
                    defaultValue = 300
                }
            )
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: return@composable
            val expiresIn = backStackEntry.arguments?.getInt("expiresIn") ?: 300

            VerificationCodeScreen(
                code = code,
                expiresInSeconds = expiresIn,
                onDone = {
                    navController.navigate(Screen.PendingTransactions.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Cards List ────────────────────────────────────────────────────────
        composable(Screen.Cards.route) {
            CardsScreen(
                onBack = { navController.popBackStack() },
                onCardClick = { cardId, racunId, cardLabel, _ ->
                    navController.navigate(
                        Screen.CardTransactions.createRoute(cardId, racunId, cardLabel)
                    )
                }
            )
        }

        // ── Accounts List ─────────────────────────────────────────────────────
        composable(Screen.Accounts.route) {
            AccountsScreen(onBack = { navController.popBackStack() })
        }

        // ── Card Transactions ─────────────────────────────────────────────────
        composable(
            route = Screen.CardTransactions.route,
            arguments = listOf(
                navArgument("cardId") { type = NavType.StringType },
                navArgument("racunId") { type = NavType.StringType },
                navArgument("cardLabel") {
                    type = NavType.StringType
                    defaultValue = "Kartica"
                }
            )
        ) {
            CardTransactionsScreen(onBack = { navController.popBackStack() })
        }

        // ── Kursna lista ──────────────────────────────────────────────────────
        composable(Screen.ExchangeRates.route) {
            ExchangeRatesScreen(
                onBack = { navController.popBackStack() },
                onOpenHistory = { navController.navigate(Screen.ExchangeRateHistory.route) }
            )
        }

        composable(Screen.ExchangeRateHistory.route) {
            ExchangeRateHistoryScreen(onBack = { navController.popBackStack() })
        }

        // ── Menjačnica (konverzija) ───────────────────────────────────────────
        composable(Screen.CurrencyExchange.route) {
            CurrencyExchangeScreen(onBack = { navController.popBackStack() })
        }

        // ── Krediti ───────────────────────────────────────────────────────────
        composable(Screen.Credits.route) {
            CreditsScreen(
                onBack = { navController.popBackStack() },
                onCreditClick = { creditId ->
                    navController.navigate(Screen.CreditDetail.createRoute(creditId))
                }
            )
        }

        composable(
            route = Screen.CreditDetail.route,
            arguments = listOf(navArgument("creditId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("creditId") ?: return@composable
            CreditDetailScreen(creditId = id, onBack = { navController.popBackStack() })
        }

        // ── Quick Approve ─────────────────────────────────────────────────────
        composable(Screen.QuickApprove.route) {
            QuickApproveScreen(onBack = { navController.popBackStack() })
        }

        // ── OTC istorija ──────────────────────────────────────────────────────
        composable(Screen.OtcHistory.route) {
            OtcHistoryScreen(onBack = { navController.popBackStack() })
        }

        // ── Fondovi ───────────────────────────────────────────────────────────
        composable(Screen.Funds.route) {
            FundsScreen(
                onBack = { navController.popBackStack() },
                onFundClick = { fundId, name ->
                    navController.navigate(Screen.FundDetail.createRoute(fundId, name))
                }
            )
        }

        composable(
            route = Screen.FundDetail.route,
            arguments = listOf(
                navArgument("fundId") { type = NavType.StringType },
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "Fond"
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("fundId") ?: return@composable
            val name = backStackEntry.arguments?.getString("name") ?: "Fond"
            FundDetailScreen(fundId = id, fundName = name, onBack = { navController.popBackStack() })
        }
    }
}
