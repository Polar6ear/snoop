package com.snoop.expensetracker.ui.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.snoop.expensetracker.security.UserProfilePreferences
import com.snoop.expensetracker.ui.common.snoopEmojiChoices
import com.snoop.expensetracker.ui.theme.SnoopColors
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(private val prefs: UserProfilePreferences) : ViewModel() {
    private val _name = MutableStateFlow(prefs.displayName)
    val name: StateFlow<String> = _name
    private val _avatar = MutableStateFlow(prefs.avatarEmoji)
    val avatar: StateFlow<String> = _avatar
    fun saveProfile(name: String, avatar: String) {
        prefs.displayName = name
        prefs.avatarEmoji = avatar
        _name.value = prefs.displayName
        _avatar.value = prefs.avatarEmoji
    }
}

@Composable
fun MoreScreen(
    onCategories: () -> Unit, onMerchantRules: () -> Unit, onRecurring: () -> Unit,
    onPortability: () -> Unit, onSecurity: () -> Unit, onPending: () -> Unit,
    viewModel: MoreViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val avatar by viewModel.avatar.collectAsState()
    var profileOpen by remember { mutableStateOf(false) }
    Scaffold(containerColor = SnoopColors.Paper) { p ->
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp, p.calculateTopPadding() + 12.dp, 20.dp, 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(onClick = { profileOpen = true }, shape = RoundedCornerShape(24.dp), color = SnoopColors.Ink) {
                    Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = SnoopColors.Purple) {
                            Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) { Text(avatar, style = MaterialTheme.typography.headlineMedium) }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(name, color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                            Text("Local-first expense tracking", color = SnoopColors.PaperDim, style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(Icons.Default.Edit, "Edit profile", tint = SnoopColors.PaperDim)
                    }
                }
            }
            item { SectionLabel("TOOLS") }
            item { MoreRow("Needs review", "Transactions Snoop isn't sure about", Icons.Default.AutoAwesome, SnoopColors.Coral, onPending) }
            item { MoreRow("Merchant rules", "Teach Snoop your habits", Icons.Default.Tune, SnoopColors.Teal, onMerchantRules) }
            item { MoreRow("Recurring payments", "Subscriptions and regular bills", Icons.Default.Repeat, SnoopColors.Amber, onRecurring) }
            item { MoreRow("Categories", "Defaults, emojis and custom categories", Icons.Default.Category, SnoopColors.Lime, onCategories) }
            item { SectionLabel("ACCOUNT") }
            item { MoreRow("Security & privacy", "Permissions, lock and data controls", Icons.Default.Security, SnoopColors.Ink, onSecurity) }
            item { MoreRow("Export & backup", "Keep a copy of your data", Icons.Default.FileDownload, SnoopColors.Blue, onPortability) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, null, tint = SnoopColors.Stone, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Local-first · Snoop", style = MaterialTheme.typography.bodySmall, color = SnoopColors.Stone)
                }
            }
        }
    }
    if (profileOpen) {
        ProfileDialog(name, avatar, { profileOpen = false }) { n, a -> viewModel.saveProfile(n, a); profileOpen = false }
    }
}

@Composable
private fun ProfileDialog(name0: String, avatar0: String, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var name by remember(name0) { mutableStateOf(name0) }
    var avatar by remember(avatar0) { mutableStateOf(avatar0) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Text("Avatar", color = SnoopColors.Stone, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(snoopEmojiChoices.take(12)) { e -> FilterChip(avatar == e, { avatar = e }, label = { Text(e) }) }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(name, avatar) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = SnoopColors.Stone, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
}

@Composable private fun MoreRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = SnoopColors.Paper, border = androidx.compose.foundation.BorderStroke(1.dp, SnoopColors.PaperDim)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(13.dp), color = accent.copy(alpha = if (accent == SnoopColors.Lime) .75f else .14f)) {
                Icon(icon, null, tint = if (accent == SnoopColors.Lime) SnoopColors.Ink else accent, modifier = Modifier.padding(10.dp).size(21.dp))
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = SnoopColors.Stone) }
            Icon(Icons.Default.ChevronRight, null, tint = SnoopColors.Stone)
        }
    }
}
