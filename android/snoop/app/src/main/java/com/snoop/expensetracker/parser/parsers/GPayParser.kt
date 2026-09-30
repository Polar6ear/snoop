package com.snoop.expensetracker.parser.parsers

import com.snoop.expensetracker.parser.ParsedTransaction
import com.snoop.expensetracker.parser.TransactionParser

/**
 * Google Pay. Package name verified against the current Play Store listing at implementation
 * time — Google has renamed this package before, so if matches() silently stops firing, check
 * this string first before assuming the notification wording changed (spec §10 risk #3).
 */
class GPayParser : TransactionParser {
    override val bankKey = "GPAY"

    companion object {
        const val PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
    }

    override fun matches(packageName: String, text: String): Boolean {
        if (packageName != PACKAGE_NAME) return false
        return Regex("""(paid|received)""", RegexOption.IGNORE_CASE).containsMatchIn(text) &&
            Regex("""(Rs\.?|INR|₹)""", RegexOption.IGNORE_CASE).containsMatchIn(text)
    }

    override fun parse(text: String): ParsedTransaction? {
        val amountMinor = ParsingUtil.extractAmountMinor(text) ?: return null
        val direction = ParsingUtil.extractDirection(text) ?: return null
        val merchant = ParsingUtil.extractMerchantRaw(text, direction) ?: return null
        return ParsedTransaction(
            amountMinor = amountMinor,
            direction = direction,
            merchantRaw = merchant,
            confidence = 1.0f
        )
    }
}
