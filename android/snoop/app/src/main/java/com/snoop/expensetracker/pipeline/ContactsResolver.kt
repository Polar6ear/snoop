package com.snoop.expensetracker.pipeline

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun findMatchingContact(raw: String): String? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        val query = normalize(raw)
        if (query.length < 3) return null

        val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        return runCatching {
            context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val index = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val name = cursor.getString(index).orEmpty()
                    if (samePerson(query, normalize(name))) return name
                }
                null
            }
        }.getOrNull()
    }

    private fun normalize(value: String): String =
        value.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun samePerson(a: String, b: String): Boolean {
        if (a == b) return true
        if (a.length >= 4 && (a.contains(b) || b.contains(a))) return true
        val at = a.split(" ").filter { it.length >= 2 }.toSet()
        val bt = b.split(" ").filter { it.length >= 2 }.toSet()
        return at.isNotEmpty() && bt.isNotEmpty() && at.intersect(bt).size >= minOf(2, at.size, bt.size)
    }
}
