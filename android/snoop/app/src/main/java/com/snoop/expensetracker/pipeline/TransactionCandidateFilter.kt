package com.snoop.expensetracker.pipeline

/**
 * Very cheap, conservative gate used before invoking the network AI fallback.
 * It is deliberately not a parser: it only rejects obvious non-transaction notifications.
 */
object TransactionCandidateFilter {
    private val amountPattern = Regex("(?:₹|rs\\.?|inr)\\s*[0-9][0-9,]*(?:\\.[0-9]{1,2})?", RegexOption.IGNORE_CASE)
    private val trailingAmountPattern = Regex("[0-9][0-9,]*(?:\\.[0-9]{1,2})?\\s?(?:₹|rs\\.?|inr)(?![a-z])", RegexOption.IGNORE_CASE)
    private val financialPattern = Regex(
        "\\b(paid|payment|debited|debit|credited|credit|received|sent|transferred|transaction|upi|refund|reversed)\\b",
        RegexOption.IGNORE_CASE
    )

    private val unconditionalNonTransactionPattern = Regex(
        "\\b(otp|one[- ]time password|verification code|promo|promotion|offer|cashback offer|failed|declined|unsuccessful|cancelled|canceled)\\b",
        RegexOption.IGNORE_CASE
    )

    private val conditionalNonTransactionPattern = Regex(
        "\\b(login|sign[- ]in|security alert|balance is|available balance|account balance)\\b",
        RegexOption.IGNORE_CASE
    )

    fun looksLikeCandidate(text: String): Boolean {
        val normalized = text.trim()
        if (normalized.isEmpty()) return false
        if (unconditionalNonTransactionPattern.containsMatchIn(normalized)) return false

        val hasFinancialVerb = financialPattern.containsMatchIn(normalized)
        if (!hasFinancialVerb && conditionalNonTransactionPattern.containsMatchIn(normalized)) return false

        return (amountPattern.containsMatchIn(normalized) || trailingAmountPattern.containsMatchIn(normalized)) && hasFinancialVerb
    }
}
