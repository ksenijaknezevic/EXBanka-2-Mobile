package rs.raf.exbanka.mobile.ui.screens.funddetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.exbanka.mobile.domain.model.FundPerformancePoint
import rs.raf.exbanka.mobile.ui.components.ErrorView
import rs.raf.exbanka.mobile.ui.components.LoadingView
import rs.raf.exbanka.mobile.ui.screens.funds.formatRsd
import rs.raf.exbanka.mobile.ui.theme.BankAccent
import rs.raf.exbanka.mobile.ui.theme.BankBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundDetailScreen(
    fundId: String,
    fundName: String,
    onBack: () -> Unit,
    viewModel: FundDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(fundId) {
        viewModel.load(fundId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fundName.ifBlank { "Detalji fonda" }) },
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
                uiState.isLoading -> LoadingView()
                uiState.error != null -> ErrorView(uiState.error!!, onRetry = { viewModel.load(fundId) })
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        PeriodSelector(
                            selected = uiState.period,
                            onSelect = viewModel::setPeriod
                        )
                    }
                    item {
                        ChartCard(points = uiState.points)
                    }
                    item {
                        SummaryCard(points = uiState.points, period = uiState.period)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(selected: PerformancePeriod, onSelect: (PerformancePeriod) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PerformancePeriod.entries.forEach { p ->
            FilterChip(
                selected = selected == p,
                onClick = { onSelect(p) },
                label = { Text(p.label) }
            )
        }
    }
}

@Composable
private fun ChartCard(points: List<FundPerformancePoint>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    "Vrednost fonda kroz vreme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(12.dp))
            when {
                points.isEmpty() -> EmptyChartMessage("Istorijski podaci još nisu dostupni.")
                points.size < 2 -> EmptyChartMessage("Premalo tačaka za prikaz grafikona. Trenutna vrednost: ${formatRsd(points.first().value)}")
                else -> LineChart(points = points)
            }

            if (points.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        points.first().period,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        points.last().period,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LineChart(points: List<FundPerformancePoint>) {
    val values = points.map { it.value }
    val minV = values.min()
    val maxV = values.max()
    val range = (maxV - minV).takeIf { it > 0.0 } ?: 1.0

    val lineColor = BankBlue
    val fillColor = BankAccent.copy(alpha = 0.18f)
    val gridColor = Color.Gray.copy(alpha = 0.25f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    ) {
        val w = size.width
        val h = size.height

        // gridline-ovi
        val gridLines = 4
        repeat(gridLines + 1) { i ->
            val y = h * i / gridLines
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
        }

        val stepX = if (points.size > 1) w / (points.size - 1) else w
        val mapY = { v: Double ->
            val norm = (v - minV) / range
            (h - (norm * h * 0.92) - h * 0.04).toFloat()
        }

        // ispuna ispod linije
        val fillPath = Path().apply {
            moveTo(0f, h)
            points.forEachIndexed { i, p ->
                val x = i * stepX
                val y = mapY(p.value)
                if (i == 0) lineTo(x, y) else lineTo(x, y)
            }
            lineTo((points.size - 1) * stepX, h)
            close()
        }
        drawPath(path = fillPath, color = fillColor)

        // glavna linija
        val linePath = Path().apply {
            points.forEachIndexed { i, p ->
                val x = i * stepX
                val y = mapY(p.value)
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 4f)
        )

        // tačke
        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = mapY(p.value)
            drawCircle(color = lineColor, radius = 5f, center = Offset(x, y))
        }
    }
}

@Composable
private fun EmptyChartMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SummaryCard(points: List<FundPerformancePoint>, period: PerformancePeriod) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Pregled (${period.label.lowercase()})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            if (points.isEmpty()) {
                Text(
                    "Nema podataka za odabrani period.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Početak", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatRsd(points.first().value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kraj", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatRsd(points.last().value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Najniža", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatRsd(points.minByOrNull { it.value }!!.value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Najviša", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatRsd(points.maxByOrNull { it.value }!!.value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
