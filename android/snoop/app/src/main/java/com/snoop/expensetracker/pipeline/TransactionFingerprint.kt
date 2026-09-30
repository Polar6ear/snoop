package com.snoop.expensetracker.pipeline

import com.snoop.expensetracker.data.db.entities.TransactionDirection
import java.security.MessageDigest

/**
 * Content-based dedupe key. Never uses StatusBarNotification.key as the sole dedupe signal.
 * Reference/UTR numbers are preferred; otherwise content is hashed into a 10-minute bucket.
 */
object TransactionFingerprint {
    const val BUCKET_MILLIS: Long = 10 * 60 * 1000L

    private val refNumberRegex = Regex(
        """(?:UPI\s+)?Ref(?:erence)?\s*(?:No\.?|Number|ID)?\s*[:.]?\s*(\d{6,})""",
        RegexOption.IGNORE_CASE
    )
    private val utrRegex = Regex("""\bUTR\s*[:.]?\s*(\w{6,})""", RegexOption.IGNORE_CASE)

    fun extractReferenceNumber(rawText: String): String? {
        refNumberRegex.find(rawText)?.let { return it.groupValues[1] }
        utrRegex.find(rawText)?.let { return it.groupValues[1] }
        return null
    }

    fun forTransaction(
        packageName: String,
        bank: String,
        amountMinor: Long,
        direction: TransactionDirection,
        merchantRawNormalized: String,
        rawText: String,
        nowMillis: Long
    ): String {
        extractReferenceNumber(rawText)?.let { ref -> return "REF:$bank:$ref" }
        val bucket = nowMillis / BUCKET_MILLIS
        val input = listOf(
            packageName, bank, amountMinor.toString(), direction.name,
            merchantRawNormalized, rawText.trim()
        ).joinToString("|")
        return "CONTENT:${sha256Hex(input)}:$bucket"
    }

    fun forPending(packageName: String, rawText: String, nowMillis: Long): String {
        extractReferenceNumber(rawText)?.let { ref -> return "REF_PENDING:$packageName:$ref" }
        // Manual-review items must not duplicate merely because the same notification is reposted
        // after the normal 10-minute transaction bucket. The exact content hash is intentionally
        // stable until the user resolves the item.
        val input = listOf(packageName, rawText.trim()).joinToString("|")
        return "PENDING:${sha256Hex(input)}"
    }

    private fun sha256Hex(input: String): String =
        MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}
