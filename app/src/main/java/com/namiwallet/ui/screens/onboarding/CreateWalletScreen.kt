package com.namiwallet.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namiwallet.ui.components.*
import com.namiwallet.ui.theme.WarningYellow
import com.namiwallet.viewmodel.CreateWalletViewModel

@Composable
fun CreateWalletScreen(
    onMnemonicGenerated: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CreateWalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.mnemonic.isEmpty()) {
            viewModel.generateMnemonic()
        }
    }

    Scaffold(
        topBar = {
            NamiTopBar(
                title = "Create Wallet",
                onBackClick = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Warning card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = WarningYellow.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningYellow
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Write down your recovery phrase",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "This is the only way to recover your wallet. Keep it safe and never share it with anyone.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Word count toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recovery Phrase",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = { viewModel.toggleWordCount() }) {
                        Text(
                            text = if (uiState.use24Words) "Use 12 words" else "Use 24 words",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mnemonic grid
                if (uiState.mnemonicWords.isNotEmpty()) {
                    MnemonicGrid(words = uiState.mnemonicWords)
                } else if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Checkbox for acknowledgment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.backupAcknowledged,
                        onCheckedChange = { if (it) viewModel.acknowledgeBackup() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I have written down my recovery phrase and stored it securely",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "Continue",
                    onClick = { onMnemonicGenerated(uiState.mnemonic) },
                    enabled = uiState.backupAcknowledged && uiState.mnemonic.isNotEmpty(),
                    loading = uiState.isLoading
                )
            }

            // Error dialog
            if (uiState.error != null) {
                ErrorDialog(
                    message = uiState.error!!,
                    onDismiss = { viewModel.clearError() }
                )
            }
        }
    }
}
