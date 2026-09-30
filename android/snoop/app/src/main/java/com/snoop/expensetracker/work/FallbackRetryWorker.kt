package com.snoop.expensetracker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao
import com.snoop.expensetracker.notification.PendingAlertPoster
import com.snoop.expensetracker.pipeline.NotificationProcessor
import com.snoop.expensetracker.pipeline.BudgetChecker
import com.snoop.expensetracker.pipeline.ProcessResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FallbackRetryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val fallbackPendingDao: FallbackPendingDao,
    private val notificationProcessor: NotificationProcessor,
    private val pendingAlertPoster: PendingAlertPoster,
    private val budgetChecker: BudgetChecker
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val items = fallbackPendingDao.getBatch(20)
        if (items.isEmpty()) return Result.success()

        var retryNeeded = false
        items.forEach { item ->
            when (val result = notificationProcessor.retryPending(item)) {
                is ProcessResult.NeedsConfirmation -> pendingAlertPoster.postPendingAlert(result.transactionId)
                is ProcessResult.NeedsManualEntry -> pendingAlertPoster.postManualReviewAlert(result.pendingId)
                is ProcessResult.AutoConfirmed -> {
                    budgetChecker.checkCategory(result.categoryId)?.let { alert ->
                        pendingAlertPoster.postBudgetAlert(alert)
                    }
                }
                is ProcessResult.TransferConfirmed -> Unit
                ProcessResult.Ignored -> {
                    // Still retryable items remain in the DB; their individual retry call
                    // increments attempts. We only return retry if at least one remains.
                }
            }
            // A failed retry is intentionally left in the table. The next run will try it again.
            // We do not infer failure from ProcessResult because ignored can also mean invalid text.
        }

        retryNeeded = fallbackPendingDao.getBatch(1).isNotEmpty()
        return if (retryNeeded) Result.retry() else Result.success()
    }
}
