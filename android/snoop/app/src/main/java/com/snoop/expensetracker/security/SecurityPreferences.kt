package com.snoop.expensetracker.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("snoop_security", Context.MODE_PRIVATE)
    var biometricEnabled: Boolean
        get() = prefs.getBoolean("biometric_enabled", false)
        set(value) { prefs.edit().putBoolean("biometric_enabled", value).apply() }
    var minimizeRawData: Boolean
        get() = prefs.getBoolean("minimize_raw_data", false)
        set(value) { prefs.edit().putBoolean("minimize_raw_data", value).apply() }
}
