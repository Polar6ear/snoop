package com.snoop.expensetracker.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FallbackRetryScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object { private const val WORK_NAME = "fallback_retry_queue" }

    fun schedule() {
        val request = OneTimeWorkRequestBuilder<FallbackRetryWorker>()
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            // AUDIT FIX: without this, a retry attempt with no connectivity just burns a
            // battery/CPU cycle on a guaranteed IOException before backing off.
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}
