package com.snoop.expensetracker.parser.parsers

import com.snoop.expensetracker.parser.ParsedTransaction
import com.snoop.expensetracker.parser.TransactionParser

/** HDFC Bank. Same dual-source reasoning as [SbiParser] — bank app notification or default SMS app notification, both via NotificationListenerService. */
class HdfcParser(private val defaultSmsPackageName: String?) : TransactionParser {
    override val bankKey = "HDFC"

    companion object {
        const val BANK_APP_PACKAGE_NAME = "com.snapwork.hdfc"
    }

    override fun matches(packageName: String, text: String): Boolean {
        val fromKnownSource = packageName == BANK_APP_PACKAGE_NAME || packageName == defaultSmsPackageName
        if (!fromKnownSource) return false
        return Regex("""\bHDFC\b""", RegexOption.IGNORE_CASE).containsMatchIn(text)
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
