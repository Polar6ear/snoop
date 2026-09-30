@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.pending

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao
import com.snoop.expensetracker.data.db.entities.*
import com.snoop.expensetracker.ui.common.categoryEmoji
import com.snoop.expensetracker.ui.common.formatRupees
import com.snoop.expensetracker.ui.theme.SnoopColors

@Composable
fun PendingScreen(viewModel: PendingViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var manualItem by remember { mutableStateOf<FallbackPendingEntity?>(null) }
    Scaffold(containerColor = SnoopColors.Paper, topBar = { TopAppBar(title = { Text("Review", fontWeight = FontWeight.ExtraBold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SnoopColors.Paper)) }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (state.pendingTransactions.isEmpty() && state.manualReviews.isEmpty()) {
                Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Surface(shape = RoundedCornerShape(18.dp), color = SnoopColors.Lime) { Icon(Icons.Default.Check, null, tint = SnoopColors.Ink, modifier = Modifier.padding(14.dp).size(28.dp)) }
                    Spacer(Modifier.height(14.dp)); Text("You're all caught up", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("Snoop will ask here when it needs your help.", color = SnoopColors.Stone)
                }
            } else LazyColumn(contentPadding = PaddingValues(20.dp, 8.dp, 20.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (state.manualReviews.isNotEmpty()) {
                    item { Text("SNOOP NEEDS YOUR INPUT", style = MaterialTheme.typography.labelLarge, color = SnoopColors.Orange, fontWeight = FontWeight.Bold) }
                    item { Text("I couldn't safely read this payment", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold) }
                    items(state.manualReviews, key = { "manual_${it.id}" }) { item -> ManualReviewCard(item) { manualItem = item } }
                }
                if (state.pendingTransactions.isNotEmpty()) {
                    item { Text("AI SUGGESTIONS", style = MaterialTheme.typography.labelLarge, color = SnoopColors.Teal, fontWeight = FontWeight.Bold) }
                    items(state.pendingTransactions, key = { "tx_${it.id}" }) { t -> PendingCard(t, state.categories) { id -> viewModel.confirmCategory(t.id, id) } }
                }
            }
            state.budgetAlertMessage?.let { message -> LaunchedEffect(message) { kotlinx.coroutines.delay(4000); viewModel.dismissBudgetAlert() }; Snackbar(Modifier.align(Alignment.BottomCenter).padding(16.dp), containerColor = SnoopColors.Ink, contentColor = SnoopColors.Paper) { Text(message) } }
        }
    }
    manualItem?.let { item -> ManualReviewDialog(item, state.categories, onDismiss = { manualItem = null }) { amount, direction, source, categoryId -> viewModel.saveManualReview(item, amount, direction, source, categoryId); manualItem = null } }
}

@Composable private fun ManualReviewCard(item: FallbackPendingEntity, onFill: () -> Unit) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = SnoopColors.Ink)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Surface(shape = RoundedCornerShape(13.dp), color = SnoopColors.Coral.copy(alpha=.18f)) { Icon(Icons.Default.AutoAwesome, null, tint = SnoopColors.Coral, modifier = Modifier.padding(10.dp)) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("Transaction needs details", color = SnoopColors.Paper, fontWeight = FontWeight.Bold); Text("AI couldn't confidently extract it", color = SnoopColors.PaperDim, style = MaterialTheme.typography.bodySmall) } }
            Text(item.text, color = SnoopColors.PaperDim, maxLines = 4, style = MaterialTheme.typography.bodySmall)
            Button(onClick = onFill, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = SnoopColors.Orange)) { Text("Fill it in") }
        }
    }
}

@Composable private fun ManualReviewDialog(item: FallbackPendingEntity, categories: List<CategoryEntity>, onDismiss: () -> Unit, onSave: (String, TransactionDirection, String, Long) -> Unit) {
    var amount by remember { mutableStateOf(extractAmount(item.text)) }
    var direction by remember { mutableStateOf(TransactionDirection.DEBIT) }
    var source by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<Long?>(null) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Tell Snoop what happened") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("I couldn't safely read this notification. Nothing will be counted until you confirm it.", color = SnoopColors.Stone)
            OutlinedTextField(value = amount, onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' }.take(12) }, label = { Text("Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { FilterChip(direction == TransactionDirection.DEBIT, { direction = TransactionDirection.DEBIT }, label = { Text("Money sent") }); FilterChip(direction == TransactionDirection.CREDIT, { direction = TransactionDirection.CREDIT }, label = { Text("Money received") }) }
            OutlinedTextField(value = source, onValueChange = { source = it }, label = { Text(if (direction == TransactionDirection.CREDIT) "Received from" else "Paid to") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Text("Category", color = SnoopColors.Stone, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) { items(categories, key = { it.id }) { c -> FilterChip(category == c.id, { category = c.id }, label = { Text("${categoryEmoji(c)} ${c.name}") }) } }
        }
    }, confirmButton = { TextButton(enabled = amount.toDoubleOrNull()?.let { it > 0 } == true && source.isNotBlank() && category != null, onClick = { onSave(amount, direction, source.trim(), category!!) }) { Text("Save transaction") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

private fun extractAmount(text: String): String {
    val m = Regex("(?:₹|Rs\\.?|INR)\\s*([\\d,]+(?:\\.\\d{1,2})?)", RegexOption.IGNORE_CASE).find(text) ?: Regex("([\\d,]+(?:\\.\\d{1,2})?)\\s?(?:₹|Rs\\.?|INR)", RegexOption.IGNORE_CASE).find(text)
    return m?.groupValues?.getOrNull(1)?.replace(",", "") ?: ""
}

@Composable private fun PendingCard(transaction: TransactionEntity, categories: List<CategoryEntity>, onCategoryPicked: (Long) -> Unit) {
    val sign = when(transaction.direction) { TransactionDirection.DEBIT -> "−"; TransactionDirection.CREDIT -> "+"; TransactionDirection.TRANSFER -> "↔" }
    val amountColor = when(transaction.direction) { TransactionDirection.DEBIT -> SnoopColors.Orange; TransactionDirection.CREDIT -> SnoopColors.Teal; TransactionDirection.TRANSFER -> SnoopColors.Purple }
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = SnoopColors.Paper), border = BorderStroke(1.dp, SnoopColors.PaperDim), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text(categoryEmoji(categories.firstOrNull { it.id == transaction.categoryId }), style = MaterialTheme.typography.headlineSmall); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(transaction.merchantRaw, fontWeight = FontWeight.Bold); Text("via ${transaction.bank}", style = MaterialTheme.typography.bodySmall, color = SnoopColors.Stone) }; Text(sign + formatRupees(transaction.amountMinor), fontWeight = FontWeight.ExtraBold, color = amountColor) }
            transaction.suggestedCategory?.let { suggestion -> Surface(shape = RoundedCornerShape(14.dp), color = SnoopColors.Lime.copy(alpha = .45f)) { Text("AI suggests $suggestion · ${(transaction.suggestedCategoryConfidence*100).toInt()}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp)) } }
            Text("Choose the correct category", style = MaterialTheme.typography.labelLarge, color = SnoopColors.Stone, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { items(categories, key = { it.id }) { c -> AssistChip(onClick = { onCategoryPicked(c.id) }, label = { Text("${categoryEmoji(c)} ${c.name}") }, shape = RoundedCornerShape(50), colors = AssistChipDefaults.assistChipColors(containerColor = SnoopColors.PaperDim)) } }
        }
    }
}
