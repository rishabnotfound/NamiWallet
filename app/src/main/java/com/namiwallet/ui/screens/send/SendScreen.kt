package com.namiwallet.ui.screens.send

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namiwallet.ui.components.*
import com.namiwallet.viewmodel.SendViewModel

@Composable
fun SendScreen(
    initialChain: String?,
    onTransactionSent: () -> Unit,
    onBack: () -> Unit,
    viewModel: SendViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialChain) {
        viewModel.setInitialChain(initialChain)
    }

    Scaffold(
        topBar = {
            NamiTopBar(
                title = "Send ${uiState.selectedChain.symbol}",
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
                // Chain selector
                Text(
                    text = "Network",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ChainSelector(
                    selectedChain = uiState.selectedChain,
                    onChainSelected = { viewModel.selectChain(it) },
                    modifier = Modifier.padding(horizontal = 0.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Recipient address
                Text(
                    text = "Recipient Address",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                NamiTextField(
                    value = uiState.recipientAddress,
                    onValueChange = { viewModel.updateRecipient(it) },
                    placeholder = "Enter ${uiState.selectedChain.symbol} address",
                    isError = uiState.isAddressValid == false,
                    errorMessage = if (uiState.isAddressValid == false) "Invalid address" else null,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: QR Scanner */ }) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR"
                            )
                        }
                    },
                    singleLine = false,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.titleSmall
                    )
                    TextButton(onClick = { viewModel.setMaxAmount() }) {
                        Text("Max: ${uiState.availableBalance} ${uiState.selectedChain.symbol}")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                NamiTextField(
                    value = uiState.amount,
                    onValueChange = { viewModel.updateAmount(it) },
                    placeholder = "0.0",
                    keyboardType = KeyboardType.Decimal,
                    isError = uiState.isAmountValid == false,
                    errorMessage = if (uiState.isAmountValid == false) "Invalid amount" else null,
                    trailingIcon = {
                        Text(
                            text = uiState.selectedChain.symbol,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Summary card
                if (uiState.recipientAddress.isNotEmpty() && uiState.amount.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Transaction Summary",
                                style = MaterialTheme.typography.titleSmall
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            SummaryRow(
                                label = "Sending",
                                value = "${uiState.amount} ${uiState.selectedChain.symbol}"
                            )
                            SummaryRow(
                                label = "To",
                                value = uiState.recipientAddress.take(10) + "..." + uiState.recipientAddress.takeLast(8)
                            )
                            SummaryRow(
                                label = "Network",
                                value = uiState.selectedChain.displayName
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "Send ${uiState.selectedChain.symbol}",
                    onClick = { viewModel.sendTransaction(onTransactionSent) },
                    enabled = uiState.isAddressValid == true && uiState.isAmountValid == true,
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

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
