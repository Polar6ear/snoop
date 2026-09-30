package com.snoop.expensetracker.parser

import com.snoop.expensetracker.parser.parsers.GPayParser
import com.snoop.expensetracker.parser.parsers.HdfcParser
import com.snoop.expensetracker.parser.parsers.PhonePeParser
import com.snoop.expensetracker.parser.parsers.SbiParser
import com.snoop.expensetracker.parser.parsers.GenericUpiParser

/**
 * Result of running the registry against one notification. `bankKey` is included alongside
 * the parse result so the caller can write [com.snoop.expensetracker.data.db.entities.TransactionEntity.bank]
 * and update `parser_versions` bookkeeping without re-deriving which parser matched.
 */
data class RegistryResult(
    val bankKey: String,
    val parsed: ParsedTransaction
)

/**
 * Tries every registered [TransactionParser] against an incoming notification, in order, and
 * returns the first match. Adding a new bank = adding one new parser to `parsers` below; the
 * rest of the pipeline (spec §8) never changes.
 *
 * `null` from [resolve] means: no parser recognized this app+text combination, and the caller
 * (NotificationListenerService's capture pipeline) should send the raw text to the backend for
 * the Gemini AI fallback (spec §5, §9).
 */
class ParserRegistry(defaultSmsPackageName: String?) {

    private val parsers: List<TransactionParser> = listOf(
        GPayParser(),
        PhonePeParser(),
        SbiParser(defaultSmsPackageName),
        HdfcParser(defaultSmsPackageName),
        GenericUpiParser()
    )

    fun resolve(packageName: String, text: String): RegistryResult? {
        for (parser in parsers) {
            if (!parser.matches(packageName, text)) continue
            val parsed = parser.parse(text) ?: continue // matched the app/keyword shape but regex extraction failed -> try next parser, then AI fallback
            return RegistryResult(bankKey = parser.bankKey, parsed = parsed)
        }
        return null
    }
}
