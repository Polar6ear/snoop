package com.snoop.expensetracker.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionDirection { DEBIT, CREDIT, TRANSFER }

enum class TransactionStatus { PENDING, CONFIRMED }

/**
 * Source app the notification/SMS came from. Kept as a plain string column (not enum)
 * so a new bank/app can be added via a new [com.snoop.expensetracker.parser.TransactionParser]
 * without a schema migration for the enum itself.
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("categoryId"),
        Index("status"),
        Index("createdAt"),
        Index("merchantResolved"),
        Index("deletedAt"),
        Index(value = ["dedupeKey"], unique = true)
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Always integer minor units (paise). NEVER a float/double — see money-handling rule. */
    val amountMinor: Long,

    val direction: TransactionDirection,

    /** Raw merchant/VPA string as extracted from the notification, before resolution. */
    val merchantRaw: String,

    /** Merchant name after MerchantResolver has normalized/matched it. Null until resolved. */
    val merchantResolved: String?,

    /** Null while status == PENDING and the user hasn't picked a category yet. */
    val categoryId: Long?,

    /** Package name of the app that posted the notification, e.g. "com.google.android.apps.nbu.paisa.user" */
    val sourceApp: String,

    /** Which bank/UPI handle this maps to for parser bookkeeping, e.g. "GPAY", "PHONEPE", "SBI", "HDFC" */
    val bank: String,

    /** Full original notification text, kept for debugging parser drift and for the AI fallback audit trail. */
    val rawText: String,

    /** 0.0–1.0. 1.0 for a clean regex match, lower for AI-fallback extractions per the model's own confidence. */
    val confidence: Float,

    val status: TransactionStatus,

    /**
     * Stable notification identity. Null for manual entries. A unique nullable index means
     * notification reposts/retries cannot create the same transaction twice, while manual
     * entries remain independent.
     */
    val dedupeKey: String? = null,

    /** Soft-delete timestamp. Null means visible. */
    val deletedAt: Long? = null,

    /** Epoch millis. */
    val createdAt: Long,

    /** Optional user note for context, e.g. "office lunch" or "reimbursable". */
    val note: String? = null,

    /** AI suggestion retained when confidence is below the auto-confirm threshold. */
    val suggestedCategory: String? = null,
    val suggestedCategoryConfidence: Float = 0f
)
