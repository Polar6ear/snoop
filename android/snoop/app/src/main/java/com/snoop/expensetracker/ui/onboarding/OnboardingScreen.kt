package com.snoop.expensetracker.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.snoop.expensetracker.notification.NotificationAccessUtils
import com.snoop.expensetracker.notification.OemBatteryInstructions
import com.snoop.expensetracker.ui.theme.SnoopColors

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    var notificationAccessGranted by remember { mutableStateOf(NotificationAccessUtils.isNotificationAccessGranted(context)) }
    var postNotificationsGranted by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    var contactsPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { notificationAccessGranted = NotificationAccessUtils.isNotificationAccessGranted(context); postNotificationsGranted = it }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { contactsPermissionGranted = it }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationAccessGranted = NotificationAccessUtils.isNotificationAccessGranted(context)
                postNotificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
                contactsPermissionGranted =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val oemInstructions = remember { OemBatteryInstructions.forCurrentDevice(context) }
    var batteryStepAcknowledged by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Let's get Snoop set up", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Snoop reads payment notifications locally, categorizes spending, and asks you only when it is unsure. No SMS inbox access is needed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OnboardingStepCard(
                1, "Notification access",
                "Required. Lets Snoop see payment notifications from GPay, PhonePe, banks, and other supported UPI apps.",
                notificationAccessGranted,
                if (notificationAccessGranted) "Granted" else "Grant access",
                { NotificationAccessUtils.openNotificationAccessSettings(context) },
                !notificationAccessGranted
            )
        }

        item {
            OnboardingStepCard(
                2, "Allow Snoop alerts",
                "Lets Snoop notify you when a transaction needs a category and when a budget reaches a warning threshold.",
                postNotificationsGranted,
                if (postNotificationsGranted) "Granted" else "Allow alerts",
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else postNotificationsGranted = true
                },
                !postNotificationsGranted
            )
        }

        item {
            OnboardingStepCard(
                3, "Keep detection reliable",
                "Your phone's battery saver can stop background notification listeners. Open the recommended setting once.",
                batteryStepAcknowledged,
                "Open settings",
                {
                    OemBatteryInstructions.openBestAvailableSettings(context, oemInstructions)
                    batteryStepAcknowledged = true
                },
                true,
                extraContent = {
                    Column(Modifier.padding(top = 8.dp)) {
                        oemInstructions.steps.forEachIndexed { index, step ->
                            Text("${index + 1}. $step", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            )
        }

        item {
            OnboardingStepCard(
                4, "Recognize people from Contacts · optional",
                "If allowed, Snoop can recognize a payment to someone already saved in your contacts and suggest Friends & Family.",
                contactsPermissionGranted,
                if (contactsPermissionGranted) "Granted" else "Allow contacts",
                { contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
                !contactsPermissionGranted
            )
        }

        item {
            Button(
                onClick = onDone,
                enabled = notificationAccessGranted && postNotificationsGranted,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Continue") }

            if (!notificationAccessGranted || !postNotificationsGranted) {
                Spacer(Modifier.height(8.dp))
                Text(
                    if (!notificationAccessGranted) "Notification access is required for automatic detection."
                    else "Allow alerts so Snoop can ask for categories and warn about budgets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SnoopColors.OrangeDark
                )
            }
        }
    }
}

@Composable
private fun OnboardingStepCard(
    stepNumber: Int,
    title: String,
    description: String,
    done: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
    actionEnabled: Boolean,
    extraContent: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (done) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    null,
                    tint = if (done) SnoopColors.Teal else SnoopColors.Stone
                )
                Spacer(Modifier.width(10.dp))
                Text("Step $stepNumber", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            extraContent?.invoke()
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onAction, enabled = actionEnabled) { Text(actionLabel) }
        }
    }
}
