package com.snoop.expensetracker.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.pipeline.MerchantResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val dao: TransactionDao,
    categoryDao: CategoryDao,
    private val merchantResolver: MerchantResolver
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> =
        categoryDao.observeAll()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun observe(id: Long) =
        dao.observeById(id)

    fun delete(
        id: Long,
        onDone: () -> Unit
    ) = viewModelScope.launch {
        dao.softDelete(id)
        onDone()
    }

    fun restore(id: Long) =
        viewModelScope.launch {
            dao.restore(id)
        }

    fun save(
        id: Long,
        amount: String,
        direction: TransactionDirection,
        merchant: String,
        categoryId: Long,
        note: String?,
        onDone: () -> Unit
    ) = viewModelScope.launch {

        val parsedAmount = amount
            .trim()
            .toDoubleOrNull()

        val amountMinor = parsedAmount
            ?.let { (it * 100).roundToLong() }
            ?: 0L

        val cleanMerchant = merchant.trim()

        if (amountMinor > 0L && cleanMerchant.isNotBlank()) {
            dao.updateUserFields(
                id = id,
                amountMinor = amountMinor,
                direction = direction,
                merchant = cleanMerchant,
                categoryId = categoryId,
                note = note?.trim()?.takeIf { it.isNotEmpty() }
            )

            merchantResolver.learn(
                cleanMerchant,
                categoryId
            )
        }

        onDone()
    }
}