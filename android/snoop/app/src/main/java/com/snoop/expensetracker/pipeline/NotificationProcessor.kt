package com.snoop.expensetracker.pipeline

import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.FallbackPendingEntity
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.data.db.entities.TransactionEntity
import com.snoop.expensetracker.data.db.entities.TransactionStatus
import com.snoop.expensetracker.data.repository.FallbackRepository
import com.snoop.expensetracker.data.repository.FallbackResult
import com.snoop.expensetracker.parser.ParsedTransaction
import com.snoop.expensetracker.parser.ParserRegistry
import com.snoop.expensetracker.security.SecurityPreferences
import com.snoop.expensetracker.work.FallbackRetryScheduler
import javax.inject.Inject
import javax.inject.Singleton

sealed class ProcessResult {
    data class AutoConfirmed(val transactionId: Long, val categoryId: Long) : ProcessResult()
    data class TransferConfirmed(val transactionId: Long) : ProcessResult()
    data class NeedsConfirmation(val transactionId: Long) : ProcessResult()
    data class NeedsManualEntry(val pendingId: Long) : ProcessResult()
    object Ignored : ProcessResult()
}

@Singleton
class NotificationProcessor @Inject constructor(
    private val parserRegistry: ParserRegistry,
    private val fallbackRepository: FallbackRepository,
    private val merchantResolver: MerchantResolver,
    private val transactionDao: TransactionDao,
    private val fallbackPendingDao: FallbackPendingDao,
    private val fallbackRetryScheduler: FallbackRetryScheduler,
    private val securityPreferences: SecurityPreferences,
    private val contactsResolver: ContactsResolver,
    private val categoryDao: CategoryDao,
    private val categoryClassifier: CategoryClassifier
) {
    companion object {
        private const val AI_CATEGORY_CONFIDENCE = 0.72f
    }

    suspend fun process(packageName: String, text: String): ProcessResult {
        if (!TransactionCandidateFilter.looksLikeCandidate(text)) return ProcessResult.Ignored

        val registryResult = parserRegistry.resolve(packageName, text)

        if (registryResult != null) {
            var parsed = registryResult.parsed

            // Local parsing knows the money movement. If it cannot categorize the merchant,
            // ask Gemini specifically for classification before asking the user.
            if (parsed.suggestedCategory == null &&
                merchantResolver.resolveCategory(parsed.merchantRaw) == null
            ) {
                when (val fallback = fallbackRepository.parse(packageName, text)) {
                    is FallbackResult.Success -> {
                        parsed = parsed.copy(
                            suggestedCategory = fallback.transaction.suggestedCategory,
                            suggestedCategoryConfidence = fallback.transaction.suggestedCategoryConfidence
                        )
                    }
                    is FallbackResult.RetryableFailure -> {
                        // We still have a valid amount/direction/merchant locally. Save it as
                        // pending instead of losing the transaction.
                    }
                    is FallbackResult.NotParseable -> Unit
                }
            }

            return saveParsedTransaction(
                packageName, text, registryResult.bankKey, parsed
            )
        }

        return when (val fallback = fallbackRepository.parse(packageName, text)) {
            is FallbackResult.Success -> saveParsedTransaction(
                packageName, text, "AI_FALLBACK", fallback.transaction
            )
            is FallbackResult.NotParseable -> {
                // The notification looked financial but neither the local parser nor AI could
                // safely extract it. Park it for an explicit user-filled transaction instead of
                // silently dropping money movement. attempts == MAX_ATTEMPTS marks manual review
                // so WorkManager does not keep retrying a non-parseable notification.
                val pendingId = fallbackPendingDao.insert(
                    FallbackPendingEntity(
                        dedupeKey = TransactionFingerprint.forPending(packageName, text, System.currentTimeMillis()),
                        packageName = packageName,
                        text = text,
                        createdAt = System.currentTimeMillis(),
                        attempts = FallbackPendingDao.MAX_ATTEMPTS
                    )
                )
                if (pendingId == -1L) ProcessResult.Ignored else ProcessResult.NeedsManualEntry(pendingId)
            }
            is FallbackResult.RetryableFailure -> {
                val pendingId = fallbackPendingDao.insert(
                    FallbackPendingEntity(
                        dedupeKey = TransactionFingerprint.forPending(packageName, text, System.currentTimeMillis()),
                        packageName = packageName,
                        text = text,
                        createdAt = System.currentTimeMillis()
                    )
                )
                fallbackRetryScheduler.schedule()
                if (pendingId == -1L) ProcessResult.Ignored else ProcessResult.NeedsManualEntry(pendingId)
            }
        }
    }

    suspend fun retryPending(item: FallbackPendingEntity): ProcessResult {
        return when (val fallback = fallbackRepository.parse(item.packageName, item.text)) {
            is FallbackResult.Success -> {
                val result = saveParsedTransaction(
                    item.packageName, item.text, "AI_FALLBACK", fallback.transaction
                )
                fallbackPendingDao.delete(item.id)
                result
            }
            is FallbackResult.NotParseable -> {
                fallbackPendingDao.parkForManualReview(item.id)
                ProcessResult.NeedsManualEntry(item.id)
            }
            is FallbackResult.RetryableFailure -> {
                fallbackPendingDao.incrementAttempts(item.id)
                ProcessResult.Ignored
            }
        }
    }

    private suspend fun saveParsedTransaction(
        packageName: String,
        text: String,
        bank: String,
        parsed: ParsedTransaction
    ): ProcessResult {
        val now = System.currentTimeMillis()
        val canonicalKey = TransactionFingerprint.forTransaction(
            packageName = packageName,
            bank = bank,
            amountMinor = parsed.amountMinor,
            direction = parsed.direction,
            merchantRawNormalized = merchantResolver.normalize(parsed.merchantRaw),
            rawText = text,
            nowMillis = now
        )
        val learnedCategoryId = merchantResolver.resolveCategory(parsed.merchantRaw)

        val contactName =
            if (learnedCategoryId == null && parsed.direction == TransactionDirection.DEBIT) {
                contactsResolver.findMatchingContact(parsed.merchantRaw)
            } else null

        val contactCategoryId =
            if (contactName != null) categoryDao.getIdByName("Friends & Family") else null

        val localCategoryId =
            if (learnedCategoryId == null && contactCategoryId == null) {
                categoryClassifier.classify(parsed.merchantRaw)
            } else null

        val aiCategoryId =
            if (learnedCategoryId == null &&
                contactCategoryId == null &&
                localCategoryId == null &&
                parsed.suggestedCategoryConfidence >= AI_CATEGORY_CONFIDENCE
            ) {
                categoryClassifier.categoryByName(parsed.suggestedCategory)
            } else null

        val knownCategoryId =
            learnedCategoryId ?: contactCategoryId ?: localCategoryId ?: aiCategoryId

        val status =
            if (parsed.direction == TransactionDirection.TRANSFER || knownCategoryId != null) {
                TransactionStatus.CONFIRMED
            } else {
                TransactionStatus.PENDING
            }

        val entity = TransactionEntity(
            amountMinor = parsed.amountMinor,
            direction = parsed.direction,
            merchantRaw = parsed.merchantRaw,
            merchantResolved = when {
                contactName != null -> contactName
                knownCategoryId != null -> parsed.merchantRaw
                else -> null
            },
            categoryId = knownCategoryId,
            sourceApp = packageName,
            bank = bank,
            rawText = if (securityPreferences.minimizeRawData) "" else text,
            confidence = parsed.confidence,
            status = status,
            dedupeKey = canonicalKey,
            createdAt = now,
            suggestedCategory = if (status == TransactionStatus.PENDING) parsed.suggestedCategory else null,
            suggestedCategoryConfidence = if (status == TransactionStatus.PENDING) parsed.suggestedCategoryConfidence else 0f
        )

        val existingByCanonicalKey = transactionDao.findIdByDedupeKey(canonicalKey)
        if (existingByCanonicalKey != null) {
            return if (knownCategoryId != null || parsed.direction == TransactionDirection.TRANSFER) {
                if (parsed.direction == TransactionDirection.TRANSFER) ProcessResult.TransferConfirmed(existingByCanonicalKey) else ProcessResult.AutoConfirmed(existingByCanonicalKey, knownCategoryId!!)
            } else {
                ProcessResult.NeedsConfirmation(existingByCanonicalKey)
            }
        }

        val insertedId = transactionDao.insert(entity)
        if (insertedId == -1L) {
            val existingId = transactionDao.findIdByDedupeKey(canonicalKey) ?: return ProcessResult.Ignored
            return if (knownCategoryId != null || parsed.direction == TransactionDirection.TRANSFER) {
                if (parsed.direction == TransactionDirection.TRANSFER) ProcessResult.TransferConfirmed(existingId) else ProcessResult.AutoConfirmed(existingId, knownCategoryId!!)
            } else {
                ProcessResult.NeedsConfirmation(existingId)
            }
        }

        return if (knownCategoryId != null || parsed.direction == TransactionDirection.TRANSFER) {
            if (parsed.direction == TransactionDirection.TRANSFER) ProcessResult.TransferConfirmed(insertedId) else ProcessResult.AutoConfirmed(insertedId, knownCategoryId!!)
        } else {
            ProcessResult.NeedsConfirmation(insertedId)
        }
    }
}
