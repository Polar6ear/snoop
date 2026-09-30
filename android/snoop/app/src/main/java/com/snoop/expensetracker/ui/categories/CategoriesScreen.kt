@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.ui.common.categoryEmoji
import com.snoop.expensetracker.ui.common.snoopEmojiChoices
import com.snoop.expensetracker.ui.theme.SnoopColors

@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel = hiltViewModel()) {
    val categories by viewModel.categories.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        containerColor = SnoopColors.Paper,
        topBar = {
            TopAppBar(
                title = { Text("Categories", fontWeight = FontWeight.ExtraBold) },
                actions = { IconButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, "Create category") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SnoopColors.Paper)
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp, padding.calculateTopPadding() + 8.dp, 16.dp, 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryCard(category) { if (category.isCustom) deleteTarget = category }
            }
        }
    }

    if (showAdd) AddCategoryDialog(
        onDismiss = { showAdd = false },
        onConfirm = { name, emoji -> viewModel.createCustomCategory(name, emoji); showAdd = false }
    )
    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete ${target.name}?") },
            text = { Text("Existing transactions will keep their amount and history; their category will become uncategorized.") },
            confirmButton = { TextButton(onClick = { viewModel.deleteCustomCategory(target.id); deleteTarget = null }) { Text("Delete", color = SnoopColors.OrangeDark) } },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun CategoryCard(category: CategoryEntity, onDelete: () -> Unit) {
    val swatch = runCatching { Color(android.graphics.Color.parseColor(category.color)) }.getOrDefault(SnoopColors.Stone)
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SnoopColors.Paper), border = androidx.compose.foundation.BorderStroke(1.dp, SnoopColors.PaperDim)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(swatch.copy(alpha = .25f), CircleShape), contentAlignment = Alignment.Center) { Text(categoryEmoji(category), style = MaterialTheme.typography.titleLarge) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                Text(if (category.isCustom) "Custom" else "Default", style = MaterialTheme.typography.bodySmall, color = SnoopColors.Stone)
            }
            if (category.isCustom) IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, "Delete", tint = SnoopColors.OrangeDark) }
        }
    }
}

@Composable
private fun AddCategoryDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("✨") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Text("Choose an emoji", fontWeight = FontWeight.Bold, color = SnoopColors.Stone)
                LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.height(170.dp), verticalArrangement = Arrangement.spacedBy(6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(snoopEmojiChoices) { e ->
                        Surface(onClick = { emoji = e }, shape = RoundedCornerShape(12.dp), color = if (emoji == e) SnoopColors.PaperDim else Color.Transparent) { Box(Modifier.padding(7.dp), contentAlignment = Alignment.Center) { Text(e, style = MaterialTheme.typography.titleLarge) } }
                    }
                }
            }
        },
        confirmButton = { TextButton(enabled = name.isNotBlank(), onClick = { onConfirm(name, emoji) }) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
