package com.snoop.expensetracker.ui.common

import kotlin.math.abs

/**
 * Single source of truth for showing money in the UI.
 * - Input is always paise (Long) — never a float.
 * - Indian digit grouping: 1428604 rupees -> 14,28,604
 * - Paise shown only when non-zero, so ₹500 stays "₹500" but ₹499.50 stays "₹499.50".
 */
fun formatRupees(minor: Long, withSymbol: Boolean = true): String {
    val negative = minor < 0
    val absMinor = abs(minor)
    val rupees = absMinor / 100
    val paise = (absMinor % 100).toInt()

    val digits = rupees.toString()
    val grouped = if (digits.length <= 3) {
        digits
    } else {
        val last3 = digits.takeLast(3)
        val rest = digits.dropLast(3)
        val restGrouped = rest.reversed().chunked(2).joinToString(",").reversed()
        "$restGrouped,$last3"
    }
    val body = if (paise == 0) grouped else "$grouped.${paise.toString().padStart(2, '0')}"
    return (if (negative) "−" else "") + (if (withSymbol) "₹" else "") + body
}
