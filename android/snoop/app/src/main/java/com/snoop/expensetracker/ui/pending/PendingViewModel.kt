package com.snoop.expensetracker.ui.pending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.*
import com.snoop.expensetracker.notification.PendingAlertPoster
import com.snoop.expensetracker.pipeline.BudgetChecker
import com.snoop.expensetracker.pipeline.MerchantResolver
import com.snoop.expensetracker.ui.common.formatRupees
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PendingUiState(val pendingTransactions: List<TransactionEntity> = emptyList(), val manualReviews: List<FallbackPendingEntity> = emptyList(), val categories: List<CategoryEntity> = emptyList(), val budgetAlertMessage: String? = null)

@HiltViewModel
class PendingViewModel @Inject constructor(
    private val transactionDao: TransactionDao, private val categoryDao: CategoryDao, private val fallbackPendingDao: FallbackPendingDao,
    private val merchantResolver: MerchantResolver, private val budgetChecker: BudgetChecker, private val pendingAlertPoster: PendingAlertPoster
) : ViewModel() {
    private val budgetAlertMessage = MutableStateFlow<String?>(null)
    val uiState: StateFlow<PendingUiState> = combine(transactionDao.observeByStatus(TransactionStatus.PENDING), fallbackPendingDao.observeAll(), categoryDao.observeAll(), budgetAlertMessage) { pending, manual, categories, alert ->
        PendingUiState(pending, manual.filter { it.attempts >= FallbackPendingDao.MAX_ATTEMPTS }, categories, alert)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PendingUiState())

    fun confirmCategory(transactionId: Long, categoryId: Long) = viewModelScope.launch {
        val transaction = transactionDao.getById(transactionId) ?: return@launch
        transactionDao.confirm(transactionId, categoryId, merchantResolver.normalize(transaction.merchantRaw))
        merchantResolver.learn(transaction.merchantRaw, categoryId)
        budgetChecker.checkCategory(categoryId)?.let { alert -> pendingAlertPoster.postBudgetAlert(alert); budgetAlertMessage.value = "${alert.categoryName}: ${formatRupees(alert.spentMinor)} of ${formatRupees(alert.limitMinor)} spent" }
    }

    fun saveManualReview(item: FallbackPendingEntity, amount: String, direction: TransactionDirection, source: String, categoryId: Long) = viewModelScope.launch {
        val rupees = amount.toDoubleOrNull() ?: return@launch
        if (rupees <= 0 || source.isBlank()) return@launch
        val tx = TransactionEntity(amountMinor = Math.round(rupees * 100.0), direction = direction, merchantRaw = source, merchantResolved = source, categoryId = categoryId, sourceApp = item.packageName, bank = "MANUAL_REVIEW", rawText = item.text, confidence = 1f, status = TransactionStatus.CONFIRMED, createdAt = item.createdAt)
        transactionDao.insert(tx)
        merchantResolver.learn(source, categoryId)
        fallbackPendingDao.delete(item.id)
        if (direction == TransactionDirection.DEBIT) budgetChecker.checkCategory(categoryId)?.let { pendingAlertPoster.postBudgetAlert(it) }
    }

    fun dismissBudgetAlert() { budgetAlertMessage.value = null }
}
