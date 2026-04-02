package com.namiwallet.ui.screens.wallet

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.namiwallet.repository.SupportedChain
import com.namiwallet.ui.components.*
import com.namiwallet.ui.theme.SuccessGreen
import com.namiwallet.ui.theme.WarningYellow

@Composable
fun TransactionDetailScreen(
    txHash: String,
    chain: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val supportedChain = try {
        SupportedChain.valueOf(chain)
    } catch (e: Exception) {
        SupportedChain.ETHEREUM
    }

    // Simulated transaction state - in production this would come from ViewModel
    val txStatus = TransactionStatus.CONFIRMED

    Scaffold(
        topBar = {
            NamiTopBar(
                title = "Transaction Details",
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status icon
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                when (txStatus) {
                    TransactionStatus.CONFIRMED -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Confirmed",
                            modifier = Modifier.size(80.dp),
                            tint = SuccessGreen
                        )
                    }
                    TransactionStatus.PENDING -> {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Pending",
                            modifier = Modifier.size(80.dp),
                            tint = WarningYellow
                        )
                    }
                    TransactionStatus.FAILED -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Failed",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status text
            Text(
                text = when (txStatus) {
                    TransactionStatus.CONFIRMED -> "Transaction Confirmed"
                    TransactionStatus.PENDING -> "Transaction Pending"
                    TransactionStatus.FAILED -> "Transaction Failed"
                },
                style = MaterialTheme.typography.titleLarge,
                color = when (txStatus) {
                    TransactionStatus.CONFIRMED -> SuccessGreen
                    TransactionStatus.PENDING -> WarningYellow
                    TransactionStatus.FAILED -> MaterialTheme.colorScheme.error
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Transaction details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    DetailRow(label = "Network", value = supportedChain.displayName)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    DetailRow(label = "Status", value = txStatus.name.lowercase().replaceFirstChar { it.uppercase() })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Transaction hash with copy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Transaction Hash",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = txHash.take(16) + "..." + txHash.takeLast(16),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(txHash))
                            }
                        ) {
                            Text("Copy")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // View on explorer button
            OutlinedButton(
                onClick = {
                    val explorerUrl = when (supportedChain) {
                        SupportedChain.ETHEREUM -> "https://etherscan.io/tx/$txHash"
                        SupportedChain.BSC -> "https://bscscan.com/tx/$txHash"
                        SupportedChain.BITCOIN -> "https://blockstream.info/tx/$txHash"
                    }
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(explorerUrl)))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("View on Explorer")
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Done",
                onClick = onBack
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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

enum class TransactionStatus {
    PENDING,
    CONFIRMED,
    FAILED
}
