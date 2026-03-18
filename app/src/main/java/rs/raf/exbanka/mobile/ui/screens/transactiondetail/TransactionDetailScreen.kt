package rs.raf.exbanka.mobile.ui.screens.transactiondetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.exbanka.mobile.domain.model.Transaction
import rs.raf.exbanka.mobile.ui.components.ErrorView
import rs.raf.exbanka.mobile.ui.components.LoadingView
import rs.raf.exbanka.mobile.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    onApproved: (code: String, expiresIn: Int) -> Unit,
    onBack: () -> Unit,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }

    // Navigate to verification code screen when approval succeeds
    LaunchedEffect(uiState.verificationCode) {
        val code = uiState.verificationCode ?: return@LaunchedEffect
        onApproved(code.code, code.expiresInSeconds)
    }

    // Error dialog for approve failure
    if (uiState.approveError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearApproveError() },
            title = { Text("Approval failed") },
            text = { Text(uiState.approveError!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearApproveError() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoadingTransaction -> LoadingView()
                uiState.transactionError != null -> {
                    ErrorView(
                        message = uiState.transactionError!!,
                        onRetry = { viewModel.loadTransaction(transactionId) }
                    )
                }
                uiState.transaction != null -> {
                    TransactionDetailContent(
                        transaction = uiState.transaction!!,
                        isApproving = uiState.isApproving,
                        onApprove = { viewModel.approveTransaction(transactionId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailContent(
    transaction: Transaction,
    isApproving: Boolean,
    onApprove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatAmount(transaction.amount, transaction.currency),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "to ${transaction.recipientName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Divider()
        Spacer(Modifier.height(16.dp))

        // Detail rows
        DetailRow(label = "Recipient", value = transaction.recipientName)
        DetailRow(label = "Account number", value = transaction.recipientAccount)
        DetailRow(label = "Amount", value = formatAmount(transaction.amount, transaction.currency))
        DetailRow(label = "Currency", value = transaction.currency)
        DetailRow(label = "Purpose", value = transaction.purpose)
        DetailRow(
            label = "Date",
            value = transaction.createdAt
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
        )
        DetailRow(label = "Status", value = transaction.status.name)

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(32.dp))

        // Approve button
        Button(
            onClick = onApprove,
            enabled = !isApproving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SuccessGreen
            )
        ) {
            if (isApproving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null
                    )
                    Text(
                        text = "Approve Transaction",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Approving will generate a verification code to enter on your laptop.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
}

private fun formatAmount(amount: Double, currency: String): String {
    return when (currency.uppercase()) {
        "RSD" -> {
            val formatter = NumberFormat.getNumberInstance(Locale("sr", "RS"))
            formatter.maximumFractionDigits = 2
            "${formatter.format(amount)} RSD"
        }
        "EUR" -> "€ %.2f".format(amount)
        "USD" -> "$ %.2f".format(amount)
        else -> "%.2f %s".format(amount, currency)
    }
}
