@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.portability

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoop.expensetracker.data.portability.DataPortabilityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PortabilityViewModel @Inject constructor(private val manager: DataPortabilityManager) : ViewModel() {
    var message by mutableStateOf<String?>(null); private set
    fun exportCsv(uri: Uri) = run("CSV export") { manager.exportCsv(appResolver, uri) }
    fun backup(uri: Uri) = run("Backup") { manager.backup(appResolver, uri) }
    fun restore(uri: Uri) = run("Restore") { manager.restore(appResolver, uri) }
    private lateinit var appResolver: android.content.ContentResolver
    fun attachResolver(r: android.content.ContentResolver){ appResolver=r }
    private fun run(label:String, block:suspend()->Unit){ viewModelScope.launch { try { block(); message="$label complete" } catch(e:Exception){ message="$label failed: ${e.message ?: "unknown error"}" } } }
}

@Composable
fun PortabilityScreen(onBack:()->Unit, vm: PortabilityViewModel = hiltViewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) { vm.attachResolver(context.contentResolver) }
    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")){ it?.let(vm::exportCsv) }
    val backup = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")){ it?.let(vm::backup) }
    val restore = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){ it?.let(vm::restore) }
    Scaffold(topBar={TopAppBar(title={Text("Export & backup")}, navigationIcon={TextButton(onClick=onBack){Text("Back")}})}) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(20.dp), verticalArrangement=Arrangement.spacedBy(14.dp)) {
            Text("Your data stays on this device unless you explicitly export or back it up.", style=MaterialTheme.typography.bodyMedium)
            Button(onClick={export.launch("snoop-transactions.csv")}, Modifier.fillMaxWidth()){Text("Export transactions as CSV")}
            Button(onClick={backup.launch("snoop-backup.json")}, Modifier.fillMaxWidth()){Text("Create full backup")}
            OutlinedButton(onClick={restore.launch(arrayOf("application/json"))}, Modifier.fillMaxWidth()){Text("Restore backup")}
            Text("Restore replaces the current local transactions, categories, budgets and merchant rules. Make a backup first if you need to keep the current data.", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.error)
            vm.message?.let { Text(it, color=MaterialTheme.colorScheme.primary) }
        }
    }
}
