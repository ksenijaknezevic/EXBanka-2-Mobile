package rs.raf.exbanka.mobile.ui.screens.currencyexchange

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.exbanka.mobile.domain.model.Account
import rs.raf.exbanka.mobile.ui.components.ErrorView
import rs.raf.exbanka.mobile.ui.components.LoadingView
import rs.raf.exbanka.mobile.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeScreen(
    onBack: () -> Unit,
    viewModel: CurrencyExchangeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.executionError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearExecutionError() },
            title = { Text("Konverzija nije uspela") },
            text = { Text(uiState.executionError!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearExecutionError() }) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menjačnica", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoadingAccounts -> LoadingView()
                uiState.loadError != null -> ErrorView(
                    uiState.loadError!!,
                    onRetry = { viewModel.loadAccounts() }
                )
                uiState.executionResult != null -> ExecutionSuccessView(
                    fromCurrency = uiState.executionResult!!.fromOznaka,
                    toCurrency = uiState.executionResult!!.toOznaka,
                    originalAmount = uiState.executionResult!!.originalAmount,
                    netAmount = uiState.executionResult!!.netAmount,
                    referenceId = uiState.executionResult!!.referenceId,
                    onDone = viewModel::acknowledgeResult
                )
                uiState.accounts.isEmpty() -> EmptyAccountsForExchange()
                else -> ExchangeForm(
                    state = uiState,
                    onSourceSelected = viewModel::onSourceSelected,
                    onTargetSelected = viewModel::onTargetSelected,
                    onAmountChange = viewModel::onAmountChange,
                    onExecute = viewModel::execute
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExchangeForm(
    state: CurrencyExchangeUiState,
    onSourceSelected: (Account) -> Unit,
    onTargetSelected: (Account) -> Unit,
    onAmountChange: (String) -> Unit,
    onExecute: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Iz računa",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AccountDropdown(
            selected = state.sourceAccount,
            accounts = state.accounts,
            onSelected = onSourceSelected
        )

        OutlinedTextField(
            value = state.amountInput,
            onValueChange = onAmountChange,
            label = { Text("Iznos") },
            suffix = { Text(state.sourceAccount?.valuta.orEmpty()) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            "U račun",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AccountDropdown(
            selected = state.targetAccount,
            accounts = state.accounts.filter { it.id != state.sourceAccount?.id },
            onSelected = onTargetSelected
        )

        ConversionPreview(state = state)

        Button(
            onClick = onExecute,
            enabled = canExecute(state) && !state.isExecuting,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (state.isExecuting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Potvrdi konverziju", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun canExecute(state: CurrencyExchangeUiState): Boolean {
    val amount = state.amountInput.toDoubleOrNull() ?: return false
    if (amount <= 0.0) return false
    val s = state.sourceAccount ?: return false
    val t = state.targetAccount ?: return false
    if (s.id == t.id) return false
    if (s.valuta == t.valuta) return false
    return state.conversion != null && !state.isCalculating
}

@Composable
private fun ConversionPreview(state: CurrencyExchangeUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "Očekivani iznos",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            when {
                state.isCalculating -> CircularProgressIndicator(
                    modifier = Modifier.size(22.dp), strokeWidth = 2.dp
                )
                state.previewError != null -> Text(
                    state.previewError,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                state.conversion != null -> {
                    val cur = state.targetAccount?.valuta.orEmpty()
                    Text(
                        formatAmount(state.conversion.result, cur),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Bruto: ${formatAmount(state.conversion.bruto, cur)} • Provizija: ${formatAmount(state.conversion.provizija, cur)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (state.conversion.rateNote.isNotBlank()) {
                        Text(
                            state.conversion.rateNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> Text(
                    "Unesite iznos i odaberite različite valute za prikaz konverzije.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountDropdown(
    selected: Account?,
    accounts: List<Account>,
    onSelected: (Account) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected?.let { "${it.valuta} • ${it.brojRacuna}" } ?: "—",
            onValueChange = {},
            readOnly = true,
            label = { Text("Račun") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            accounts.forEach { acc ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text("${acc.valuta} • ${acc.brojRacuna}")
                            Text(
                                "Raspoloživo: ${formatAmount(acc.raspolozivoStanje, acc.valuta)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onSelected(acc)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ExecutionSuccessView(
    fromCurrency: String,
    toCurrency: String,
    originalAmount: Double,
    netAmount: Double,
    referenceId: String,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Konverzija uspešna",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "${formatAmount(originalAmount, fromCurrency)} → ${formatAmount(netAmount, toCurrency)}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        if (referenceId.isNotBlank()) {
            Text(
                "Ref: $referenceId",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("U redu")
        }
    }
}

@Composable
private fun EmptyAccountsForExchange() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CurrencyExchange,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Nema dostupnih računa za konverziju",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private fun formatAmount(amount: Double, currency: String): String {
    val nf = NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }
    return "${nf.format(amount)} $currency"
}
