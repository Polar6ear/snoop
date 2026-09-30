package com.snoop.expensetracker.pipeline

import com.snoop.expensetracker.data.db.entities.TransactionDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TransactionFingerprintTest {
    @Test fun `same repost within the time bucket produces the same key`() {
        val now = 1_700_000_000_000L
        val text = "You paid Rs.249.00 to Swiggy"
        val key1 = TransactionFingerprint.forTransaction("com.google.android.apps.nbu.paisa.user", "GPAY", 24900L, TransactionDirection.DEBIT, "swiggy", text, now)
        val key2 = TransactionFingerprint.forTransaction("com.google.android.apps.nbu.paisa.user", "GPAY", 24900L, TransactionDirection.DEBIT, "swiggy", text, now + 5_000L)
        assertEquals(key1, key2)
    }
    @Test fun `separate purchase well outside the time bucket produces a different key`() {
        val now = 1_700_000_000_000L
        val text = "You paid Rs.249.00 to Swiggy"
        val key1 = TransactionFingerprint.forTransaction("com.google.android.apps.nbu.paisa.user", "GPAY", 24900L, TransactionDirection.DEBIT, "swiggy", text, now)
        val key2 = TransactionFingerprint.forTransaction("com.google.android.apps.nbu.paisa.user", "GPAY", 24900L, TransactionDirection.DEBIT, "swiggy", text, now + TransactionFingerprint.BUCKET_MILLIS * 2)
        assertNotEquals(key1, key2)
    }
    @Test fun `a bank reference number produces a time-independent key`() {
        val text = "Rs.500.00 debited from A/c XX1234 to VPA swiggy@ybl. Ref No 123456789012 -SBI"
        val now = 1_700_000_000_000L
        val muchLater = now + 1000L * 60 * 60 * 24 * 3
        val key1 = TransactionFingerprint.forTransaction("com.google.android.apps.messaging", "SBI", 50000L, TransactionDirection.DEBIT, "swiggy@ybl", text, now)
        val key2 = TransactionFingerprint.forTransaction("com.google.android.apps.messaging", "SBI", 50000L, TransactionDirection.DEBIT, "swiggy@ybl", text, muchLater)
        assertEquals(key1, key2)
        assertEquals("REF:SBI:123456789012", key1)
    }
}
