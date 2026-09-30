package com.snoop.expensetracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = SnoopColors.Orange,
    onPrimary = SnoopColors.Paper,
    primaryContainer = SnoopColors.Coral,
    onPrimaryContainer = SnoopColors.Ink,
    secondary = SnoopColors.Teal,
    onSecondary = SnoopColors.Paper,
    background = SnoopColors.Paper,
    onBackground = SnoopColors.Ink,
    surface = SnoopColors.Paper,
    onSurface = SnoopColors.Ink,
    surfaceVariant = SnoopColors.PaperDim,
    onSurfaceVariant = SnoopColors.Stone,
    error = SnoopColors.OrangeDark,
    outline = SnoopColors.PaperDimmer
)

private val DarkColors = darkColorScheme(
    primary = SnoopColors.Coral,
    onPrimary = SnoopColors.Ink,
    primaryContainer = SnoopColors.OrangeDark,
    onPrimaryContainer = SnoopColors.Paper,
    secondary = SnoopColors.TealDark,
    onSecondary = SnoopColors.Paper,
    background = SnoopColors.Ink,
    onBackground = SnoopColors.Paper,
    surface = SnoopColors.Ink,
    onSurface = SnoopColors.Paper,
    surfaceVariant = Color(0xFF221F1A),
    onSurfaceVariant = SnoopColors.Stone,
    error = SnoopColors.Coral,
    outline = Color(0xFF33302A)
)

@Composable
fun SnoopTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = SnoopTypography,
        content = content
    )
}
