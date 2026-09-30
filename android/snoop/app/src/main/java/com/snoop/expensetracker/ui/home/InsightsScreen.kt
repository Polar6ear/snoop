@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.home

import com.snoop.expensetracker.ui.common.formatRupees

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.dao.TransactionDao
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.ui.theme.SnoopColors
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.*

data class InsightCategory(val name: String, val amountMinor: Long, val color: Color)
data class InsightUiState(
    val current: Long = 0,
    val previous: Long = 0,
    val daily: List<Long> = emptyList(),
    val categories: List<InsightCategory> = emptyList(),
    val refunds: List<String> = emptyList(),
    val budgetSuggestions: List<String> = emptyList()
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val dao: TransactionDao,
    private val categoryDao: CategoryDao
) : ViewModel() {
    private fun monthStart(offset: Int): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, offset)
        c.set(Calendar.DAY_OF_MONTH, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    val state = combine(
        dao.observeExpenseBetween(monthStart(0), System.currentTimeMillis()),
        dao.observeExpenseBetween(monthStart(-1), monthStart(0)),
        dao.observeAll(),
        categoryDao.observeAll()
    ) { current, previous, txs, cats ->
        val confirmedDebits = txs.filter { it.direction == TransactionDirection.DEBIT && it.status.name == "CONFIRMED" && it.createdAt >= monthStart(0) }
        val categoryRows = cats.mapNotNull { cat ->
            val amount = confirmedDebits.filter { it.categoryId == cat.id }.sumOf { it.amountMinor }
            if (amount == 0L) null else InsightCategory(
                cat.name,
                amount,
                runCatching { Color(android.graphics.Color.parseColor(cat.color)) }.getOrDefault(SnoopColors.Teal)
            )
        }.sortedByDescending { it.amountMinor }.take(5)

        val now = Calendar.getInstance()
        val daily = (6 downTo 0).map { daysAgo ->
            val start = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo); set(Calendar.HOUR_OF_DAY,0); set(Calendar.MINUTE,0); set(Calendar.SECOND,0); set(Calendar.MILLISECOND,0) }.timeInMillis
            val end = start + 24L * 60 * 60 * 1000
            confirmedDebits.filter { it.createdAt in start until end }.sumOf { it.amountMinor }
        }

        val refunds = txs.filter { it.direction == TransactionDirection.CREDIT && it.status.name == "CONFIRMED" }.mapNotNull { credit ->
            val match = txs.firstOrNull { debit ->
                debit.direction == TransactionDirection.DEBIT && debit.status.name == "CONFIRMED" &&
                    debit.amountMinor == credit.amountMinor && debit.merchantRaw.equals(credit.merchantRaw, true) &&
                    credit.createdAt >= debit.createdAt && credit.createdAt - debit.createdAt <= 14L * 24 * 60 * 60 * 1000
            }
            if (match != null) "Possible refund: ${credit.merchantResolved ?: credit.merchantRaw} · ${formatRupees(credit.amountMinor)}" else null
        }.distinct().take(10)

        val suggestions = cats.mapNotNull { cat ->
            val rows = txs.filter { it.categoryId == cat.id && it.direction == TransactionDirection.DEBIT && it.status.name == "CONFIRMED" && it.createdAt >= monthStart(-2) }
            if (rows.size < 2) null else "${cat.name}: consider ₹${((rows.sumOf { it.amountMinor }.toDouble() / rows.size) * 1.15 / 100).toInt()} monthly"
        }
        InsightUiState(current, previous, daily, categoryRows, refunds, suggestions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightUiState())
}

@Composable
fun InsightsScreen(onBack: () -> Unit, vm: InsightsViewModel = hiltViewModel()) {
    val s by vm.state.collectAsState()
    val elapsedDays = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
    val dailyAverage = s.current / elapsedDays
    val previous = s.previous.coerceAtLeast(1)
    val percentChange = ((s.current - s.previous).toDouble() / previous * 100).toInt()
    val maxDaily = s.daily.maxOrNull()?.coerceAtLeast(1L) ?: 1L

    Scaffold(
        containerColor = SnoopColors.Paper,
        topBar = {
            TopAppBar(
                title = { Text("Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = { AssistChip(onClick = {}, label = { Text("This month") }) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SnoopColors.Paper)
            )
        }
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p),
            contentPadding = PaddingValues(20.dp, 8.dp, 20.dp, 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, SnoopColors.PaperDimmer)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Daily average", color = SnoopColors.Stone)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(formatRupees(dailyAverage), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (s.previous == 0L) "" else if (percentChange >= 0) "↑ ${percentChange}% vs last month" else "↓ ${-percentChange}% vs last month",
                                color = if (percentChange >= 0) SnoopColors.Coral else SnoopColors.TealDark,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                        Row(Modifier.fillMaxWidth().height(130.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                            s.daily.forEach { amount ->
                                val h = (amount.toFloat() / maxDaily).coerceIn(.05f, 1f)
                                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.BottomCenter) {
                                    Surface(shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp), color = SnoopColors.Orange, modifier = Modifier.fillMaxWidth().fillMaxHeight(h)) {}
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("M","T","W","T","F","S","S").forEach { Text(it, color = SnoopColors.Stone, style = MaterialTheme.typography.labelSmall) }
                        }
                    }
                }
            }

            item { Text("TOP CATEGORIES", color = SnoopColors.Stone, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge) }
            item {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, SnoopColors.PaperDimmer)) {
                    Column(Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
                        if (s.categories.isEmpty()) Text("Not enough confirmed spending yet.", modifier = Modifier.padding(10.dp), color = SnoopColors.Stone)
                        s.categories.forEach { cat ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(10.dp), contentAlignment = Alignment.Center) { Canvas(Modifier.fillMaxSize()) { drawCircle(cat.color) } }
                                Spacer(Modifier.width(12.dp))
                                Text(cat.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                                Text(formatRupees(cat.amountMinor), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (s.refunds.isNotEmpty()) {
                item { Text("POSSIBLE REFUNDS", color = SnoopColors.Stone, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge) }
                items(s.refunds.size) { Text(s.refunds[it], style = MaterialTheme.typography.bodyMedium) }
            }
            if (s.budgetSuggestions.isNotEmpty()) {
                item { Text("BUDGET SUGGESTIONS", color = SnoopColors.Stone, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge) }
                items(s.budgetSuggestions.size) { Text(s.budgetSuggestions[it], style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }
}
