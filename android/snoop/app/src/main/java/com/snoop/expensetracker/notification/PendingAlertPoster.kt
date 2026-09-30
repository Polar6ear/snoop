package com.snoop.expensetracker.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.snoop.expensetracker.R
import com.snoop.expensetracker.pipeline.BudgetAlert
import com.snoop.expensetracker.pipeline.BudgetAlertLevel
import com.snoop.expensetracker.ui.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

object NotificationChannels {
    const val PENDING_TRANSACTIONS = "pending_transactions"
    const val BUDGET_ALERTS = "budget_alerts"
}

/**
 * Two notification kinds, both local (no push server involved):
 *  - "New expense detected, tap to categorize" — spec §5's Pending card/notification.
 *  - "Food budget at 90%" — spec §5's budget-engine warning.
 */
@Singleton
class PendingAlertPoster @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        createChannelsIfNeeded()
    }

    private fun createChannelsIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        manager.createNotificationChannel(
            NotificationChannel(
                NotificationChannels.PENDING_TRANSACTIONS,
                "Transactions needing review",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        manager.createNotificationChannel(
            NotificationChannel(
                NotificationChannels.BUDGET_ALERTS,
                "Budget warnings",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    private fun hasPostPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun postPendingAlert(transactionId: Long) {
        if (!hasPostPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_PENDING_TRANSACTION_ID, transactionId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            transactionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.PENDING_TRANSACTIONS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Transaction needs your input")
            .setContentText("Tap to choose a category")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        androidx.core.app.NotificationManagerCompat.from(context)
            .notify(transactionId.toInt(), notification)
    }


    fun postManualReviewAlert(pendingId: Long) {
        if (!hasPostPermission()) return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_PENDING_FALLBACK_ID, pendingId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, (pendingId xor 0x5A17).toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NotificationChannels.PENDING_TRANSACTIONS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Snoop couldn't read this transaction")
            .setContentText("Tap to enter the amount, source and category")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        androidx.core.app.NotificationManagerCompat.from(context)
            .notify((pendingId xor 0x5A17).toInt(), notification)
    }

    fun postBudgetAlert(alert: BudgetAlert) {
        if (!hasPostPermission()) return

        val title = when (alert.level) {
            BudgetAlertLevel.OVER_100 -> "${alert.categoryName} budget exceeded"
            BudgetAlertLevel.WARNING_90 -> "${alert.categoryName} budget at 90%"
            BudgetAlertLevel.NONE -> return
        }
        val spentRupees = alert.spentMinor / 100.0
        val limitRupees = alert.limitMinor / 100.0
        val text = "₹%.0f of ₹%.0f spent this month".format(spentRupees, limitRupees)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.categoryName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.BUDGET_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        androidx.core.app.NotificationManagerCompat.from(context)
            .notify(alert.categoryName.hashCode(), notification)
    }

    companion object {
        const val EXTRA_OPEN_PENDING_TRANSACTION_ID = "open_pending_transaction_id"
        const val EXTRA_OPEN_PENDING_FALLBACK_ID = "open_pending_fallback_id"
    }
}
