package com.snoop.expensetracker.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class ParseNotificationRequest(
    @Json(name = "package_name") val packageName: String,
    @Json(name = "text") val text: String,
    @Json(name = "categories") val categories: List<String> = emptyList()
)

/**
 * Mirrors the backend's response shape exactly (snoop-backend/app/schemas.py `ParseTransactionResponse`).
 * `parseable == false` when the backend/model genuinely couldn't extract a transaction from the
 * text (e.g. it was a promo/OTP notification, not a payment) — the app must treat that the same
 * as "leave this notification alone", not crash or show a garbage pending card.
 */
@JsonClass(generateAdapter = true)
data class ParseNotificationResponse(
    @Json(name = "parseable") val parseable: Boolean,
    @Json(name = "amount_minor") val amountMinor: Long?,
    @Json(name = "direction") val direction: String?, // "DEBIT" | "CREDIT"
    @Json(name = "merchant") val merchant: String?,
    @Json(name = "category") val category: String?,
    @Json(name = "confidence") val confidence: Float?,
    @Json(name = "error") val error: String?
)

interface FallbackApi {
    // Must match snoop-backend/app/main.py exactly: POST /v1/parse-transaction
    @POST("/v1/parse-transaction")
    suspend fun parseNotification(@Body request: ParseNotificationRequest): ParseNotificationResponse
}
