package com.snoop.expensetracker.pipeline

import com.snoop.expensetracker.data.db.dao.CategoryDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fast deterministic category layer. It deliberately handles obvious merchants locally;
 * unknown merchants go to Gemini and, if still uncertain, to the user.
 */
@Singleton
class CategoryClassifier @Inject constructor(
    private val categoryDao: CategoryDao
) {
    private val rules = linkedMapOf(
        "Food" to listOf("zomato", "swiggy", "dominos", "domino's", "mcdonald", "mcd", "kfc", "eatclub", "restaurant", "cafe", "pizza", "food"),
        "Groceries" to listOf("bigbasket", "zepto", "blinkit", "instamart", "dmart", "grocery", "grocer"),
        "Transport" to listOf("uber", "ola", "rapido", "metro", "irctc", "makemytrip", "fastag", "fuel", "petrol", "parking"),
        "Shopping" to listOf("amazon", "flipkart", "myntra", "ajio", "meesho", "nykaa", "shopping"),
        "Bills" to listOf("electricity", "bill", "bescom", "bses", "tata power", "water bill", "gas bill", "broadband"),
        "Recharge" to listOf("recharge", "jio", "airtel", "vi ", "vodafone", "mobile recharge"),
        "Entertainment" to listOf("netflix", "spotify", "prime video", "hotstar", "bookmyshow", "youtube premium", "steam"),
        "Health" to listOf("pharmeasy", "netmeds", "apollo", "1mg", "hospital", "clinic", "medical", "pharmacy"),
        "Rent" to listOf("rent", "housing"),
    )

    suspend fun classify(merchant: String): Long? {
        val normalized = merchant.lowercase()
        val match = rules.entries.firstOrNull { (_, keywords) ->
            keywords.any { normalized.contains(it) }
        } ?: return null
        return categoryDao.getIdByName(match.key)
    }

    suspend fun categoryByName(name: String?): Long? {
        if (name.isNullOrBlank()) return null
        return categoryDao.getIdByName(name.trim())
    }
}
