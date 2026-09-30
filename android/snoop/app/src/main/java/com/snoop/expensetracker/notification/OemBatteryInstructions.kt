package com.snoop.expensetracker.notification

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

data class OemInstructions(
    val brandLabel: String,
    val steps: List<String>,
    /** Best-effort deep link into that OEM's battery-management screen. May fail on some ROM versions — always paired with a Settings.ACTION_APPLICATION_DETAILS_SETTINGS fallback. */
    val deepLinkIntent: Intent?
)

/**
 * Spec §10 risk #2: "Xiaomi/Vivo/Oppo aggressively kill background services... unless the user
 * manually whitelists the app." Package/activity names below are the commonly-documented ones
 * for each OEM's "Autostart" / battery-saver exemption screen as of late-2025 ROM builds, but
 * OEMs change these between MIUI/ColorOS/FuntouchOS versions without warning — that's exactly
 * why every entry also has a generic app-details-settings fallback the caller can use if
 * `deepLinkIntent` fails to resolve on a given device.
 */
object OemBatteryInstructions {

    fun forCurrentDevice(context: Context): OemInstructions {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val pkg = context.packageName
        return when {
            manufacturer.contains("xiaomi") -> OemInstructions(
                brandLabel = "Xiaomi / Redmi / POCO",
                steps = listOf(
                    "Open Security app -> Permissions -> Autostart, and turn Snoop ON.",
                    "Go to Settings -> Apps -> Manage apps -> Snoop -> Battery saver -> set to \"No restrictions\".",
                    "Recent apps -> long-press Snoop's card -> tap the lock icon so it isn't cleared from memory."
                ),
                deepLinkIntent = Intent().apply {
                    component = android.content.ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }
            )
            manufacturer.contains("vivo") -> OemInstructions(
                brandLabel = "Vivo",
                steps = listOf(
                    "Open iManager -> App manager -> Autostart manager, and enable Snoop.",
                    "Settings -> Battery -> Background power consumption management -> set Snoop to \"Allow\".",
                    "Recent apps -> long-press Snoop's card -> lock it."
                ),
                deepLinkIntent = Intent().apply {
                    component = android.content.ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                }
            )
            manufacturer.contains("oppo") -> OemInstructions(
                brandLabel = "Oppo",
                steps = listOf(
                    "Settings -> Battery -> App Battery Management -> find Snoop -> allow background running.",
                    "Settings -> App management -> Snoop -> Allow auto-launch.",
                    "Recent apps -> long-press Snoop's card -> lock it."
                ),
                deepLinkIntent = Intent().apply {
                    component = android.content.ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                }
            )
            manufacturer.contains("oneplus") -> OemInstructions(
                brandLabel = "OnePlus",
                steps = listOf(
                    "Settings -> Battery -> Battery optimization -> Snoop -> Don't optimize.",
                    "Settings -> Apps -> Snoop -> Battery -> Allow background activity.",
                    "Recent apps -> long-press Snoop's card -> lock it."
                ),
                deepLinkIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            )
            else -> OemInstructions(
                brandLabel = "your phone",
                steps = listOf(
                    "Settings -> Apps -> Snoop -> Battery -> set to \"Unrestricted\".",
                    "If your phone shows a \"Recent apps lock\" option, lock Snoop's card so it isn't cleared from memory."
                ),
                deepLinkIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            )
        }
    }

    /** Tries the OEM-specific deep link first; falls back to this app's generic App info screen. */
    fun openBestAvailableSettings(context: Context, instructions: OemInstructions) {
        val primary = instructions.deepLinkIntent
        if (primary != null) {
            try {
                primary.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(primary)
                return
            } catch (e: ActivityNotFoundException) {
                // Falls through to the generic app-details screen below.
            } catch (e: SecurityException) {
                // Some OEMs block launching their battery screens from third-party apps outright.
            }
        }
        val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallback)
    }
}
