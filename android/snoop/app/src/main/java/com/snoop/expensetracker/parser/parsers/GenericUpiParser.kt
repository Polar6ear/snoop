package com.snoop.expensetracker.parser.parsers

import com.snoop.expensetracker.parser.ParsedTransaction
import com.snoop.expensetracker.parser.TransactionParser

/**
 * Last-resort deterministic parser for supported-looking UPI/bank notification text.
 * It makes Snoop useful beyond a hard-coded package list; AI remains the fallback when
 * amount/direction/merchant cannot be extracted locally.
 */
class GenericUpiParser : TransactionParser {
    override val bankKey: String = "GENERIC_UPI"

    override fun matches(packageName: String, text: String): Boolean {
        if (ParsingUtil.looksLikeReminder(text)) return false
        return ParsingUtil.extractAmountMinor(text) != null &&
            ParsingUtil.extractDirection(text) != null
    }

    override fun parse(text: String): ParsedTransaction? {
        val amount = ParsingUtil.extractAmountMinor(text) ?: return null
        val direction = ParsingUtil.extractDirection(text) ?: return null
        val merchant = ParsingUtil.extractMerchantRaw(text, direction) ?: return null
        return ParsedTransaction(amountMinor = amount, direction = direction, merchantRaw = merchant, confidence = 0.82f)
    }
}
