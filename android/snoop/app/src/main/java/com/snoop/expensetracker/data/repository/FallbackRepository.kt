package com.snoop.expensetracker.data.repository

import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.network.FallbackApi
import com.snoop.expensetracker.data.network.ParseNotificationRequest
import com.snoop.expensetracker.parser.ParsedTransaction
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed interface FallbackResult {
    data class Success(val transaction: ParsedTransaction) : FallbackResult
    data class NotParseable(val reason: String? = null) : FallbackResult
    data class RetryableFailure(val reason: String) : FallbackResult
}

@Singleton
class FallbackRepository @Inject constructor(
    private val api: FallbackApi,
    private val categoryDao: CategoryDao
) {
    suspend fun parse(packageName: String, text: String): FallbackResult {
        return try {
            val categories = categoryDao.getAllOnce().map { it.name }
            val response = api.parseNotification(ParseNotificationRequest(packageName, text, categories))
            if (!response.parseable) return FallbackResult.NotParseable(response.error)

            val amountMinor = response.amountMinor?.takeIf { it > 0 } ?: return FallbackResult.NotParseable("missing/invalid amount")
            val direction = when (response.direction) {
                "DEBIT" -> TransactionDirection.DEBIT
                "CREDIT" -> TransactionDirection.CREDIT
                "TRANSFER" -> TransactionDirection.TRANSFER
                else -> return FallbackResult.NotParseable("invalid direction")
            }
            val merchant = response.merchant?.trim()?.takeIf { it.isNotEmpty() }
                ?: return FallbackResult.NotParseable("missing merchant")
            val confidence = (response.confidence ?: 0.5f).coerceIn(0f, 1f)

            FallbackResult.Success(
                ParsedTransaction(
                    amountMinor = amountMinor,
                    direction = direction,
                    merchantRaw = merchant,
                    suggestedCategory = response.category?.trim()?.takeIf { it.isNotEmpty() },
                    suggestedCategoryConfidence = confidence,
                    confidence = confidence
                )
            )
        } catch (e: IOException) {
            FallbackResult.RetryableFailure(e.message ?: "network failure")
        } catch (e: Exception) {
            // Retrofit/HTTP/schema failures may be transient; keep the notification locally rather
            // than silently dropping a legitimate candidate.
            FallbackResult.RetryableFailure(e.message ?: "fallback failure")
        }
    }
}
