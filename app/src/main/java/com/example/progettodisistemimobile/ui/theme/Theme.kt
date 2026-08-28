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
    secondary = SanremoGold,
    tertiary = LavenderDark,
    tertiaryContainer = LavenderContainer,
    background = DarkBlueBG,
    surface = DarkBlueBG,
    onPrimary = DarkBlueBG,
    onSecondary = DarkBlueBG,
    onTertiary = DarkBlueBG,
    onTertiaryContainer = LightBlueBG,
    onBackground = LightBlueBG,
    onSurface = LightBlueBG,
    outline = LavenderDark,
    outlineVariant = LavenderContainer
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonRed,
    secondary = SanremoGold,
    tertiary = AzureBlue,
    tertiaryContainer = AzureContainer,
    background = LightBlueBG,
    surface = LightBlueBG,
    onPrimary = LightBlueBG,
    onSecondary = DarkBlueBG,
    onTertiary = LightBlueBG,
    onTertiaryContainer = DarkBlueBG,
    onBackground = DarkBlueBG,
    onSurface = DarkBlueBG,
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
