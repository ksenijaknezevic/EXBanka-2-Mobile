package rs.raf.exbanka.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import rs.raf.exbanka.mobile.ui.screens.login.LoginScreen
import rs.raf.exbanka.mobile.ui.screens.login.LoginViewModel
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

        // ── Pending Transactions List ─────────────────────────────────────────
        composable(Screen.PendingTransactions.route) {
            PendingTransactionsScreen(
                onTransactionClick = { transactionId ->
                    navController.navigate(Screen.TransactionDetail.createRoute(transactionId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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
                        // Remove the detail screen so back goes to the list
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
    }
}
