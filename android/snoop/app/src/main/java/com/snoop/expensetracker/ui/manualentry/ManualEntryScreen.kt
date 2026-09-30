@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.manualentry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.ui.theme.SnoopColors

/** Bottom-sheet entry used from Home. The rest of the app should not expose a second global add button. */
@Composable
fun ManualEntrySheet(
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ManualEntryViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    var amount by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf(TransactionDirection.DEBIT) }
    var merchant by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    LaunchedEffect(saveState.saved) {
        if (saveState.saved) {
            viewModel.resetSaveState()
            onSaved()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SnoopColors.Paper,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = SnoopColors.PaperDimmer) }
    ) {
        Column(
            Modifier.fillMaxWidth().imePadding().padding(horizontal = 20.dp).padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("New entry", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("₹", color = SnoopColors.Stone, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(7.dp))
                BasicTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' }.take(12) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold, color = SnoopColors.Ink),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.widthIn(min = 150.dp, max = 260.dp),
                    decorationBox = { inner ->
                        if (amount.isEmpty()) Text("0.00", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold), color = SnoopColors.PaperDimmer) else inner
                    }
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = direction == TransactionDirection.DEBIT,
                    onClick = { direction = TransactionDirection.DEBIT },
                    label = { Text("Spent") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SnoopColors.Ink, selectedLabelColor = SnoopColors.Paper)
                )
                FilterChip(
                    selected = direction == TransactionDirection.CREDIT,
                    onClick = { direction = TransactionDirection.CREDIT },
                    label = { Text("Received") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SnoopColors.TealDark, selectedLabelColor = SnoopColors.Paper)
                )
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 2.dp)) {
                items(categories, key = { it.id }) { c ->
                    ManualCategoryChip(c, selectedCategoryId == c.id) { selectedCategoryId = c.id }
                }
            }

            EntryField(
                label = if (direction == TransactionDirection.DEBIT) "Merchant / paid to" else "Received from",
                value = merchant,
                onValueChange = { merchant = it }
            )

            val valid = amount.toDoubleOrNull()?.let { it > 0 } == true
            Button(
                onClick = { viewModel.save(amount, direction, merchant, selectedCategoryId) },
                enabled = valid && (direction == TransactionDirection.CREDIT || selectedCategoryId != null),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SnoopColors.Orange)
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Save entry", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EntryField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SnoopColors.PaperDim,
            focusedContainerColor = SnoopColors.PaperDim,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = SnoopColors.Orange
        )
    )
}

@Composable
private fun ManualCategoryChip(category: CategoryEntity, selected: Boolean, onClick: () -> Unit) {
    val swatch = runCatching { Color(android.graphics.Color.parseColor(category.color)) }.getOrDefault(SnoopColors.Stone)
    AssistChip(
        onClick = onClick,
        label = { Text(category.name) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) swatch else swatch.copy(alpha = .14f),
            labelColor = if (selected) SnoopColors.Paper else SnoopColors.Ink
        ),
        shape = RoundedCornerShape(50)
    )
}

/** Kept for deep-link/backward compatibility; normal entry now comes from Home's sheet. */
@Composable
fun ManualEntryScreen(onSaved: () -> Unit, viewModel: ManualEntryViewModel = hiltViewModel()) {
    Scaffold(containerColor = SnoopColors.Paper) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("Manual entry is available from Home.", color = SnoopColors.Stone)
        }
    }
}
