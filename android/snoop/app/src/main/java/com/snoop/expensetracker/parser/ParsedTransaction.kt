package com.snoop.expensetracker.parser

import com.snoop.expensetracker.data.db.entities.TransactionDirection

/**
 * Output of a successful [TransactionParser.parse] call, or of the AI-fallback backend call.
 * Amount is always integer minor units (paise) — never a float/double, per the money-handling
 * rule. A parser is responsible for converting "₹1,250.00" -> 125000L paise itself.
 */
data class ParsedTransaction(
    val amountMinor: Long,
    val direction: TransactionDirection,
    /** Raw merchant/VPA string exactly as extracted — normalization happens in MerchantResolver. */
    val merchantRaw: String,
    /** Optional category suggested by a classifier/AI. Local parsers leave this null. */
    val suggestedCategory: String? = null,
    val suggestedCategoryConfidence: Float = 0f,
    /** 1.0 for a clean regex match. AI-fallback fills this from the model's own reported confidence. */
    val confidence: Float
)
