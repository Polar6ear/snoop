package com.snoop.expensetracker.ui.home

import com.snoop.expensetracker.ui.common.formatRupees
import com.snoop.expensetracker.ui.common.categoryEmoji

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.data.db.entities.TransactionEntity
import com.snoop.expensetracker.ui.theme.SnoopColors
import com.snoop.expensetracker.ui.manualentry.ManualEntrySheet
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onTransaction: (Long) -> Unit,
    onPending: () -> Unit,
    onHistory: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val s by viewModel.state.collectAsState()
    val month = s.monthExpense.coerceAtLeast(0)
    val income = s.monthIncome.coerceAtLeast(0)
    var showBalanceEditor by remember { mutableStateOf(false) }
    var showAddEntry by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEntry = true },
                containerColor = SnoopColors.Orange,
                contentColor = SnoopColors.Paper,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction")
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = pad.calculateTopPadding() + 18.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Good evening", style = MaterialTheme.typography.bodySmall, color = SnoopColors.Stone)
                        Text("Snoop", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                    Surface(shape = RoundedCornerShape(50), color = SnoopColors.PaperDim) {
                        Icon(Icons.Default.NotificationsNone, null, tint = SnoopColors.Ink, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            item {
                Card(
                    onClick = { showBalanceEditor = true },
                    colors = CardDefaults.cardColors(containerColor = SnoopColors.Ink),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Total balance", color = Color(0xFFA79E7E), style = MaterialTheme.typography.labelLarge)
                        Text(
                            money(s.balance),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SnoopColors.Paper
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            BalancePill("In · this month", money(income), SnoopColors.Lime)
                            BalancePill("Out · this month", money(month), SnoopColors.Coral)
                        }
                    }
                }
            }

            if (s.pendingCount > 0) {
                item {
                    Card(
                        onClick = onPending,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HelpOutline, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "${s.pendingCount} transaction${if (s.pendingCount == 1) "" else "s"} need${if (s.pendingCount == 1) "s" else ""} your input",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "Snoop couldn't confidently choose a category. Tap to review.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .78f)
                                )
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }

            item { SectionHeader("Recent activity", "See all", onHistory) }
            if (s.recent.isEmpty()) item { EmptyState() }
            items(s.recent, key = { "transaction_${it.id}" }) { t ->
                TransactionRow(t, s.categories, onTransaction)
            }

        }
    }

    if (showAddEntry) {
        ManualEntrySheet(
            onDismiss = { showAddEntry = false },
            onSaved = { showAddEntry = false }
        )
    }

    if (showBalanceEditor) {
        var value by remember { mutableStateOf((s.balance / 100.0).toString().removeSuffix(".0")) }
        AlertDialog(
            onDismissRequest = { showBalanceEditor = false },
            title = { Text("Update current balance") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Tell Snoop the balance your bank shows right now. Your existing transaction history stays untouched.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Current balance (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val rupees = value.toDoubleOrNull()
                        if (rupees != null && rupees >= 0) {
                            viewModel.setCurrentBalance(Math.round(rupees * 100))
                            showBalanceEditor = false
                        }
                    }
                ) { Text("Save balance") }
            },
            dismissButton = { TextButton(onClick = { showBalanceEditor = false }) { Text("Cancel") } }
        )
    }
}

@Composable private fun RowScope.BalancePill(label: String, value: String, accent: Color) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = .08f)
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(label, color = Color(0xFFA79E7E), style = MaterialTheme.typography.bodySmall)
            Text(value, color = accent, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable private fun SectionHeader(title: String, action: String?, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title.uppercase(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = SnoopColors.Stone)
        if (action != null) {
            TextButton(onClick = { onAction?.invoke() }, contentPadding = PaddingValues(0.dp)) {
                Text(action, color = SnoopColors.Orange, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable private fun EmptyState() {
    Card(shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AutoGraph, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("Your spending story starts here", fontWeight = FontWeight.Bold)
            Text(
                "Snoop will pick up supported payment notifications automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable private fun TransactionRow(t: TransactionEntity, categories: List<com.snoop.expensetracker.data.db.entities.CategoryEntity>, onTransaction: (Long) -> Unit) {
    val category = categories.firstOrNull { it.id == t.categoryId }
    val debit = t.direction == TransactionDirection.DEBIT
    val transfer = t.direction == TransactionDirection.TRANSFER
    Card(
        onClick = { onTransaction(t.id) },
        colors = CardDefaults.cardColors(containerColor = SnoopColors.Paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    Text(categoryEmoji(category), style = MaterialTheme.typography.titleLarge)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(t.merchantResolved ?: t.merchantRaw, fontWeight = FontWeight.SemiBold)
                Text(
                    when {
                        t.status.name == "PENDING" -> "Needs category · "
                        transfer -> "Transfer · "
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = SnoopColors.Amber
                )
                Text(
                    SimpleDateFormat("dd MMM · hh:mm a", Locale.getDefault()).format(Date(t.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                (when { transfer -> "↔"; debit -> "−"; else -> "+" }) + money(t.amountMinor),
                fontWeight = FontWeight.Bold,
                color = if (transfer) MaterialTheme.colorScheme.primary else if (debit) SnoopColors.OrangeDark else SnoopColors.Teal
            )
        }
    }
}

private fun money(minor: Long): String = formatRupees(minor)
private fun signedMoney(minor: Long): String = (if (minor >= 0) "+" else "−") + money(kotlin.math.abs(minor))
