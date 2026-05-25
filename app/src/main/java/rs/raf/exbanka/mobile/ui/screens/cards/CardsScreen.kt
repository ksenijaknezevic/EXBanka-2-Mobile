package rs.raf.exbanka.mobile.ui.screens.cards

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import rs.raf.exbanka.mobile.domain.model.Card as CardModel
import rs.raf.exbanka.mobile.ui.components.ErrorView
import rs.raf.exbanka.mobile.ui.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    onBack: () -> Unit,
    onCardClick: (cardId: String, racunId: String, cardLabel: String, currency: String) -> Unit,
    viewModel: CardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val blockingId by viewModel.blockingId.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var unblockNoticeOpen by remember { mutableStateOf(false) }
    var cardToBlock by remember { mutableStateOf<CardModel?>(null) }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje kartice", style = MaterialTheme.typography.titleLarge) },
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
                    IconButton(onClick = { viewModel.loadCards() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Osveži")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is CardsUiState.Loading -> LoadingView()
                is CardsUiState.Error -> ErrorView(state.message, onRetry = { viewModel.loadCards() })
                is CardsUiState.Empty -> EmptyCardsView()
                is CardsUiState.Success -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        UnblockNotice(onMoreInfo = { unblockNoticeOpen = true })
                    }
                    items(state.cards, key = { it.id }) { card ->
                        CardListItem(
                            card = card,
                            isBlocking = blockingId == card.id,
                            onClick = {
                                onCardClick(
                                    card.id,
                                    card.racunId,
                                    "${card.tipKartice} ${card.maskiraniBroj}",
                                    "" // valuta će se učitati u CardTransactionsScreen iz račun-detalja
                                )
                            },
                            onBlock = { cardToBlock = card }
                        )
                    }
                }
            }
        }
    }

    if (unblockNoticeOpen) {
        AlertDialog(
            onDismissRequest = { unblockNoticeOpen = false },
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            title = { Text("Deblokiranje kartice") },
            text = {
                Text(
                    "Deblokiranje kartice nije dostupno iz mobilne aplikacije. " +
                        "Da biste odblokirali karticu, kontaktirajte službenika banke " +
                        "telefonom ili dođite lično u banku."
                )
            },
            confirmButton = {
                TextButton(onClick = { unblockNoticeOpen = false }) { Text("U redu") }
            }
        )
    }

    cardToBlock?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToBlock = null },
            title = { Text("Blokiraj karticu?") },
            text = {
                Text(
                    "Da li ste sigurni da želite da blokirate karticu ${card.maskiraniBroj}? " +
                        "Deblokiranje nije moguće iz aplikacije."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.blockCard(card.id)
                    cardToBlock = null
                }) { Text("Blokiraj") }
            },
            dismissButton = {
                TextButton(onClick = { cardToBlock = null }) { Text("Odustani") }
            }
        )
    }
}

@Composable
private fun UnblockNotice(onMoreInfo: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onMoreInfo() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Deblokiranje kartice nije dostupno iz aplikacije. Pozovite službenika ili dođite u banku.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CardListItem(
    card: CardModel,
    isBlocking: Boolean,
    onClick: () -> Unit,
    onBlock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = !isBlocking, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CreditCard, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.maskiraniBroj,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${card.tipKartice} • ${card.vrstaKartice}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(card.status)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Račun: ${card.nazivRacuna.ifBlank { card.brojRacuna }} (${card.brojRacuna})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (card.datumIsteka.isNotBlank()) {
                Text(
                    text = "Ističe: ${card.datumIsteka.take(10)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (card.isAktivna) {
                    Button(onClick = onBlock, enabled = !isBlocking) {
                        Text(if (isBlocking) "Blokiranje…" else "Blokiraj karticu")
                    }
                } else {
                    OutlinedButton(onClick = {}, enabled = false) {
                        Text("Blokiranje nedostupno")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val isAktivna = status.equals("AKTIVNA", ignoreCase = true)
    val bg = if (isAktivna) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.errorContainer
    val fg = if (isAktivna) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onErrorContainer
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
    ) {
        androidx.compose.material3.Surface(
            color = bg,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = fg,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun EmptyCardsView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CreditCard,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Nemate aktivnih kartica",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}
