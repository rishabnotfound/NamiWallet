package com.namiwallet.ui.screens.receive

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namiwallet.ui.components.*
import com.namiwallet.viewmodel.ReceiveViewModel

@Composable
fun ReceiveScreen(
    initialChain: String?,
    onBack: () -> Unit,
    viewModel: ReceiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(initialChain) {
        viewModel.setInitialChain(initialChain)
    }

    Scaffold(
        topBar = {
            NamiTopBar(
                title = "Receive ${uiState.selectedChain.symbol}",
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Chain selector
                ChainSelector(
                    selectedChain = uiState.selectedChain,
                    onChainSelected = { viewModel.selectChain(it) },
                    modifier = Modifier.padding(horizontal = 0.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // QR Code
                if (uiState.displayedAddress.isNotEmpty()) {
                    Card(
                        modifier = Modifier.size(280.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            QRCodeImage(
                                content = uiState.displayedAddress,
                                size = 248
                            )
                        }
                    }
                } else if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.size(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Network label
                Text(
                    text = uiState.selectedChain.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Warning text
                Text(
                    text = "Only send ${uiState.selectedChain.symbol} to this address",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Address display
                if (uiState.displayedAddress.isNotEmpty()) {
                    AddressDisplay(
                        address = uiState.displayedAddress,
                        showCopyButton = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = "Copy",
                        onClick = {
                            clipboardManager.setText(AnnotatedString(uiState.displayedAddress))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, uiState.displayedAddress)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Address"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Important notice
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
                            text = "Important",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (uiState.selectedChain) {
                                com.namiwallet.repository.SupportedChain.BITCOIN ->
                                    "This is a Bitcoin address. Only send BTC to this address."
                                com.namiwallet.repository.SupportedChain.ETHEREUM ->
                                    "This address works on Ethereum mainnet. Send ETH or ERC-20 tokens."
                                com.namiwallet.repository.SupportedChain.BSC ->
                                    "This address works on BNB Smart Chain. Send BNB or BEP-20 tokens."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
