@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.more

import com.snoop.expensetracker.ui.common.formatRupees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.data.db.entities.TransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.*

data class RecurringCandidate(val merchant: String, val amountMinor: Long, val count: Int, val cadenceDays: Int, val lastSeen: Long)

@HiltViewModel
class RecurringViewModel @Inject constructor(dao: TransactionDao) : ViewModel() {
    val candidates: StateFlow<List<RecurringCandidate>> = dao.observeAll().map { txs ->
        txs.filter { it.status.name == "CONFIRMED" && it.deletedAt == null && it.direction == TransactionDirection.DEBIT }
            .groupBy { (it.merchantResolved ?: it.merchantRaw).trim().lowercase() to it.amountMinor }
            .mapNotNull { (key, rows) ->
                if (rows.size < 2) return@mapNotNull null
                val sorted = rows.sortedBy { it.createdAt }
                val gaps = sorted.zipWithNext().map { (a,b) -> TimeUnit.MILLISECONDS.toDays(b.createdAt-a.createdAt).toInt() }.filter { it in 20..40 }
                if (gaps.isEmpty()) null else RecurringCandidate(key.first, key.second, rows.size, gaps.average().toInt(), sorted.last().createdAt)
            }.sortedByDescending { it.lastSeen }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@Composable
fun RecurringScreen(onBack: () -> Unit, viewModel: RecurringViewModel = hiltViewModel()) {
    val items by viewModel.candidates.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Recurring payments") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }) }) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("Detected from confirmed transactions. A payment is considered recurring when the same merchant and amount repeats on roughly a monthly cadence.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (items.isEmpty()) item { Text("No recurring pattern detected yet.") }
            items(items, key = { it.merchant + it.amountMinor }) { r ->
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
                    Text(r.merchant.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium)
                    Text("${formatRupees(r.amountMinor)} · about every ${r.cadenceDays} days · ${r.count} payments")
                }}
            }
        }
    }
}
