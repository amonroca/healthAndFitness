package com.cse310.healthandfitness.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF082F49),
    primaryContainer = Color(0xFF0F3C59),
    onPrimaryContainer = Color(0xFFD8F0FF),
    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF062E2A),
    secondaryContainer = Color(0xFF114B44),
    onSecondaryContainer = Color(0xFFD6FFF8),
    tertiary = Color(0xFF60A5FA),
    onTertiary = Color(0xFF0B1F3A),
    tertiaryContainer = Color(0xFF17345D),
    onTertiaryContainer = Color(0xFFDDEBFF),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = Color(0xFFEF4444),
    onError = Color(0xFFF8FAFC)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0EA5E9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD8F1FF),
    onPrimaryContainer = Color(0xFF083B59),
    secondary = Color(0xFF14B8A6),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD4F8F3),
    onSecondaryContainer = Color(0xFF0B3B35),
    tertiary = Color(0xFF2563EB),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDBEAFE),
    onTertiaryContainer = Color(0xFF1D3A8A),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE0F2FE),
    onSurfaceVariant = Color(0xFF334155),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun HealthAndFitnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
