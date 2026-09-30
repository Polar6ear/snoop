package com.snoop.expensetracker.parser

/**
 * One implementation per bank/app (GPay, PhonePe, SBI, HDFC, ...). Adding a new bank means
 * adding one new file implementing this interface and registering it in [ParserRegistry] —
 * the rest of the pipeline is untouched. Interface shape is exactly spec §8.
 */
interface TransactionParser {
    /** A unique key for this parser, used for the transactions.bank column and parser_versions bookkeeping. */
    val bankKey: String

    fun matches(packageName: String, text: String): Boolean
    fun parse(text: String): ParsedTransaction?
}
