package com.snoop.expensetracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The mockup uses "Bricolage Grotesque" (display/headlines) + "Instrument Sans" (body), both
 * Google Fonts. Wiring those in via Compose's Downloadable Fonts provider needs a signing-cert
 * hash array resource that's easy to get subtly wrong without a real device/Play Services to
 * test against — not something to guess at blind in this sandbox. Shipping with the platform
 * default font family + the mockup's weight choices (heavy display weight, regular body) now;
 * swapping `FontFamily.Default` for a `GoogleFont("Bricolage Grotesque")` FontFamily later is a
 * one-line change once you can verify it renders on a real device.
 */
private val displayFamily = FontFamily.Default
private val bodyFamily = FontFamily.Default

val SnoopTypography = Typography(
    displayLarge = TextStyle(fontFamily = displayFamily, fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, lineHeight = 40.sp),
    headlineLarge = TextStyle(fontFamily = displayFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontFamily = displayFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = displayFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp)
)
