package com.snoop.expensetracker.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.data.db.entities.TransactionEntity
import com.snoop.expensetracker.ui.common.formatRupees
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import java.util.Calendar

enum class TransactionFilter { ALL, EXPENSE, INCOME, TRANSFER }
enum class TransactionRange { ALL, TODAY, LAST_7_DAYS }

data class TransactionsUiState(
    val items: List<TransactionEntity> = emptyList(),
    val filter: TransactionFilter = TransactionFilter.ALL,
    val range: TransactionRange = TransactionRange.ALL,
    val query: String = "",
    val categoryId: Long? = null,
    val categories: List<CategoryEntity> = emptyList()
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val dao: TransactionDao,
    private val categoryDao: CategoryDao
) : ViewModel() {
    private val filter = MutableStateFlow(TransactionFilter.ALL)
    private val range = MutableStateFlow(TransactionRange.ALL)
    private val query = MutableStateFlow("")
    private val categoryId = MutableStateFlow<Long?>(null)

    private val controls = combine(filter, range, query, categoryId) { f, r, q, selectedCategory ->
        Quad(f, r, q, selectedCategory)
    }

    val state: StateFlow<TransactionsUiState> = controls
        .combine(dao.observeAll()) { c, all -> c to all }
        .combine(categoryDao.observeAll()) { pair, categories ->
            val (c, all) = pair
            val now = System.currentTimeMillis()
            val cutoff = when (c.range) {
                TransactionRange.ALL -> Long.MIN_VALUE
                TransactionRange.TODAY -> startOfDay(now)
                TransactionRange.LAST_7_DAYS -> now - 7L * 24 * 60 * 60 * 1000
            }
            val normalized = c.query.trim()
            val items = all.filter { tx ->
                val directionOk = when (c.filter) {
                    TransactionFilter.ALL -> true
                    TransactionFilter.EXPENSE -> tx.direction == TransactionDirection.DEBIT
                    TransactionFilter.INCOME -> tx.direction == TransactionDirection.CREDIT
                    TransactionFilter.TRANSFER -> tx.direction == TransactionDirection.TRANSFER
                }
                val queryOk = normalized.isBlank() || tx.merchantRaw.contains(normalized, ignoreCase = true) ||
                    tx.merchantResolved?.contains(normalized, ignoreCase = true) == true ||
                    tx.note?.contains(normalized, ignoreCase = true) == true ||
                    formatRupees(tx.amountMinor).contains(normalized, ignoreCase = true) ||
                    (tx.amountMinor / 100).toString().contains(normalized, ignoreCase = true)
                directionOk && queryOk && tx.createdAt >= cutoff && (c.categoryId == null || tx.categoryId == c.categoryId)
            }
            TransactionsUiState(items, c.filter, c.range, c.query, c.categoryId, categories)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TransactionsUiState())

    private data class Quad(
        val filter: TransactionFilter,
        val range: TransactionRange,
        val query: String,
        val categoryId: Long?
    )

    fun setFilter(f: TransactionFilter) { filter.value = f }
    fun setRange(r: TransactionRange) { range.value = r }
    fun setQuery(q: String) { query.value = q }
    fun setCategory(id: Long?) { categoryId.value = id }
    fun clearFilters() { filter.value = TransactionFilter.ALL; range.value = TransactionRange.ALL; categoryId.value = null }

    private fun startOfDay(time: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
