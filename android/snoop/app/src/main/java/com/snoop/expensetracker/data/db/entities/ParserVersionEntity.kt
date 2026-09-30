package com.snoop.expensetracker.data.db.entities

import androidx.room.Entity

/**
 * Tracks per-bank parser format-drift (spec §10 risk #3). Every time a [TransactionEntity]
 * falls through to the AI fallback for a bank that used to match reliably, the app bumps a
 * local drift counter against this row so it's visible instead of failing silently. The
 * `version` here is the app's own parser-implementation version (bumped when a parser's regex
 * is updated to follow a bank's changed wording), not a value read from the bank.
 */
@Entity(tableName = "parser_versions", primaryKeys = ["bankKey"])
data class ParserVersionEntity(
    /** e.g. "GPAY", "PHONEPE", "SBI", "HDFC" — matches [TransactionEntity.bank]. */
    val bankKey: String,

    /** Incremented whenever that bank's parser regex is changed to follow a wording change. */
    val version: Int,

    /** Epoch millis of the last time this parser's regex was updated. */
    val lastUpdated: Long
)
