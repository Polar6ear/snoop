package com.snoop.expensetracker.parser

import com.snoop.expensetracker.data.db.entities.TransactionDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pure-JVM tests — no Android framework calls anywhere in the parser package, so these run as
 * ordinary local unit tests (`./gradlew testDebugUnitTest`), no emulator/Robolectric needed.
 *
 * Sample notification texts below match real-world GPay/PhonePe/SBI/HDFC wording patterns.
 * The underlying regex logic (amount/direction/merchant extraction) was sanity-checked against
 * these exact strings with Python's `re` module before being ported to Kotlin `Regex`, since
 * this sandbox has no Android/Kotlin compiler to run the actual test suite against.
 */
class ParserRegistryTest {

    private val registry = ParserRegistry(defaultSmsPackageName = "com.google.android.apps.messaging")

    // ---- GPay ----

    @Test
    fun `gpay debit - paid to merchant`() {
        val result = registry.resolve(
            "com.google.android.apps.nbu.paisa.user",
            "You paid ₹249.00 to Swiggy"
        )
        assertNotNull(result)
        assertEquals("GPAY", result!!.bankKey)
        assertEquals(24900L, result.parsed.amountMinor)
        assertEquals(TransactionDirection.DEBIT, result.parsed.direction)
        assertEquals("Swiggy", result.parsed.merchantRaw)
    }

    @Test
    fun `gpay debit - amount-first phrasing with comma thousands separator`() {
        val result = registry.resolve(
            "com.google.android.apps.nbu.paisa.user",
            "₹1,250.00 paid to Amit Kumar using Bank Account"
        )
        assertNotNull(result)
        assertEquals(125000L, result!!.parsed.amountMinor)
        assertEquals(TransactionDirection.DEBIT, result.parsed.direction)
        assertEquals("Amit Kumar", result.parsed.merchantRaw)
    }

    @Test
    fun `gpay credit - received from person`() {
        val result = registry.resolve(
            "com.google.android.apps.nbu.paisa.user",
            "You received ₹500.00 from Rahul Sharma"
        )
        assertNotNull(result)
        assertEquals(TransactionDirection.CREDIT, result!!.parsed.direction)
        assertEquals("Rahul Sharma", result.parsed.merchantRaw)
    }

    // ---- PhonePe ----

    @Test
    fun `phonepe debit - payment of X to Y was successful`() {
        val result = registry.resolve("com.phonepe.app", "Payment of ₹150.00 to Zomato was successful.")
        assertNotNull(result)
        assertEquals("PHONEPE", result!!.bankKey)
        assertEquals(15000L, result.parsed.amountMinor)
        assertEquals(TransactionDirection.DEBIT, result.parsed.direction)
        assertEquals("Zomato", result.parsed.merchantRaw)
    }

    @Test
    fun `phonepe debit - no decimal amount`() {
        val result = registry.resolve("com.phonepe.app", "You paid ₹99 to BESCOM via PhonePe")
        assertNotNull(result)
        assertEquals(9900L, result!!.parsed.amountMinor)
        assertEquals("BESCOM", result.parsed.merchantRaw)
    }

    @Test
    fun `phonepe credit`() {
        val result = registry.resolve("com.phonepe.app", "You received ₹2,000.00 from Priya Singh on PhonePe")
        assertNotNull(result)
        assertEquals(200000L, result!!.parsed.amountMinor)
        assertEquals(TransactionDirection.CREDIT, result.parsed.direction)
        assertEquals("Priya Singh", result.parsed.merchantRaw)
    }

    // ---- SBI (via default SMS app notification) ----

    @Test
    fun `sbi debit via VPA`() {
        val result = registry.resolve(
            "com.google.android.apps.messaging",
            "Dear Customer, Rs.500.00 debited from A/c XX1234 on 12-09-26 to VPA swiggy@ybl. Ref No 123456789012 -SBI"
        )
        assertNotNull(result)
        assertEquals("SBI", result!!.bankKey)
        assertEquals(50000L, result.parsed.amountMinor)
        assertEquals(TransactionDirection.DEBIT, result.parsed.direction)
        assertEquals("swiggy@ybl", result.parsed.merchantRaw)
    }

    @Test
    fun `sbi credit via VPA`() {
        val result = registry.resolve(
            "com.sbi.yono",
            "Dear Customer, Rs.1500.00 credited to A/c XX1234 on 12-09-26 by VPA rahul@okhdfcbank Ref No 987654321098 -SBI"
        )
        assertNotNull(result)
        assertEquals(150000L, result!!.parsed.amountMinor)
        assertEquals(TransactionDirection.CREDIT, result.parsed.direction)
        assertEquals("rahul@okhdfcbank", result.parsed.merchantRaw)
    }

    // ---- HDFC ----

    @Test
    fun `hdfc debit via VPA with ref number in parens`() {
        val result = registry.resolve(
            "com.snapwork.hdfc",
            "Rs 499.00 debited from a/c XXXXXX1234 on 12-09-26 to VPA amazon@apl (UPI Ref No 123456789012)."
        )
        assertNotNull(result)
        assertEquals("HDFC", result!!.bankKey)
        assertEquals(49900L, result.parsed.amountMinor)
        assertEquals(TransactionDirection.DEBIT, result.parsed.direction)
        assertEquals("amazon@apl", result.parsed.merchantRaw)
    }

    @Test
    fun `hdfc credit via VPA`() {
        val result = registry.resolve(
            "com.google.android.apps.messaging",
            "Rs 2500.00 credited to a/c XXXXXX1234 on 12-09-26 from VPA client@okicici (UPI Ref No 123456789013)."
        )
        assertNotNull(result)
        assertEquals(250000L, result!!.parsed.amountMinor)
        assertEquals(TransactionDirection.CREDIT, result.parsed.direction)
        assertEquals("client@okicici", result.parsed.merchantRaw)
    }

    // ---- No match cases (registry must return null, never throw, never guess) ----

    @Test
    fun `unknown financial package uses generic parser`() {
        val result = registry.resolve("com.example.newupi", "You paid ₹100 to someone")
        assertNotNull(result)
        assertEquals("GENERIC_UPI", result!!.bankKey)
        assertEquals(10000L, result.parsed.amountMinor)
        assertEquals(TransactionDirection.DEBIT, result.parsed.direction)
    }

    @Test
    fun `known package but unrelated notification returns null`() {
        // e.g. a GPay "reminder" or promo notification with no amount in it
        val result = registry.resolve(
            "com.google.android.apps.nbu.paisa.user",
            "Complete your profile to unlock more features"
        )
        assertNull(result)
    }

    @Test
    fun `sbi package but non-SBI SMS in same default messaging app returns null`() {
        val result = registry.resolve(
            "com.google.android.apps.messaging",
            "Your OTP for login is 482913. Do not share this with anyone."
        )
        assertNull(result)
    }
    // ---- Candidate gate ----

    @Test
    fun `otp is rejected before AI fallback`() {
        assertEquals(false, com.snoop.expensetracker.pipeline.TransactionCandidateFilter.looksLikeCandidate(
            "Your OTP is 482913 for UPI. ₹500 reference amount"
        ))
    }

    @Test
    fun `promo is rejected before AI fallback`() {
        assertEquals(false, com.snoop.expensetracker.pipeline.TransactionCandidateFilter.looksLikeCandidate(
            "Get ₹500 cashback offer today"
        ))
    }

    @Test
    fun `payment candidate passes gate`() {
        assertEquals(true, com.snoop.expensetracker.pipeline.TransactionCandidateFilter.looksLikeCandidate(
            "You paid ₹249.00 to Swiggy"
        ))
    }

    @Test
    fun `generic self transfer is classified separately`() {
        val result = registry.resolve(
            "com.example.bank",
            "₹5,000 transferred to your own account. Ref No 123456789012"
        )
        assertNotNull(result)
        assertEquals("GENERIC_UPI", result!!.bankKey)
        assertEquals(500000L, result.parsed.amountMinor)
        assertEquals(TransactionDirection.TRANSFER, result.parsed.direction)
    }

}