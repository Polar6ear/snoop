@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.more

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
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.MerchantMappingDao
import com.snoop.expensetracker.data.db.entities.MerchantMappingEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class MerchantRulesViewModel @Inject constructor(
    merchantDao: MerchantMappingDao,
    private val categoryDao: CategoryDao
) : ViewModel() {
    val mappings = merchantDao.observeAll()
    val categories = categoryDao.observeAll()
    private val dao = merchantDao
    fun changeCategory(key: String, categoryId: Long) = viewModelScope.launch {
        dao.upsert(MerchantMappingEntity(key, categoryId, System.currentTimeMillis()))
    }
    fun delete(key: String) = viewModelScope.launch { dao.delete(key) }
}

@Composable
fun MerchantRulesScreen(onBack: () -> Unit, viewModel: MerchantRulesViewModel = hiltViewModel()) {
    val mappings by viewModel.mappings.collectAsState(emptyList())
    val categories by viewModel.categories.collectAsState(emptyList())
    Scaffold(topBar = { TopAppBar(title = { Text("Merchant rules") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }) }) { p ->
        if (mappings.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(p), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No learned merchant rules yet. Categorise a pending transaction and Snoop will remember it.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(mappings, key = { it.merchantKey }) { rule ->
                    val category = categories.firstOrNull { it.id == rule.categoryId }
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(rule.merchantKey, style = MaterialTheme.typography.titleMedium)
                            Text("Category: ${category?.name ?: "Unknown"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                categories.take(6).forEach { c ->
                                    TextButton(onClick = { viewModel.changeCategory(rule.merchantKey, c.id) }) { Text(c.name) }
                                }
                                TextButton(onClick = { viewModel.delete(rule.merchantKey) }) { Text("Forget") }
                            }
                        }
                    }
                }
            }
        }
    }
}
