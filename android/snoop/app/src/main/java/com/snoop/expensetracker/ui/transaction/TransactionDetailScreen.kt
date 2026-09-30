@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.transaction

import com.snoop.expensetracker.ui.common.formatRupees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snoop.expensetracker.data.db.entities.*
import com.snoop.expensetracker.ui.theme.SnoopColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionDetailScreen(id: Long, onBack: () -> Unit, onDeleted: () -> Unit, viewModel: TransactionViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
    val t by viewModel.observe(id).collectAsState(initial = null); var editing by remember { mutableStateOf(false) }; val cats by viewModel.categories.collectAsState(); val x=t ?: return
    if (editing) { EditTransaction(x,cats,{a,d,m,c,n->viewModel.save(id,a,d,m,c,n){editing=false}},{editing=false}); return }
    val debit=x.direction==TransactionDirection.DEBIT; val transfer=x.direction==TransactionDirection.TRANSFER
    Scaffold(containerColor=SnoopColors.Paper, topBar={ TopAppBar(title={Text("Transaction",fontWeight=FontWeight.Bold)}, navigationIcon={IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,"Back")}}, colors=TopAppBarDefaults.topAppBarColors(containerColor=SnoopColors.Paper)) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Surface(shape=RoundedCornerShape(24.dp),color=SnoopColors.Ink){Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text(x.merchantResolved?:x.merchantRaw,color=SnoopColors.PaperDim,fontWeight=FontWeight.Bold); Text((when{transfer->"↔";debit->"−";else->"+"})+formatRupees(x.amountMinor),style=MaterialTheme.typography.displayLarge,fontWeight=FontWeight.ExtraBold,color=Color.White); Text(if(transfer)"Transfer" else if(debit)"Expense" else "Income",color=if(debit)SnoopColors.Coral else SnoopColors.Lime,fontWeight=FontWeight.Bold)}}
            if (x.deletedAt != null) {
                Surface(shape=RoundedCornerShape(16.dp), color=SnoopColors.Coral.copy(alpha=.14f)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                        Text("Recently deleted", fontWeight=FontWeight.Bold)
                        TextButton(onClick={viewModel.restore(id)}) { Text("Restore", color=SnoopColors.OrangeDark) }
                    }
                }
            }
            Surface(shape=RoundedCornerShape(20.dp),color=SnoopColors.Paper,border=androidx.compose.foundation.BorderStroke(1.dp,SnoopColors.PaperDim)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(13.dp)){DetailLine("Category",cats.firstOrNull{it.id==x.categoryId}?.name?:"Uncategorized");DetailLine("Source",x.sourceApp);DetailLine("Bank / app",x.bank);DetailLine("Date",SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.getDefault()).format(Date(x.createdAt)));DetailLine("Detection confidence","${(x.confidence*100).toInt()}%")}}
            if(transfer) Text("Transfers don't count as income, expenses or budgets.",style=MaterialTheme.typography.bodySmall,color=SnoopColors.Stone)
            x.note?.takeIf{it.isNotBlank()}?.let{Surface(shape=RoundedCornerShape(16.dp),color=SnoopColors.PaperDim){Text("Note · $it",Modifier.padding(14.dp))}}
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){if(x.deletedAt==null){Button(onClick={editing=true},modifier=Modifier.weight(1f),shape=RoundedCornerShape(15.dp),colors=ButtonDefaults.buttonColors(containerColor=SnoopColors.Orange)){Icon(Icons.Default.Edit,null);Spacer(Modifier.width(7.dp));Text("Edit")};OutlinedButton(onClick={viewModel.delete(id,onDeleted)},modifier=Modifier.weight(1f),shape=RoundedCornerShape(15.dp)){Icon(Icons.Default.DeleteOutline,null);Spacer(Modifier.width(7.dp));Text("Delete")}}}
        }
    }
}

@Composable private fun DetailLine(label:String,value:String){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(label,color=SnoopColors.Stone);Text(value,fontWeight=FontWeight.SemiBold)}}

@Composable
private fun EditTransaction(
    x: TransactionEntity,
    cats: List<CategoryEntity>,
    onSave: (String, TransactionDirection, String, Long, String?) -> Unit,
    onCancel: () -> Unit
) {
    var amount by remember { mutableStateOf("%.2f".format(Locale.US, x.amountMinor / 100.0)) }
    var merchant by remember { mutableStateOf(x.merchantResolved ?: x.merchantRaw) }
    var note by remember { mutableStateOf(x.note.orEmpty()) }
    var direction by remember { mutableStateOf(x.direction) }
    var category by remember { mutableStateOf(x.categoryId ?: cats.firstOrNull()?.id) }
    Scaffold(containerColor=SnoopColors.Paper, topBar={ TopAppBar(title={Text("Edit transaction")}, colors=TopAppBarDefaults.topAppBarColors(containerColor=SnoopColors.Paper)) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(20.dp), verticalArrangement=Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(amount,{amount=it.filter{c->c.isDigit()||c=='.'}},label={Text("Amount (₹)")},singleLine=true,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp))
            OutlinedTextField(merchant,{merchant=it},label={Text("Merchant / person")},singleLine=true,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp))
            OutlinedTextField(note,{note=it},label={Text("Note (optional)")},minLines=2,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(15.dp))
            LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(cats,key={it.id}){c->FilterChip(category==c.id,{category=c.id},label={Text(c.name)})}}
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){FilterChip(direction==TransactionDirection.DEBIT,{direction=TransactionDirection.DEBIT},label={Text("Expense")});FilterChip(direction==TransactionDirection.CREDIT,{direction=TransactionDirection.CREDIT},label={Text("Income")});FilterChip(direction==TransactionDirection.TRANSFER,{direction=TransactionDirection.TRANSFER},label={Text("Transfer")})}
            Spacer(Modifier.weight(1f)); Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){OutlinedButton(onClick=onCancel,modifier=Modifier.weight(1f),shape=RoundedCornerShape(15.dp)){Text("Cancel")};Button(enabled=category!=null&&merchant.isNotBlank()&&amount.toDoubleOrNull()?.let{it>0}==true,onClick={onSave(amount,direction,merchant,category!!,note)},modifier=Modifier.weight(1f),shape=RoundedCornerShape(15.dp),colors=ButtonDefaults.buttonColors(containerColor=SnoopColors.Orange)){Text("Save changes")}}
        }
    }
}
