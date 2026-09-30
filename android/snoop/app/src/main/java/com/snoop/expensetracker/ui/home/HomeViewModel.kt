package com.snoop.expensetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.CategorySpend
import com.snoop.expensetracker.data.db.dao.BalanceDao
import com.snoop.expensetracker.data.db.entities.BalanceAnchorEntity
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.data.db.entities.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val balance: Long = 0,
    val pendingCount: Int = 0,
    val monthExpense: Long = 0,
    val todayExpense: Long = 0,
    val monthIncome: Long = 0,
    val recent: List<TransactionEntity> = emptyList(),
    val categorySpend: List<CategorySpend> = emptyList(),
    val categories: List<CategoryEntity> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val balanceDao: BalanceDao
) : ViewModel() {

    private fun startOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun startOfToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Emits (startOfToday, startOfMonth) and re-emits whenever either boundary changes, so the
     * dashboard rolls over at midnight / month-end even if the process stays alive for days.
     */
    private val periods: kotlinx.coroutines.flow.Flow<Pair<Long, Long>> =
        kotlinx.coroutines.flow.flow {
            while (true) {
                emit(startOfToday() to startOfMonth())
                kotlinx.coroutines.delay(60_000L)
            }
        }.distinctUntilChanged()

    private val financialData =
        periods.flatMapLatest { (today, month) ->
            combine(
                dao.observeExpenseSince(month),
                dao.observeExpenseSince(today),
                dao.observeIncomeSince(month)
            ) { monthExpense, todayExpense, monthIncome ->
                Triple(monthExpense, todayExpense, monthIncome)
            }
        }

    private val transactionData =
        periods.flatMapLatest { (_, month) ->
            combine(
                dao.observeRecent(8),
                dao.observeSpendByCategorySince(month)
            ) { recent, categorySpend ->
                Pair(recent, categorySpend)
            }
        }

    private val balanceData: kotlinx.coroutines.flow.Flow<Long> =
        balanceDao.observeAnchor().flatMapLatest { anchor ->
            if (anchor == null) {
                dao.observeNetChangeSince(0L)
            } else {
                dao.observeNetChangeSince(anchor.anchoredAt).map { delta ->
                    anchor.balanceMinor + delta
                }
            }
        }

    fun setCurrentBalance(balanceMinor: Long) {
        viewModelScope.launch {
            balanceDao.upsertAnchor(
                BalanceAnchorEntity(
                    balanceMinor = balanceMinor,
                    anchoredAt = System.currentTimeMillis()
                )
            )
        }
    }

    val state: StateFlow<HomeUiState> =
        combine(
            combine(financialData, transactionData) { financial, transactions ->
                Pair(financial, transactions)
            },
            combine(balanceData, dao.observePendingCount()) { balance, pendingCount ->
                Pair(balance, pendingCount)
            },
            categoryDao.observeAll()
        ) { first, second, categories ->

            HomeUiState(
                monthExpense = first.first.first,
                todayExpense = first.first.second,
                monthIncome = first.first.third,
                recent = first.second.first,
                categorySpend = first.second.second,
                categories = categories,
                balance = second.first,
                pendingCount = second.second
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HomeUiState()
        )
}