package com.example.progettodisistemimobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VibrantPink,
    onPrimary = DarkBlueBG,
    secondary = SanremoGold,
    onSecondary = DarkBlueBG,
    tertiary = LavenderDark,
    onTertiary = DarkBlueBG,

    // Sfondo Sanremo e Barra di navigazione
    tertiaryContainer = LavenderContainer,
    onTertiaryContainer = LightBlueBG,
    surfaceContainer = LavenderContainer,

    // Elementi selezionati (pillola nav bar)
    secondaryContainer = VibrantPink,
    onSecondaryContainer = DarkBlueBG,

    background = DarkBlueBG,
    onBackground = LightBlueBG,
    surface = DarkBlueBG,
    onSurface = LightBlueBG,
    onSurfaceVariant = LavenderDark,
    outline = LavenderDark,
    outlineVariant = LavenderContainer
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonRed,
    onPrimary = LightBlueBG,
    secondary = SanremoGold,
    onSecondary = DarkBlueBG,
    tertiary = AzureBlue,
    onTertiary = LightBlueBG,

    // Sfondo Sanremo
    tertiaryContainer = AzureContainer,
    onTertiaryContainer = DarkBlueBG,
    surfaceContainer = AzureContainer,

    secondaryContainer = CrimsonRed,
    onSecondaryContainer = LightBlueBG,

    background = LightBlueBG,
    onBackground = DarkBlueBG,
    surface = LightBlueBG,
    onSurface = DarkBlueBG,
    onSurfaceVariant = AzureBlue,
    outline = AzureBlue,
    outlineVariant = AzureContainer
)

@Composable
fun ProgettoDiSistemiMobileTheme(
    themeMode: String = "Sistema",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "Chiaro" -> false
        "Scuro" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
