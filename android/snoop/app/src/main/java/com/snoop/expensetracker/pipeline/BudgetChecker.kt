package com.snoop.expensetracker.pipeline

import com.snoop.expensetracker.data.db.dao.BudgetDao
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

enum class BudgetAlertLevel { NONE, WARNING_90, OVER_100 }

data class BudgetAlert(
    val categoryName: String,
    val level: BudgetAlertLevel,
    val spentMinor: Long,
    val limitMinor: Long
)

/**
 * Spec §5: after a transaction is confirmed, "Budget engine checks: is this category's monthly
 * spend near/over its limit?" This class is the check; [com.snoop.expensetracker.work.BudgetOverspendWorker]
 * and the confirm-flow call it and are responsible for actually posting the notification.
 */
@Singleton
class BudgetChecker @Inject constructor(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    companion object {
        const val WARNING_THRESHOLD = 0.9
    }

    /** Start of the current calendar month, local time, as epoch millis. */
    private fun currentMonthStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Checks one category's budget against this month's confirmed spend. Null if no budget is set for it. */
    suspend fun checkCategory(categoryId: Long): BudgetAlert? {
        val budget = budgetDao.getForCategory(categoryId) ?: return null
        val category = categoryDao.getById(categoryId) ?: return null
        val spent = transactionDao.sumDebitForCategorySince(categoryId, currentMonthStart())

        val level = when {
            spent >= budget.limitMinor -> BudgetAlertLevel.OVER_100
            spent >= (budget.limitMinor * WARNING_THRESHOLD).toLong() -> BudgetAlertLevel.WARNING_90
            else -> BudgetAlertLevel.NONE
        }
        if (level == BudgetAlertLevel.NONE) return null

        val shouldNotify = when (level) {
            BudgetAlertLevel.WARNING_90 -> budgetDao.markWarningNotified(categoryId, currentMonthStart()) > 0
            BudgetAlertLevel.OVER_100 -> budgetDao.markOverNotified(categoryId, currentMonthStart()) > 0
            BudgetAlertLevel.NONE -> false
        }
        if (!shouldNotify) return null

        return BudgetAlert(
            categoryName = category.name,
            level = level,
            spentMinor = spent,
            limitMinor = budget.limitMinor
        )
    }

    /** Checks every category that currently has a budget set — used by the periodic WorkManager job. */
    suspend fun checkAllBudgets(): List<BudgetAlert> {
        return budgetDao.getAllOnce().mapNotNull { budget -> checkCategory(budget.categoryId) }
    }
}
