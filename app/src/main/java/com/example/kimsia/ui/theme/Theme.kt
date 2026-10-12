package com.example.kimsia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KimsiaColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    primaryContainer = EmeraldSoft,
    onPrimaryContainer = EmeraldDeep,
    secondary = Terracotta,
    onSecondary = Color.White,
    tertiary = SunYellow,
    onTertiary = Ink,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
)

@Composable
fun KimsiaTheme(content: @Composable () -> Unit) {
    // Solo tema claro (fondo crema); modo oscuro queda para una fase posterior.
    MaterialTheme(
        colorScheme = KimsiaColorScheme,
        typography = Typography(),
        content = content,
    )
}
