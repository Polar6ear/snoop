@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.budgets

import com.snoop.expensetracker.ui.common.formatRupees

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snoop.expensetracker.ui.theme.SnoopColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun BudgetsScreen(
    onCreateCategory: () -> Unit,
    onInsights: () -> Unit,
    viewModel: BudgetsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var editingCategoryId by remember { mutableStateOf<Long?>(null) }
    val editingRow = state.rows.find { it.category.id == editingCategoryId }

    val totalSpent = state.rows.sumOf { it.spentMinor }
    val totalBudget = state.rows.mapNotNull { it.limitMinor }.sum()
    val budgetPercent = if (totalBudget > 0) {
        (totalSpent.toDouble() / totalBudget * 100.0).roundToInt().coerceAtLeast(0)
    } else 0
    val overBudget = totalBudget > 0 && totalSpent > totalBudget
    val month = SimpleDateFormat("MMM", Locale.getDefault()).format(Date())

    Scaffold(
        containerColor = SnoopColors.Paper,
        topBar = {
            TopAppBar(
                title = { Text("Budgets · $month", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onCreateCategory) {
                        Icon(Icons.Default.Add, contentDescription = "Create category")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SnoopColors.Paper)
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = padding.calculateTopPadding() + 8.dp, bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(
                    onClick = onInsights,
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SnoopColors.PaperDimmer)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BudgetRing(
                            percent = budgetPercent.coerceIn(0, 100),
                            overBudget = overBudget,
                            modifier = Modifier.size(128.dp)
                        )
                        Spacer(Modifier.width(18.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Spent this month", color = SnoopColors.Stone, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                formatRupees(totalSpent),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                if (totalBudget > 0) "/ ${formatRupees(totalBudget)}" else "No monthly limits yet",
                                color = SnoopColors.Stone,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                when {
                                    totalBudget == 0L -> "Set a category budget to start"
                                    overBudget -> "Over budget · tap for insights"
                                    else -> "On track · tap for insights"
                                },
                                color = if (overBudget) SnoopColors.Orange else SnoopColors.TealDark,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    "BY CATEGORY",
                    color = SnoopColors.Stone,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                )
            }

            items(state.rows, key = { it.category.id }) { row ->
                BudgetRow(row = row, onClick = { editingCategoryId = row.category.id })
            }
        }
    }

    if (editingRow != null) {
        EditBudgetDialog(
            categoryName = editingRow.category.name,
            currentLimitRupees = editingRow.limitMinor?.let { it / 100 },
            onDismiss = { editingCategoryId = null },
            onSave = { rupees ->
                viewModel.setBudget(editingRow.category.id, rupees * 100)
                editingCategoryId = null
            },
            onClear = if (editingRow.limitMinor != null) {
                { viewModel.clearBudget(editingRow.category.id); editingCategoryId = null }
            } else null
        )
    }
}

@Composable
private fun BudgetRing(percent: Int, overBudget: Boolean, modifier: Modifier = Modifier) {
    val accent = if (overBudget) SnoopColors.Orange else SnoopColors.Coral
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(4.dp)) {
            val stroke = 13.dp.toPx()
            drawArc(
                color = SnoopColors.PaperDimmer,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * percent / 100f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percent%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text("of budget", color = SnoopColors.Stone, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BudgetRow(row: CategoryBudgetRow, onClick: () -> Unit) {
    val limit = row.limitMinor
    val fraction = if (limit != null && limit > 0) row.spentMinor.toFloat() / limit else 0f
    val over = limit != null && row.spentMinor > limit
    val track = when {
        over -> SnoopColors.Orange
        fraction >= .9f -> SnoopColors.Amber
        else -> runCatching { Color(android.graphics.Color.parseColor(row.category.color)) }.getOrDefault(SnoopColors.Teal)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = SnoopColors.Paper,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(vertical = 8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(categoryEmoji(row.category.name) + "  " + row.category.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (limit != null) "${formatRupees(row.spentMinor)} / ${formatRupees(limit)}" else "Set budget",
                    fontWeight = FontWeight.Bold,
                    color = if (over) SnoopColors.Orange else SnoopColors.Ink
                )
            }
            if (limit != null && limit > 0) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { fraction.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = track,
                    trackColor = SnoopColors.PaperDimmer
                )
            }
        }
    }
}

private fun categoryEmoji(name: String): String = when (name.lowercase(Locale.getDefault())) {
    "food" -> "🍔"
    "transport" -> "🚕"
    "shopping" -> "🛍️"
    "bills" -> "💡"
    "entertainment" -> "🎬"
    "groceries" -> "🛒"
    "health" -> "🩺"
    "rent" -> "🏠"
    "friends & family" -> "👨‍👩‍👧"
    "transfers" -> "↔️"
    "recharge" -> "📱"
    else -> "•"
}

@Composable
private fun EditBudgetDialog(
    categoryName: String,
    currentLimitRupees: Long?,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit,
    onClear: (() -> Unit)?
) {
    var text by remember { mutableStateOf(currentLimitRupees?.toString() ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$categoryName budget") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.filter(Char::isDigit) },
                label = { Text("Monthly limit (₹)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { text.toLongOrNull()?.let(onSave) }, enabled = text.toLongOrNull()?.let { it > 0 } == true) { Text("Save") }
        },
        dismissButton = {
            Row {
                if (onClear != null) TextButton(onClick = onClear) { Text("Remove") }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}
