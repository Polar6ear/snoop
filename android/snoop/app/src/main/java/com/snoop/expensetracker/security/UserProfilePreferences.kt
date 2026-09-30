package com.snoop.expensetracker.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfilePreferences @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("snoop_profile", Context.MODE_PRIVATE)
    var displayName: String
        get() = prefs.getString("display_name", "Snoop User") ?: "Snoop User"
        set(value) { prefs.edit().putString("display_name", value.trim().ifBlank { "Snoop User" }).apply() }
    var avatarEmoji: String
        get() = prefs.getString("avatar_emoji", "🧊") ?: "🧊"
        set(value) { prefs.edit().putString("avatar_emoji", value.ifBlank { "🧊" }).apply() }
}
