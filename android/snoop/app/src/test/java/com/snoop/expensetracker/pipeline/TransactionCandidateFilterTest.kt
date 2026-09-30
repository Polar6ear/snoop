package com.snoop.expensetracker.pipeline

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionCandidateFilterTest {
    @Test fun `real debit SMS with available balance phrase is NOT rejected`() {
        val text = "Rs.500.00 debited from A/c XX1234 on 12-09-26 to VPA swiggy@ybl. Available balance is Rs.4,500.00"
        assertTrue(TransactionCandidateFilter.looksLikeCandidate(text))
    }
    @Test fun `real credit SMS with account balance phrase is NOT rejected`() {
        val text = "Rs.2000.00 credited to A/c XX1234. Your account balance is Rs.10,000.00"
        assertTrue(TransactionCandidateFilter.looksLikeCandidate(text))
    }
    @Test fun `pure balance-only alert with no transaction verb IS rejected`() {
        assertFalse(TransactionCandidateFilter.looksLikeCandidate("Your available balance is Rs.4,500.00 as of today"))
    }
    @Test fun `otp is always rejected even with an amount present`() {
        assertFalse(TransactionCandidateFilter.looksLikeCandidate("Your OTP for the Rs.500 transaction is 483910. Do not share it with anyone."))
    }
    @Test fun `failed payment is rejected even though it says payment`() {
        assertFalse(TransactionCandidateFilter.looksLikeCandidate("Your payment of Rs.500 to Swiggy failed. Please try again."))
    }
    @Test fun `refund credit is accepted, not treated as a failure`() {
        assertTrue(TransactionCandidateFilter.looksLikeCandidate("Rs.500.00 credited to A/c XX1234 - refund for reversed transaction Ref No 123456789012"))
    }
    @Test fun `ordinary debit is accepted`() {
        assertTrue(TransactionCandidateFilter.looksLikeCandidate("You paid Rs.249.00 to Swiggy"))
    }
    @Test fun `text with no amount at all is rejected`() {
        assertFalse(TransactionCandidateFilter.looksLikeCandidate("Thank you for banking with us"))
    }
}
