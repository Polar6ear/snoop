package com.snoop.expensetracker.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette pulled from the provided design mockup (Expense Tracker.dc.html): warm paper
 * background, near-black ink for text/dark surfaces, and a small set of saturated accents used
 * per-category so the category list reads as color-coded at a glance.
 */
object SnoopColors {
    val Ink = Color(0xFF14120F)          // near-black, used for dark surfaces & primary text
    val InkFaint = Color(0xFF8A7A3F)
    val Paper = Color(0xFFF4F0E8)        // warm off-white background
    val PaperDim = Color(0xFFE8E2D6)
    val PaperDimmer = Color(0xFFE4DDCE)

    val Orange = Color(0xFFF0522F)       // primary accent (CTAs, brand)
    val OrangeDark = Color(0xFFC33F1F)   // pressed/hover state
    val Coral = Color(0xFFFF8B6B)

    val Lime = Color(0xFFC9E265)         // positive / under-budget
    val Teal = Color(0xFF1F9C8C)
    val TealDark = Color(0xFF177A6D)
    val Purple = Color(0xFF6B5BF0)
    val Amber = Color(0xFFD99A2B)
    val Pink = Color(0xFFE0457B)
    val Blue = Color(0xFF3B8FE0)
    val Stone = Color(0xFF8A8478)        // muted secondary text

    /** Rotating palette for category chips/icons when a category doesn't have an explicit color. */
    val categoryPalette = listOf(Coral, Blue, Purple, Pink, Teal, Amber, Lime, Stone)
}
