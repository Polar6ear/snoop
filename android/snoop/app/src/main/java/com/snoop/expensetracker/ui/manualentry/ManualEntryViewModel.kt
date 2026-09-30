package com.snoop.expensetracker.ui.manualentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.data.db.entities.TransactionEntity
import com.snoop.expensetracker.data.db.entities.TransactionStatus
import com.snoop.expensetracker.notification.PendingAlertPoster
import com.snoop.expensetracker.pipeline.BudgetChecker
import com.snoop.expensetracker.pipeline.MerchantResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ManualEntrySaveState(val saved: Boolean = false)

@HiltViewModel
class ManualEntryViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val merchantResolver: MerchantResolver,
    private val budgetChecker: BudgetChecker,
    private val pendingAlertPoster: PendingAlertPoster
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _saveState = MutableStateFlow(ManualEntrySaveState())
    val saveState: StateFlow<ManualEntrySaveState> = _saveState

    /**
     * Backup path for cash payments or a notification Snoop missed (spec §12 item 9). Saved
     * straight as CONFIRMED — a manual entry, by definition, already has the user's own
     * category choice, so there's nothing to show a fallback popup for.
     */
    fun save(amountRupees: String, direction: TransactionDirection, merchantName: String, categoryId: Long?) {
        val rupees = amountRupees.toDoubleOrNull() ?: return
        if (rupees <= 0) return
        val merchant = merchantName.trim().ifBlank { "Manual entry" }

        viewModelScope.launch {
            val amountMinor = Math.round(rupees * 100.0)
            val entity = TransactionEntity(
                amountMinor = amountMinor,
                direction = direction,
                merchantRaw = merchant,
                merchantResolved = merchant,
                categoryId = categoryId,
                sourceApp = "manual",
                bank = "MANUAL",
                rawText = "Manually entered by user",
                confidence = 1.0f,
                status = TransactionStatus.CONFIRMED,
                createdAt = System.currentTimeMillis()
            )
            transactionDao.insert(entity)

            // If the user typed a real merchant name (not the "Manual entry" placeholder),
            // learn it too — a cash payment to the same shop next time can still auto-suggest.
            if (merchantName.isNotBlank() && categoryId != null) {
                merchantResolver.learn(merchant, categoryId)
            }

            if (direction == TransactionDirection.DEBIT && categoryId != null) {
                budgetChecker.checkCategory(categoryId)?.let { alert ->
                    pendingAlertPoster.postBudgetAlert(alert)
                }
            }

            _saveState.value = ManualEntrySaveState(saved = true)
        }
    }

    fun resetSaveState() {
        _saveState.value = ManualEntrySaveState()
    }
}
