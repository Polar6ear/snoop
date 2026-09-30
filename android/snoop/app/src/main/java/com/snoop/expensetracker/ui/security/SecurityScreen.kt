@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.snoop.expensetracker.ui.security

import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.snoop.expensetracker.security.SecurityPreferences
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(private val prefs: SecurityPreferences) : ViewModel() {
    var biometricEnabled by mutableStateOf(prefs.biometricEnabled); private set
    var minimizeRawData by mutableStateOf(prefs.minimizeRawData); private set
    fun setBiometric(v:Boolean){ prefs.biometricEnabled=v; biometricEnabled=v }
    fun setRaw(v:Boolean){ prefs.minimizeRawData=v; minimizeRawData=v }
}

@Composable
fun SecurityScreen(onBack:()->Unit, vm: SecurityViewModel = hiltViewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val biometricAvailable = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    Scaffold(topBar={TopAppBar(title={Text("Security & privacy")}, navigationIcon={TextButton(onClick=onBack){Text("Back")}})}) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(20.dp), verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Text("Security", style=MaterialTheme.typography.titleLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)){Text("Biometric app lock"); Text(if (biometricAvailable) "Require fingerprint/face when opening Snoop" else "Set up a screen lock or biometric on this device first", style=MaterialTheme.typography.bodySmall)}
                Switch(checked=vm.biometricEnabled, enabled=biometricAvailable, onCheckedChange=vm::setBiometric)
            }
            HorizontalDivider()
            Text("Privacy", style=MaterialTheme.typography.titleLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)){Text("Minimize stored notification text"); Text("New transactions won't retain the original notification body", style=MaterialTheme.typography.bodySmall)}
                Switch(checked=vm.minimizeRawData, onCheckedChange=vm::setRaw)
            }
            Text("Snoop processes notifications locally first. AI fallback is only used when local parsing cannot extract a transaction.", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
