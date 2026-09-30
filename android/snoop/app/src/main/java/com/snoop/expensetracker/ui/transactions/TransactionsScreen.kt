package com.snoop.expensetracker.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snoop.expensetracker.data.db.entities.TransactionDirection
import com.snoop.expensetracker.data.db.entities.TransactionEntity
import com.snoop.expensetracker.ui.common.categoryEmoji
import com.snoop.expensetracker.ui.common.formatRupees
import com.snoop.expensetracker.ui.theme.SnoopColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen(
    onTransaction: (Long) -> Unit,
    viewModel: TransactionsViewModel =
        androidx.hilt.navigation.compose.hiltViewModel()
) {
    val s by viewModel.state.collectAsState()
    var filtersOpen by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SnoopColors.Paper
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
        ) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Transactions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { filtersOpen = true }
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        "More filters"
                    )
                }
            }

            OutlinedTextField(
                value = s.query,
                onValueChange = viewModel::setQuery,
                placeholder = {
                    Text("Search merchant, note, amount")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        null
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = SnoopColors.PaperDim,
                    focusedContainerColor = SnoopColors.PaperDim,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = SnoopColors.Orange
                )
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = s.filter == TransactionFilter.ALL,
                    onClick = {
                        viewModel.setFilter(TransactionFilter.ALL)
                    },
                    label = {
                        Text("All")
                    }
                )

                FilterChip(
                    selected = s.filter == TransactionFilter.EXPENSE,
                    onClick = {
                        viewModel.setFilter(TransactionFilter.EXPENSE)
                    },
                    label = {
                        Text("Spent")
                    }
                )

                FilterChip(
                    selected = s.filter == TransactionFilter.INCOME,
                    onClick = {
                        viewModel.setFilter(TransactionFilter.INCOME)
                    },
                    label = {
                        Text("Income")
                    }
                )

                Surface(
                    onClick = {
                        filtersOpen = true
                    },
                    shape = RoundedCornerShape(50),
                    color = SnoopColors.PaperDim
                ) {
                    Row(
                        Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 9.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            filterSummary(s),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            Modifier.width(4.dp)
                        )

                        Icon(
                            Icons.Default.FilterList,
                            null,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            if (s.items.isEmpty()) {

                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            null,
                            modifier = Modifier.size(40.dp),
                            tint = SnoopColors.Stone
                        )

                        Text(
                            "Nothing found",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Try another search or filter.",
                            color = SnoopColors.Stone
                        )
                    }
                }

            } else {

                LazyColumn(
                    contentPadding = PaddingValues(
                        20.dp,
                        0.dp,
                        20.dp,
                        100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {

                    val groups = s.items.groupBy {
                        dayLabel(it.createdAt)
                    }

                    groups.forEach { (day, rows) ->

                        item(
                            key = "day_$day"
                        ) {
                            Text(
                                day.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = SnoopColors.Stone,
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    bottom = 5.dp
                                )
                            )
                        }

                        items(
                            rows,
                            key = { "tx_${it.id}" }
                        ) { tx ->

                            TransactionListRow(
                                tx,
                                s.categories
                            ) {
                                onTransaction(tx.id)
                            }
                        }
                    }
                }
            }
        }
    }

    if (filtersOpen) {
        TransactionFiltersSheet(
            s,
            viewModel
        ) {
            filtersOpen = false
        }
    }
}

private fun filterSummary(
    s: TransactionsUiState
): String = when {

    s.range == TransactionRange.TODAY ->
        "Today"

    s.range == TransactionRange.LAST_7_DAYS ->
        "7 days"

    s.filter == TransactionFilter.TRANSFER ->
        "Transfers"

    s.categoryId != null ->
        s.categories
            .firstOrNull { it.id == s.categoryId }
            ?.name
            ?: "Category"

    else ->
        "Filters"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionFiltersSheet(
    s: TransactionsUiState,
    vm: TransactionsViewModel,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SnoopColors.Paper
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                "Filters",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                "Type",
                color = SnoopColors.Stone,
                fontWeight = FontWeight.Bold
            )

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    listOf(
                        TransactionFilter.ALL to "All",
                        TransactionFilter.EXPENSE to "Spent",
                        TransactionFilter.INCOME to "Income",
                        TransactionFilter.TRANSFER to "Transfers"
                    )
                ) { (f, label) ->

                    FilterChip(
                        selected = s.filter == f,
                        onClick = {
                            vm.setFilter(f)
                        },
                        label = {
                            Text(label)
                        }
                    )
                }
            }

            Text(
                "Time",
                color = SnoopColors.Stone,
                fontWeight = FontWeight.Bold
            )

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    listOf(
                        TransactionRange.ALL to "All time",
                        TransactionRange.TODAY to "Today",
                        TransactionRange.LAST_7_DAYS to "7 days"
                    )
                ) { (r, label) ->

                    FilterChip(
                        selected = s.range == r,
                        onClick = {
                            vm.setRange(r)
                        },
                        label = {
                            Text(label)
                        }
                    )
                }
            }

            Text(
                "Category",
                color = SnoopColors.Stone,
                fontWeight = FontWeight.Bold
            )

            LazyCategoryRow(
                s,
                vm
            )

            Button(
                onClick = {
                    vm.clearFilters()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SnoopColors.Ink
                )
            ) {
                Text(
                    "Clear filters",
                    color = SnoopColors.Paper
                )
            }
        }
    }
}

@Composable
private fun LazyCategoryRow(
    s: TransactionsUiState,
    vm: TransactionsViewModel
) {
    androidx.compose.foundation.lazy.LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            FilterChip(
                selected = s.categoryId == null,
                onClick = {
                    vm.setCategory(null)
                },
                label = {
                    Text("All")
                }
            )
        }

        items(
            s.categories,
            key = { it.id }
        ) { c ->

            FilterChip(
                selected = s.categoryId == c.id,
                onClick = {
                    vm.setCategory(c.id)
                },
                label = {
                    Text(
                        "${categoryEmoji(c)} ${c.name}"
                    )
                }
            )
        }
    }
}

private fun dayLabel(
    time: Long
): String {

    val now = Calendar.getInstance()

    val date = Calendar.getInstance().apply {
        timeInMillis = time
    }

    return if (
        now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    ) {
        "Today"
    } else {
        SimpleDateFormat(
            "EEEE, dd MMM",
            Locale.getDefault()
        ).format(Date(time))
    }
}

@Composable
private fun TransactionListRow(
    t: TransactionEntity,
    categories: List<com.snoop.expensetracker.data.db.entities.CategoryEntity>,
    onClick: () -> Unit
) {

    val category = categories.firstOrNull {
        it.id == t.categoryId
    }

    val debit =
        t.direction == TransactionDirection.DEBIT

    val transfer =
        t.direction == TransactionDirection.TRANSFER

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = SnoopColors.Paper
        ),
        elevation = CardDefaults.cardElevation(
            0.dp
        ),
        shape = RoundedCornerShape(14.dp)
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 11.dp,
                    horizontal = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                shape = RoundedCornerShape(13.dp),
                color = runCatching {
                    Color(
                        android.graphics.Color.parseColor(
                            category?.color ?: "#E8E2D6"
                        )
                    )
                }
                    .getOrDefault(
                        SnoopColors.PaperDim
                    )
                    .copy(alpha = .25f)
            ) {

                Box(
                    Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        categoryEmoji(category),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(
                Modifier.width(12.dp)
            )

            Column(
                Modifier.weight(1f)
            ) {

                Text(
                    t.merchantResolved ?: t.merchantRaw,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                val categoryName =
                    category?.name ?: "Uncategorized"

                val source = when (t.bank) {
                    "GPAY" -> "UPI"
                    "PHONEPE" -> "UPI"
                    "MANUAL" -> "Manual"
                    else -> t.bank
                }

                Text(
                    "$categoryName · ${
                        SimpleDateFormat(
                            "hh:mm a",
                            Locale.getDefault()
                        ).format(Date(t.createdAt))
                    } · $source",
                    style = MaterialTheme.typography.bodySmall,
                    color = SnoopColors.Stone,
                    maxLines = 1
                )
            }

            Text(
                (
                        when {
                            transfer -> "↔"
                            debit -> "−"
                            else -> "+"
                        }
                        ) + formatRupees(t.amountMinor),
                fontWeight = FontWeight.ExtraBold,
                color = when {
                    transfer -> SnoopColors.Purple
                    debit -> SnoopColors.Ink
                    else -> SnoopColors.Teal
                }
            )
        }
    }
}