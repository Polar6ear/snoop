package com.snoop.expensetracker.notification

import android.provider.Telephony
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.snoop.expensetracker.parser.parsers.GPayParser
import com.snoop.expensetracker.parser.parsers.HdfcParser
import com.snoop.expensetracker.parser.parsers.PhonePeParser
import com.snoop.expensetracker.parser.parsers.SbiParser
import com.snoop.expensetracker.pipeline.BudgetChecker
import com.snoop.expensetracker.pipeline.TransactionCandidateFilter
import com.snoop.expensetracker.pipeline.NotificationProcessor
import com.snoop.expensetracker.pipeline.ProcessResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Spec §4/§5: the Play-Store-safe primary detection path. Reads notification *content* posted
 * by banking/UPI apps (and, for SBI/HDFC, the phone's own default SMS app) via the standard
 * NotificationListenerService permission category — never touches READ_SMS/RECEIVE_SMS.
 *
 * The system instantiates this class directly (declared in AndroidManifest.xml), so field
 * injection via @AndroidEntryPoint is used instead of constructor injection.
 */
@AndroidEntryPoint
class SnoopNotificationListenerService : NotificationListenerService() {

    @Inject lateinit var notificationProcessor: NotificationProcessor
    @Inject lateinit var budgetChecker: BudgetChecker
    @Inject lateinit var pendingAlertPoster: PendingAlertPoster

    // SupervisorJob so one failed notification-processing coroutine can't cancel the others.
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Packages worth even attempting to parse. Rebuilt per call (cheap) rather than cached at
     * service-creation time, since the default SMS app can change if the user switches it.
     */
    private fun relevantPackages(): Set<String> {
        val defaultSms = try {
            Telephony.Sms.getDefaultSmsPackage(applicationContext)
        } catch (e: Exception) {
            null
        }
        return setOfNotNull(
            GPayParser.PACKAGE_NAME,
            PhonePeParser.PACKAGE_NAME,
            SbiParser.YONO_PACKAGE_NAME,
            HdfcParser.BANK_APP_PACKAGE_NAME,
            defaultSms
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(android.app.Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()

        // Prefer the expanded "big text" style when present (banks often put the full message
        // there and only a truncated preview in EXTRA_TEXT).
        val body = bigText.ifBlank { text }
        val fullText = listOf(title, body).filter { it.isNotBlank() }.joinToString(separator = " ")
        if (fullText.isBlank()) return

        // Process known banking/UPI apps plus any clearly financial notification. This
        // generic gate is what lets Snoop work with new UPI apps before a package-specific
        // parser is added.
        if (packageName !in relevantPackages() && !TransactionCandidateFilter.looksLikeCandidate(fullText)) return

        serviceScope.launch {
            when (val result = notificationProcessor.process(
                    packageName,
                    fullText
                )) {
                is ProcessResult.NeedsConfirmation -> {
                    pendingAlertPoster.postPendingAlert(result.transactionId)
                }
                is ProcessResult.NeedsManualEntry -> {
                    pendingAlertPoster.postManualReviewAlert(result.pendingId)
                }
                is ProcessResult.AutoConfirmed -> {
                    budgetChecker.checkCategory(result.categoryId)?.let { alert ->
                        pendingAlertPoster.postBudgetAlert(alert)
                    }
                }
                is ProcessResult.TransferConfirmed -> {
                }
                ProcessResult.Ignored -> {
                }
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        // No backlog replay: NotificationListenerService only sees notifications posted while
        // connected. Historical transactions before the user granted this permission are a known,
        // accepted gap — manual entry (Chunk 9) is the documented backfill path for those.
    }
}
