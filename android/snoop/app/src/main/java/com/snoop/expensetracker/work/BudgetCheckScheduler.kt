package com.snoop.expensetracker.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetCheckScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val WORK_NAME = "periodic_budget_check"
    }

    /**
     * 6-hourly is frequent enough to catch drift without being a meaningful battery cost —
     * WorkManager's own minimum periodic interval is 15 minutes, but the real-time check that
     * already runs right after every confirmed transaction (see [com.snoop.expensetracker.pipeline.NotificationProcessor])
     * handles the common case; this job is just the periodic safety net.
     */
    fun schedulePeriodicCheck() {
        val request = PeriodicWorkRequestBuilder<BudgetCheckWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // don't reset the schedule/cancel a queued run on every app start
            request
        )
    }
}
