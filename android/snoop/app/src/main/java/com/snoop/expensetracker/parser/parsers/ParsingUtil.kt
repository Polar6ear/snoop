package com.snoop.expensetracker.parser.parsers

import com.snoop.expensetracker.data.db.entities.TransactionDirection

/**
 * Shared helpers so each bank/app parser only has to define *which* regexes apply to it, not
 * re-implement amount-to-paise conversion five times (a classic place to accidentally introduce
 * a float and violate the money-handling rule).
 */
internal object ParsingUtil {

    private val amountRegex = Regex("""(?:Rs\.?|INR|₹)\s?([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
    private val trailingAmountRegex = Regex("""([\d,]+(?:\.\d{1,2})?)\s?(?:Rs\.?|INR|₹)(?![A-Za-z])""", RegexOption.IGNORE_CASE)
    private val transferWordsRegex = Regex(
        """\b(self[- ]?transfer|own account|between (?:my|your|the) accounts|to (?:my|your) (?:own )?account|from (?:my|your) (?:own )?account)\b""",
        RegexOption.IGNORE_CASE
    )
    private val creditWordsRegex = Regex("""\b(credited|received|deposited|refund(?:ed)?)\b""", RegexOption.IGNORE_CASE)
    // "payment" alone is neutral. "payment received" must be CREDIT, not DEBIT.
    private val debitWordsRegex = Regex("""\b(debited|paid|sent|spent|transferred|withdrawn|purchase(?:d)?)\b""", RegexOption.IGNORE_CASE)

    /** "₹1,250.00" / "Rs.500.00" / "INR 99" -> paise as Long, e.g. 125000L / 50000L / 9900L. Null if no amount found. */
    fun extractAmountMinor(text: String): Long? {
        val match = amountRegex.find(text) ?: trailingAmountRegex.find(text) ?: return null
        val raw = match.groupValues[1].replace(",", "").trimEnd('.')
        val rupees = raw.toDoubleOrNull() ?: return null
        // Single controlled float->long conversion at the parse boundary; every value downstream
        // of this point stays a Long. Rounding, not truncation, avoids a systematic 1-paise-low bias.
        return Math.round(rupees * 100.0)
    }

    /**
     * Returns null if neither a credit nor a debit keyword is found — caller should treat as unparseable.
     *
     * Bank SMS often mention BOTH words: "Rs.500 debited from A/c XX1234 and credited to VPA x@ybl".
     * From the account holder's point of view the FIRST verb describes their own account, so the
     * earliest keyword in the text wins instead of a fixed credit-before-debit priority.
     */
    fun extractDirection(text: String): TransactionDirection? {
        if (transferWordsRegex.containsMatchIn(text)) return TransactionDirection.TRANSFER
        val credit = creditWordsRegex.find(text)?.range?.first
        val debit = debitWordsRegex.find(text)?.range?.first
        return when {
            credit == null && debit == null -> null
            credit == null -> TransactionDirection.DEBIT
            debit == null -> TransactionDirection.CREDIT
            debit < credit -> TransactionDirection.DEBIT
            else -> TransactionDirection.CREDIT
        }
    }

    private val reminderRegex = Regex(
        """\b(due|overdue|reminder|remind|statement|bill (?:is|of)|will be debited|scheduled|mandate|request(?:ed)?|collect request|autopay (?:on|for))\b""",
        RegexOption.IGNORE_CASE
    )

    /** True for reminders/requests that mention an amount but are NOT a completed money movement. */
    fun looksLikeReminder(text: String): Boolean = reminderRegex.containsMatchIn(text)

    private val vpaRegex = Regex("""VPA\s+([\w.\-]+@[\w.\-]+)""", RegexOption.IGNORE_CASE)
    private val toNameRegex = Regex("""\bto\s+([A-Za-z0-9 .&'\-]+?)(?:\s+using|\s+via|\s+was|$)""", RegexOption.IGNORE_CASE)
    private val fromNameRegex = Regex("""\bfrom\s+([A-Za-z0-9 .&'\-]+?)(?:\s+on|\s+using|\s+via|$)""", RegexOption.IGNORE_CASE)

    /**
     * Bank-SMS-style texts carry a VPA ("VPA merchant@ybl"); prefer that when present since it's
     * the most stable identifier for the MerchantResolver to key on. Falls back to a plain-English
     * "to <name>" / "from <name>" capture for GPay/PhonePe-style app notifications.
     */
    fun extractMerchantRaw(text: String, direction: TransactionDirection?): String? {
        vpaRegex.find(text)?.let { return it.groupValues[1].trim().trimEnd('.') }
        val nameMatch = if (direction == TransactionDirection.CREDIT) {
            fromNameRegex.find(text)
        } else {
            toNameRegex.find(text)
        }
        return nameMatch?.groupValues?.get(1)?.trim()?.trimEnd('.')
    }
}
