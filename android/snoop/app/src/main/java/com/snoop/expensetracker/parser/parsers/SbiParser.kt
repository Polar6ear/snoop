package com.snoop.expensetracker.parser.parsers

import com.snoop.expensetracker.parser.ParsedTransaction
import com.snoop.expensetracker.parser.TransactionParser

/**
 * SBI. Two legitimate sources for this text, both reachable via NotificationListenerService
 * without ever requesting READ_SMS/RECEIVE_SMS (spec §4):
 *  1. The YONO SBI app's own UPI notifications.
 *  2. SBI's transaction SMS, as posted by the phone's *default SMS app* — reading the
 *     notification a messaging app posts is a normal NotificationListenerService read, not the
 *     restricted SMS-permission category, because the app itself never touches the SMS inbox.
 * `defaultSmsPackageName` is passed in per-device (from Telephony.Sms.getDefaultSmsPackage())
 * rather than hardcoded, since it varies (Google Messages, Samsung Messages, etc.).
 */
class SbiParser(private val defaultSmsPackageName: String?) : TransactionParser {
    override val bankKey = "SBI"

    companion object {
        const val YONO_PACKAGE_NAME = "com.sbi.yono"
    }

    override fun matches(packageName: String, text: String): Boolean {
        val fromKnownSource = packageName == YONO_PACKAGE_NAME || packageName == defaultSmsPackageName
        if (!fromKnownSource) return false
        return Regex("""-SBI\b""").containsMatchIn(text) ||
            Regex("""\bSBI\b""", RegexOption.IGNORE_CASE).containsMatchIn(text)
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
