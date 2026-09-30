package com.snoop.expensetracker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.snoop.expensetracker.notification.PendingAlertPoster
import com.snoop.expensetracker.pipeline.BudgetChecker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

/**
 * Spec §5's budget engine also runs periodically (not just right after each transaction), so a
 * category that crept over its limit through several small confirmed transactions still gets
 * caught even if the exact transaction that tipped it over didn't trigger a same-instant check
 * for some reason (app was killed mid-processing, etc.) — belt and suspenders.
 */
@HiltWorker
class BudgetCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val budgetChecker: BudgetChecker,
    private val pendingAlertPoster: PendingAlertPoster
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val alerts = budgetChecker.checkAllBudgets()
            alerts.forEach { alert -> pendingAlertPoster.postBudgetAlert(alert) }
            Result.success()
        } catch (e: IOException) {
            // AUDIT FIX: transient (DB briefly locked/IO hiccup) -> worth a near-term retry with
            // backoff rather than waiting for the next 6-hourly scheduled run. The previous code
            // caught all exceptions here and returned Result.failure() with a comment claiming
            // that "lets WorkManager retry" -- it doesn't; only Result.retry() does.
            Result.retry()
        } catch (e: Exception) {
            // Unexpected/programming error -- retrying immediately won't fix a bug, and retrying
            // forever on one is worse than surfacing it. Left for the next periodic run instead.
            Result.failure()
        }
    }
}
