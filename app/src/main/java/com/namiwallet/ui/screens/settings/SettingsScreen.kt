package com.namiwallet.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namiwallet.ui.components.*
import com.namiwallet.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMnemonicDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NamiTopBar(
                title = "Settings",
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
            ) {
                // Security Section
                SettingsSection(title = "Security") {
                    if (uiState.biometricAvailable) {
                        SettingsToggleItem(
                            icon = Icons.Default.Fingerprint,
                            title = "Biometric Authentication",
                            subtitle = "Use fingerprint or face to unlock",
                            checked = uiState.biometricEnabled,
                            onCheckedChange = { viewModel.toggleBiometric(it) }
                        )
                    }

                    SettingsClickableItem(
                        icon = Icons.Default.Key,
                        title = "View Recovery Phrase",
                        subtitle = "Show your 12/24 word recovery phrase",
                        onClick = {
                            viewModel.showMnemonic { showMnemonicDialog = true }
                        }
                    )
                }

                // Appearance Section
                SettingsSection(title = "Appearance") {
                    SettingsClickableItem(
                        icon = Icons.Default.DarkMode,
                        title = "Theme",
                        subtitle = when (uiState.selectedTheme) {
                            "light" -> "Light"
                            "dark" -> "Dark"
                            else -> "System default"
                        },
                        onClick = { showThemeDialog = true }
                    )

                    SettingsClickableItem(
                        icon = Icons.Default.AttachMoney,
                        title = "Currency",
                        subtitle = uiState.selectedCurrency,
                        onClick = { /* TODO: Currency selector */ }
                    )
                }

                // About Section
                SettingsSection(title = "About") {
                    SettingsClickableItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = "1.0.0",
                        showArrow = false,
                        onClick = { }
                    )

                    SettingsClickableItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = { /* TODO: Open terms */ }
                    )

                    SettingsClickableItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = { /* TODO: Open privacy */ }
                    )
                }

                // Danger Zone
                SettingsSection(title = "Danger Zone") {
                    SettingsClickableItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Delete Wallet",
                        subtitle = "Remove wallet and all data",
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteDialog = true }
                    )
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

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Delete Wallet") },
            text = {
                Text("This will permanently delete your wallet. Make sure you have backed up your recovery phrase. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteWallet(onLogout)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Mnemonic dialog
    if (showMnemonicDialog && uiState.mnemonic != null) {
        AlertDialog(
            onDismissRequest = {
                showMnemonicDialog = false
                viewModel.hideMnemonic()
            },
            title = { Text("Recovery Phrase") },
            text = {
                Column {
                    Text(
                        text = "Keep this phrase safe. Anyone with these words can access your wallet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MnemonicGrid(words = uiState.mnemonic!!.split(" "))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMnemonicDialog = false
                        viewModel.hideMnemonic()
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Theme dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Theme") },
            text = {
                Column {
                    ThemeOption(
                        title = "System default",
                        selected = uiState.selectedTheme == "system",
                        onClick = {
                            viewModel.setTheme("system")
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        title = "Light",
                        selected = uiState.selectedTheme == "light",
                        onClick = {
                            viewModel.setTheme("light")
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        title = "Dark",
                        selected = uiState.selectedTheme == "dark",
                        onClick = {
                            viewModel.setTheme("dark")
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = { }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        content()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title)
    }
}
