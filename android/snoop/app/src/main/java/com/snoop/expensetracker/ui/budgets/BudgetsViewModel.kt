package com.snoop.expensetracker.ui.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.BudgetDao
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.BudgetEntity
import com.snoop.expensetracker.data.db.entities.BudgetPeriod
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryBudgetRow(
    val category: CategoryEntity,
    val limitMinor: Long?,   // null = no budget set for this category yet
    val spentMinor: Long
)

data class BudgetsUiState(
    val rows: List<CategoryBudgetRow> = emptyList()
)

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) : ViewModel() {

    private fun currentMonthStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // Re-evaluated every minute so the budget month rolls over without restarting the app.
    private val monthStart: kotlinx.coroutines.flow.Flow<Long> =
        kotlinx.coroutines.flow.flow {
            while (true) {
                emit(currentMonthStart())
                kotlinx.coroutines.delay(60_000L)
            }
        }.distinctUntilChanged()

    val uiState: StateFlow<BudgetsUiState> = monthStart.flatMapLatest { start ->
      combine(
        categoryDao.observeAll(),
        budgetDao.observeAll(),
        transactionDao.observeSpendByCategorySince(start)
      ) { categories, budgets, spends ->
        val budgetByCategory = budgets.associateBy { it.categoryId }
        val spendByCategory = spends.associate { it.categoryId to it.spentMinor }
        val rows = categories.map { category ->
            CategoryBudgetRow(
                category = category,
                limitMinor = budgetByCategory[category.id]?.limitMinor,
                spentMinor = spendByCategory[category.id] ?: 0L
            )
        }
        BudgetsUiState(rows = rows)
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetsUiState())

    /**
     * User sets/edits a monthly limit for a category (spec §5: "Users set spending limits per
     * category"). @Upsert only resolves conflicts on the primary key, not the categoryId unique
     * index, so re-setting an existing category's budget with a fresh id=0 entity would throw a
     * UNIQUE constraint violation instead of updating — look up the existing row's id first so
     * the same physical budget row gets updated in place.
     */
    fun setBudget(categoryId: Long, limitMinor: Long) {
        viewModelScope.launch {
            val existing = budgetDao.getForCategory(categoryId)
            budgetDao.upsert(
                BudgetEntity(
                    id = existing?.id ?: 0,
                    categoryId = categoryId,
                    limitMinor = limitMinor,
                    period = BudgetPeriod.MONTHLY,
                    startDate = existing?.startDate ?: currentMonthStart()
                )
            )
        }
    }

    fun clearBudget(categoryId: Long) {
        viewModelScope.launch { budgetDao.deleteForCategory(categoryId) }
    }
}
