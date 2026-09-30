package com.snoop.expensetracker.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class BudgetPeriod { MONTHLY }
// Only MONTHLY for MVP (spec §12 doesn't ask for weekly/custom periods). The enum exists so
// adding WEEKLY/CUSTOM later doesn't require a schema change, just a new enum value + migration.

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId", unique = true)]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** One active budget per category at a time (enforced by the unique index above). */
    val categoryId: Long,

    /** Integer minor units (paise). NEVER a float. */
    val limitMinor: Long,

    val period: BudgetPeriod,

    /** Epoch millis marking the start of the current period, used to compute "this month's spend". */
    val startDate: Long,

    /** Period start for which the 90% warning was already delivered. Null means not delivered. */
    val warningNotifiedPeriodStart: Long? = null,

    /** Period start for which the 100% warning was already delivered. Null means not delivered. */
    val overNotifiedPeriodStart: Long? = null
)
