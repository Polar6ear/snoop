package com.snoop.expensetracker.ui.common

import com.snoop.expensetracker.data.db.entities.CategoryEntity

/** Supports both new emoji values and legacy Material-icon names from older Snoop databases. */
fun categoryEmoji(category: CategoryEntity?): String {
    if (category == null) return "•"
    val value = category.icon.trim()
    if (value.any { it.code > 127 }) return value
    return when (value.lowercase()) {
        "restaurant" -> "🍔"
        "directions_car" -> "🚕"
        "shopping_bag" -> "🛍️"
        "phone_android" -> "📱"
        "receipt_long" -> "🧾"
        "local_grocery_store" -> "🛒"
        "movie" -> "🎬"
        "local_hospital" -> "🩺"
        "home" -> "🏠"
        "people" -> "👥"
        "swap_horiz" -> "↔️"
        else -> "✨"
    }
}

val snoopEmojiChoices = listOf(
    "🍔", "🛒", "🚕", "🛍️", "💡", "📱", "🎬", "🩺",
    "🏠", "👥", "💸", "💰", "☕", "✈️", "🎮", "🐶",
    "🎓", "💻", "🧾", "✨", "❤️", "🏋️", "🎁", "📚"
)
