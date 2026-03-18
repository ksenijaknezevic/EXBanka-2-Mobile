package rs.raf.exbanka.mobile.ui.screens.verificationcode

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.exbanka.mobile.ui.theme.BankBlue
import rs.raf.exbanka.mobile.ui.theme.ErrorRed
import rs.raf.exbanka.mobile.ui.theme.SuccessGreen
import rs.raf.exbanka.mobile.ui.theme.WarningAmber

@Composable
fun VerificationCodeScreen(
    code: String,
    expiresInSeconds: Int,
    onDone: () -> Unit,
    viewModel: VerificationCodeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(code, expiresInSeconds) {
        viewModel.initialize(code, expiresInSeconds)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "Transaction Approved!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            // ── Verification code card ─────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BankBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your Verification Code",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.5.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    // Code digits – split with a space in the middle for readability
                    val formattedCode = if (uiState.code.length == 6) {
                        "${uiState.code.take(3)} ${uiState.code.drop(3)}"
                    } else uiState.code

                    Text(
                        text = formattedCode,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 52.sp,
                            letterSpacing = 6.sp
                        ),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(16.dp))

                    // Countdown timer
                    val timerColor = when {
                        uiState.isExpired -> ErrorRed
                        uiState.remainingSeconds <= 60 -> WarningAmber
                        else -> SuccessGreen
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = timerColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (uiState.isExpired) {
                                "Code expired"
                            } else {
                                "Expires in ${uiState.remainingSeconds.toMinutesSeconds()}"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = timerColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Instruction card ──────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.LaptopMac,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Enter this code on your laptop",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Switch to your laptop/browser where you initiated the payment and type the code above to confirm the transaction.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Expiry warning
            if (uiState.isExpired) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "This code has expired. The transaction was not completed. Please go back and try again.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ErrorRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Attempt limit info
                Text(
                    text = "Note: After 3 incorrect attempts on the laptop, the transaction will be automatically cancelled.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1f))

            // Done / Back to transactions button
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Back to Transactions",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
