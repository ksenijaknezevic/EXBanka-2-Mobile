package rs.raf.exbanka.mobile.ui.screens.credits

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.exbanka.mobile.domain.model.Credit
import rs.raf.exbanka.mobile.ui.components.ErrorView
import rs.raf.exbanka.mobile.ui.components.LoadingView
import rs.raf.exbanka.mobile.ui.theme.ErrorRed
import rs.raf.exbanka.mobile.ui.theme.SuccessGreen
import rs.raf.exbanka.mobile.ui.theme.WarningAmber
import java.text.NumberFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onBack: () -> Unit,
    onCreditClick: (String) -> Unit,
    viewModel: CreditsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moji krediti", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { viewModel.loadCredits() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Osveži")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is CreditsUiState.Loading -> LoadingView()
                is CreditsUiState.Error -> ErrorView(state.message, onRetry = { viewModel.loadCredits() })
                is CreditsUiState.Empty -> EmptyCreditsView()
                is CreditsUiState.Success -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.credits, key = { it.id }) { credit ->
                        CreditCard(credit = credit, onClick = { onCreditClick(credit.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CreditCard(credit: Credit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatCreditType(credit.vrstaKredita),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = credit.brojKredita.ifBlank { "Kredit #${credit.id}" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = credit.status)
            }
            Spacer(Modifier.height(12.dp))

            // Predstojeća rata — istaknuto
            UpcomingInstallment(credit = credit)

            Spacer(Modifier.height(8.dp))

            DetailRow(label = "Preostalo dugovanje", value = formatAmount(credit.preostaloDugovanje, credit.valuta))
            DetailRow(label = "Mesečna rata", value = formatAmount(credit.iznosMesecneRate, credit.valuta))
            DetailRow(label = "Period otplate", value = "${credit.periodOtplate} mes.")
            DetailRow(label = "Nominalna stopa", value = "%.2f%%".format(credit.nominalnaKamatnaStopa))
        }
    }
}

@Composable
private fun UpcomingInstallment(credit: Credit) {
    val due = parseDate(credit.datumSledeceRate)
    val today = LocalDate.now()
    val daysUntil = if (due != null) java.time.temporal.ChronoUnit.DAYS.between(today, due) else null
    val tone = when {
        daysUntil == null -> MaterialTheme.colorScheme.onSurfaceVariant
        daysUntil < 0 -> ErrorRed
        daysUntil <= 3 -> WarningAmber
        else -> SuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = tone,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.size(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Sledeća rata",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    formatAmount(credit.iznosMesecneRate, credit.valuta),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatDate(credit.datumSledeceRate),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = tone
                )
                if (daysUntil != null) {
                    Text(
                        text = when {
                            daysUntil < 0 -> "Kasni ${-daysUntil} d"
                            daysUntil == 0L -> "Dospeva danas"
                            else -> "Za $daysUntil d"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = tone
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, color) = when (status.uppercase()) {
        "ODOBREN" -> "Aktivan" to SuccessGreen
        "U_KASNJENJU" -> "U kašnjenju" to ErrorRed
        "OTPLACEN" -> "Otplaćen" to MaterialTheme.colorScheme.onSurfaceVariant
        "ODBIJEN" -> "Odbijen" to ErrorRed
        else -> status to MaterialTheme.colorScheme.primary
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyCreditsView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Trenutno nemate aktivnih kredita",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Kada vam bude odobren kredit, ovde će se pojaviti detalji i predstojeće rate.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

internal fun parseDate(value: String): LocalDate? = try {
    when {
        value.isBlank() -> null
        value.contains('T') -> OffsetDateTime.parse(value).toLocalDate()
        else -> LocalDate.parse(value)
    }
} catch (_: Exception) {
    null
}

internal fun formatDate(value: String): String {
    val d = parseDate(value) ?: return value.ifBlank { "—" }
    return d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}

internal fun formatCreditType(raw: String): String = when (raw.uppercase()) {
    "GOTOVINSKI" -> "Gotovinski kredit"
    "STAMBENI" -> "Stambeni kredit"
    "AUTO" -> "Auto kredit"
    "REFINANSIRAJUCI" -> "Refinansirajući"
    "STUDENTSKI" -> "Studentski"
    else -> raw.ifBlank { "Kredit" }
}

internal fun formatAmount(amount: Double, currency: String): String {
    val nf = NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }
    return "${nf.format(amount)} $currency"
}
