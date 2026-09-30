package com.snoop.expensetracker.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local retry queue for notification candidates that could not reach the AI fallback.
 * A unique dedupeKey makes retries and repeated notification callbacks idempotent.
 */
@Entity(
    tableName = "fallback_pending",
    indices = [Index(value = ["dedupeKey"], unique = true)]
)
data class FallbackPendingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dedupeKey: String,
    val packageName: String,
    val text: String,
    val createdAt: Long,
    val attempts: Int = 0
)
