package rs.raf.exbanka.mobile.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BankBlue,
    onPrimary = CardBackground,
    primaryContainer = BankBlueLight,
    onPrimaryContainer = CardBackground,
    secondary = BankAccent,
    onSecondary = CardBackground,
    background = SurfaceLight,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = DividerColor,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = CardBackground
)

private val DarkColorScheme = darkColorScheme(
    primary = BankBlueLightVariant,
    onPrimary = BankBlueDark,
    primaryContainer = BankBlue,
    onPrimaryContainer = CardBackground,
    secondary = BankAccent,
    onSecondary = BankBlueDark,
    background = SurfaceDark,
    onBackground = CardBackground,
    surface = CardBackgroundDark,
    onSurface = CardBackground,
    error = ErrorRed,
    onError = CardBackground
)

@Composable
fun EXBankaVerificationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
