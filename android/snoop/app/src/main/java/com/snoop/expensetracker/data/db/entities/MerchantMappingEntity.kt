package com.snoop.expensetracker.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * The "remembers it" table (spec §7). Keyed on a normalized merchant/VPA string so the
 * MerchantResolver can look up "have we seen this before?" in O(1).
 */
@Entity(
    tableName = "merchant_mappings",
    primaryKeys = ["merchantKey"],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class MerchantMappingEntity(
    /** Normalized merchant identifier — lowercased VPA (e.g. "swiggy@ybl") or normalized display name. */
    val merchantKey: String,

    val categoryId: Long,

    /** Epoch millis of when the user first confirmed this mapping. */
    val learnedAt: Long
)
