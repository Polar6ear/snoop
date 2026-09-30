package com.snoop.expensetracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The user's manually confirmed current balance.
 *
 * Snoop never changes this anchor when a transaction arrives. Instead it calculates:
 * anchor balance + observed credits - observed debits after the anchor timestamp.
 * Updating the balance simply moves the anchor forward to "now", so the user can correct
 * missed bank activity without rewriting transaction history.
 */
@Entity(tableName = "balance_anchor")
data class BalanceAnchorEntity(
    @PrimaryKey val id: Int = 1,
    val balanceMinor: Long,
    val anchoredAt: Long
)
