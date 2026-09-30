package com.snoop.expensetracker.parser.parsers

import com.snoop.expensetracker.parser.ParsedTransaction
import com.snoop.expensetracker.parser.TransactionParser

class PhonePeParser : TransactionParser {
    override val bankKey = "PHONEPE"

    companion object {
        const val PACKAGE_NAME = "com.phonepe.app"
    }

    override fun matches(packageName: String, text: String): Boolean {
        if (packageName != PACKAGE_NAME) return false
        return Regex("""(paid|payment|received)""", RegexOption.IGNORE_CASE).containsMatchIn(text) &&
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
