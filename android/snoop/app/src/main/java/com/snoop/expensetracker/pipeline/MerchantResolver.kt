package com.snoop.expensetracker.pipeline

import com.snoop.expensetracker.data.db.dao.MerchantMappingDao
import com.snoop.expensetracker.data.db.entities.MerchantMappingEntity
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Spec §5: "Merchant Resolver: has this merchant/VPA been categorized before?" — yes -> auto-apply
 * that category; no -> surface a Pending card. Also owns the "remembers it" write path: once the
 * user taps a category on the confirm popup, [learn] is called so the same merchant auto-applies
 * next time.
 */
@Singleton
class MerchantResolver @Inject constructor(
    private val merchantMappingDao: MerchantMappingDao
) {
    /** Normalizes a raw merchant/VPA string into the lookup key: lowercase, trimmed, collapsed whitespace. */
    fun normalize(merchantRaw: String): String =
        merchantRaw.trim().lowercase(Locale.ROOT).replace(Regex("\\s+"), " ")

    /** Returns the categoryId this merchant was last confirmed under, or null if we've never seen it. */
    suspend fun resolveCategory(merchantRaw: String): Long? {
        val key = normalize(merchantRaw)
        return merchantMappingDao.getByMerchantKey(key)?.categoryId
    }

    /** Called when the user confirms/corrects a category for a merchant — this is the "remembers it" write. */
    suspend fun learn(merchantRaw: String, categoryId: Long) {
        val key = normalize(merchantRaw)
        merchantMappingDao.upsert(
            MerchantMappingEntity(
                merchantKey = key,
                categoryId = categoryId,
                learnedAt = System.currentTimeMillis()
            )
        )
    }
}
