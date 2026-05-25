package rs.raf.exbanka.mobile.ui.screens.creditdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import rs.raf.exbanka.mobile.domain.model.CreditInstallment
import rs.raf.exbanka.mobile.ui.components.ErrorView
import rs.raf.exbanka.mobile.ui.components.LoadingView
import rs.raf.exbanka.mobile.ui.screens.credits.formatAmount
import rs.raf.exbanka.mobile.ui.screens.credits.formatCreditType
import rs.raf.exbanka.mobile.ui.screens.credits.formatDate
import rs.raf.exbanka.mobile.ui.theme.ErrorRed
import rs.raf.exbanka.mobile.ui.theme.SuccessGreen
import rs.raf.exbanka.mobile.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditDetailScreen(
    creditId: String,
    onBack: () -> Unit,
    viewModel: CreditDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(creditId) {
        viewModel.load(creditId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalji kredita") },
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
            when (val state = uiState) {
                is CreditDetailUiState.Loading -> LoadingView()
                is CreditDetailUiState.Error -> ErrorView(
                    state.message,
                    onRetry = { viewModel.load(creditId) }
                )
                is CreditDetailUiState.Success -> CreditDetailContent(state)
            }
        }
    }
}

@Composable
private fun CreditDetailContent(state: CreditDetailUiState.Success) {
    val credit = state.detail.credit
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(formatCreditType(credit.vrstaKredita), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(credit.brojKredita.ifBlank { "Kredit #${credit.id}" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    InfoRow("Iznos kredita", formatAmount(credit.iznosKredita, credit.valuta))
                    InfoRow("Preostalo dugovanje", formatAmount(credit.preostaloDugovanje, credit.valuta))
                    InfoRow("Mesečna rata", formatAmount(credit.iznosMesecneRate, credit.valuta))
                    InfoRow("Sledeća rata", formatDate(credit.datumSledeceRate))
                    InfoRow("Datum isplate", formatDate(credit.datumIsplate))
                    InfoRow("Datum ugovaranja", formatDate(credit.datumUgovaranja))
                    InfoRow("Period otplate", "${credit.periodOtplate} mes.")
                    InfoRow("Nominalna stopa", "%.2f%%".format(credit.nominalnaKamatnaStopa))
                    InfoRow("Efektivna stopa", "%.2f%%".format(credit.efektivnaKamatnaStopa))
                    InfoRow("Tip kamate", credit.tipKamate.ifBlank { "—" })
                    InfoRow("Status", credit.status.ifBlank { "—" })
                }
            }
        }

        item {
            Text(
                "Plan otplate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (state.detail.rate.isEmpty()) {
            item {
                Text(
                    "Plan rata trenutno nije dostupan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(state.detail.rate, key = { it.id }) { rata ->
                InstallmentCard(rata)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
}

@Composable
private fun InstallmentCard(rata: CreditInstallment) {
    val (statusColor, statusText) = when (rata.statusPlacanja.uppercase()) {
        "PLACENO" -> SuccessGreen to "Plaćeno"
        "KASNI" -> ErrorRed to "Kasni"
        "NEPLACENO" -> WarningAmber to "Neplaćeno"
        else -> MaterialTheme.colorScheme.onSurfaceVariant to rata.statusPlacanja
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        formatAmount(rata.iznosRate, rata.valuta),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Dospeće: ${formatDate(rata.ocekivaniDatumDospeca)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (rata.praviDatumDospeca != null) {
                        Text(
                            "Plaćeno: ${formatDate(rata.praviDatumDospeca)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
