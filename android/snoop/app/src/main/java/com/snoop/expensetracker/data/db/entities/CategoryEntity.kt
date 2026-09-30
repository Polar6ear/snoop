package com.snoop.expensetracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    /** Emoji chosen for this category. Legacy installs may still contain old Material icon names; the UI maps them. */
    val icon: String,

    /** Hex ARGB, e.g. "#FFE0B2". */
    val color: String,

    /** false for the seeded default set (Food, Transport, Shopping, Skincare, Bills, ...), true for user-created. */
    val isCustom: Boolean
)

/** Seeded on first launch (spec §11: one universal default set, no age-segmented presets). */
object DefaultCategories {
    data class Seed(val name: String, val icon: String, val color: String)

    val seeds = listOf(
        Seed("Food", "🍔", "#FF8A65"),
        Seed("Transport", "🚕", "#4FC3F7"),
        Seed("Shopping", "🛍️", "#BA68C8"),
        Seed("Recharge", "📱", "#F06292"),
        Seed("Bills", "🧾", "#81C784"),
        Seed("Groceries", "🛒", "#AED581"),
        Seed("Entertainment", "🎬", "#FFD54F"),
        Seed("Health", "🩺", "#E57373"),
        Seed("Rent", "🏠", "#90A4AE"),
        Seed("Friends & Family", "👥", "#7986CB"),
        Seed("Transfers", "↔️", "#A1887F"),
        Seed("Other", "✨", "#9E9E9E")
    )
}
