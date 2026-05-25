package rs.raf.exbanka.mobile.ui.screens.otchistory

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.exbanka.mobile.domain.model.OtcHistoryEntry
import rs.raf.exbanka.mobile.domain.model.OtcNegotiation
import rs.raf.exbanka.mobile.ui.components.ErrorView
import rs.raf.exbanka.mobile.ui.components.LoadingView
import rs.raf.exbanka.mobile.ui.theme.ErrorRed
import rs.raf.exbanka.mobile.ui.theme.SuccessGreen
import rs.raf.exbanka.mobile.ui.theme.WarningAmber
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtcHistoryScreen(
    onBack: () -> Unit,
    viewModel: OtcHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var filtersOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OTC istorija", style = MaterialTheme.typography.titleLarge) },
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
                    IconButton(onClick = { filtersOpen = !filtersOpen }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filteri")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            StatusFilters(
                selected = uiState.filters.status,
                onSelect = viewModel::onStatusChange
            )

            AnimatedVisibility(visible = filtersOpen) {
                FilterPanel(
                    fromValue = uiState.filters.from.orEmpty(),
                    toValue = uiState.filters.to.orEmpty(),
                    counterpartValue = uiState.filters.counterpartIdInput,
                    onFromChange = viewModel::onFromChange,
                    onToChange = viewModel::onToChange,
                    onCounterpartChange = viewModel::onCounterpartChange,
                    onApply = { viewModel.applyFilters(); filtersOpen = false },
                    onClear = { viewModel.clearFilters() }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> LoadingView()
                    uiState.error != null -> ErrorView(uiState.error!!, onRetry = { viewModel.load() })
                    uiState.negotiations.isEmpty() -> EmptyHistoryView()
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.negotiations, key = { it.offerId }) { n -> NegotiationCard(n) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusFilters(
    selected: OtcStatusFilter,
    onSelect: (OtcStatusFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OtcStatusFilter.entries.forEach { status ->
            FilterChip(
                selected = selected == status,
                onClick = { onSelect(status) },
                label = { Text(labelForStatus(status)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterPanel(
    fromValue: String,
    toValue: String,
    counterpartValue: String,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit,
    onCounterpartChange: (String) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fromValue,
                onValueChange = onFromChange,
                label = { Text("Od (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = toValue,
                onValueChange = onToChange,
                label = { Text("Do (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = counterpartValue,
                onValueChange = onCounterpartChange,
                label = { Text("ID druge strane") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Resetuj") }
                Button(onClick = onApply, modifier = Modifier.weight(1f)) { Text("Primeni") }
            }
        }
    }
}

@Composable
private fun NegotiationCard(n: OtcNegotiation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = n.ticker.ifBlank { "Ponuda #${n.offerId}" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (n.stockName.isNotBlank()) {
                        Text(
                            n.stockName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                StatusBadge(n.finalStatus)
            }

            Spacer(Modifier.height(8.dp))
            Row {
                Text(
                    "Kupac: ${n.buyerId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Prodavac: ${n.sellerId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Poslednja izmena: ${formatTimestamp(n.lastModified)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (n.history.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text(
                    "Istorija (${n.history.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                n.history.forEach { entry -> HistoryEntryRow(entry) }
            }
        }
    }
}

@Composable
private fun HistoryEntryRow(entry: OtcHistoryEntry) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatAction(entry.action),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatTimestamp(entry.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            "Korisnik: ${entry.changedBy}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // Promene: stare → nove vrednosti
        renderChange("Količina", entry.oldAmount?.toString(), entry.amount?.toString())
        renderChange("Cena po akciji", entry.oldPricePerStock?.toString(), entry.pricePerStock?.toString())
        renderChange("Premija", entry.oldPremium?.toString(), entry.premium?.toString())
        renderChange("Datum poravnanja", entry.oldSettlementDate, entry.settlementDate)
        if (entry.newStatus != null) {
            Text(
                "Novi status: ${entry.newStatus}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun renderChange(label: String, old: String?, new: String?) {
    if (old == null && new == null) return
    val display = when {
        old != null && new != null && old != new -> "$label: $old → $new"
        new != null -> "$label: $new"
        old != null -> "$label: $old"
        else -> return
    }
    Text(
        display,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusBadge(status: String) {
    val (label, color) = when (status.uppercase()) {
        "ACCEPTED" -> "Prihvaćeno" to SuccessGreen
        "REJECTED" -> "Odbijeno" to ErrorRed
        "DEACTIVATED" -> "Otkazano" to WarningAmber
        else -> status to MaterialTheme.colorScheme.primary
    }
    AssistChip(
        onClick = {},
        label = { Text(label, fontWeight = FontWeight.SemiBold) },
        colors = AssistChipDefaults.assistChipColors(labelColor = color)
    )
}

@Composable
private fun EmptyHistoryView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Nema završenih OTC pregovora",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

private fun labelForStatus(filter: OtcStatusFilter): String = when (filter) {
    OtcStatusFilter.All -> "Sve"
    OtcStatusFilter.Accepted -> "Prihvaćeno"
    OtcStatusFilter.Rejected -> "Odbijeno"
    OtcStatusFilter.Deactivated -> "Otkazano"
}

private fun formatAction(action: String): String = when (action.uppercase()) {
    "CREATED" -> "Kreirana ponuda"
    "COUNTER", "COUNTER_OFFER" -> "Kontraponuda"
    "ACCEPTED" -> "Prihvaćeno"
    "DECLINED", "REJECTED" -> "Odbijeno"
    "DEACTIVATED", "CANCELLED" -> "Otkazano"
    else -> action.ifBlank { "—" }
}

private fun formatTimestamp(raw: String): String {
    if (raw.isBlank()) return "—"
    return try {
        OffsetDateTime.parse(raw).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    } catch (_: Exception) {
        raw
    }
}
