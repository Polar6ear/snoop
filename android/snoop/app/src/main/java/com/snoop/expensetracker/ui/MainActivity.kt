package com.snoop.expensetracker.ui

import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.snoop.expensetracker.security.SecurityPreferences
import com.snoop.expensetracker.ui.nav.SnoopNavHost
import com.snoop.expensetracker.ui.theme.SnoopTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var securityPreferences: SecurityPreferences

    private var unlocked = mutableStateOf(true)
    private var promptShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unlocked.value = !securityPreferences.biometricEnabled

        val composeView = ComposeView(this).apply {
            setContent {
                SnoopTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (unlocked.value) {
                            SnoopNavHost()
                        } else {
                            LockedScreen(
                                onUnlock = {
                                    authenticate()
                                }
                            )
                        }
                    }
                }
            }
        }

        setContentView(composeView)

        if (securityPreferences.biometricEnabled) {
            window.decorView.post {
                authenticate()
            }
        }
    }

    private fun authenticate() {
        if (promptShown) return

        promptShown = true

        val manager = BiometricManager.from(this)

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL

        val allowed = manager.canAuthenticate(authenticators)

        if (allowed != BiometricManager.BIOMETRIC_SUCCESS) {
            promptShown = false
            securityPreferences.biometricEnabled = false
            unlocked.value = true
            return
        }

        val executor = ContextCompat.getMainExecutor(this)

        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    runOnUiThread {
                        unlocked.value = true
                        promptShown = false
                    }
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    runOnUiThread {
                        promptShown = false
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )

        val promptInfo =
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock Snoop")
                .setSubtitle("Authenticate to view your expense data")
                .setAllowedAuthenticators(authenticators)
                .build()

        prompt.authenticate(promptInfo)
    }

    override fun onResume() {
        super.onResume()

        if (
            securityPreferences.biometricEnabled &&
            !unlocked.value
        ) {
            authenticate()
        }
    }

    override fun onPause() {
        super.onPause()

        if (securityPreferences.biometricEnabled) {
            unlocked.value = false
            promptShown = false
        }
    }
}

@Composable
private fun LockedScreen(
    onUnlock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Snoop is locked",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Authenticate to access your expense data.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onUnlock
        ) {
            Text("Unlock")
        }
    }
}