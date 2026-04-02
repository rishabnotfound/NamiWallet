package com.namiwallet.ui.screens.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namiwallet.repository.SupportedChain
import com.namiwallet.ui.components.*
import com.namiwallet.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSendClick: (String?) -> Unit,
    onReceiveClick: (String?) -> Unit,
    onTransactionClick: (String, String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NamiWallet") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
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
            ) {
                // Total Balance Section
                TotalBalanceCard(
                    balances = uiState.balances,
                    isLoading = uiState.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionButton(
                        icon = Icons.AutoMirrored.Filled.CallMade,
                        label = "Send",
                        onClick = { onSendClick(uiState.selectedChain.name) },
                        modifier = Modifier.weight(1f)
                    )
                    ActionButton(
                        icon = Icons.AutoMirrored.Filled.CallReceived,
                        label = "Receive",
                        onClick = { onReceiveClick(uiState.selectedChain.name) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Chain Selector
                ChainSelector(
                    selectedChain = uiState.selectedChain,
                    onChainSelected = { viewModel.selectChain(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Balance Cards
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SupportedChain.entries.forEach { chain ->
                        val balance = when (chain) {
                            SupportedChain.ETHEREUM -> uiState.balances?.ethereum ?: "0"
                            SupportedChain.BSC -> uiState.balances?.bsc ?: "0"
                            SupportedChain.BITCOIN -> uiState.balances?.bitcoin ?: "0"
                        }

                        BalanceCard(
                            chain = chain,
                            balance = balance,
                            onClick = { viewModel.selectChain(chain) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Address display for selected chain
                uiState.addresses?.let { addresses ->
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "${uiState.selectedChain.displayName} Address",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val address = when (uiState.selectedChain) {
                            SupportedChain.ETHEREUM -> addresses.ethereum
                            SupportedChain.BSC -> addresses.bsc
                            SupportedChain.BITCOIN -> addresses.bitcoin
                        }

                        AddressDisplay(address = address)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Loading overlay
            if (uiState.isLoading) {
                LoadingOverlay(isLoading = true)
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
private fun TotalBalanceCard(
    balances: com.namiwallet.repository.WalletBalances?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp
                )
            } else {
                // Display balances summary
                Text(
                    text = buildString {
                        append("${balances?.ethereum ?: "0"} ETH")
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}
