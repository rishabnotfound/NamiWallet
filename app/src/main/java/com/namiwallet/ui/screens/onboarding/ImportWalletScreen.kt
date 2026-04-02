package com.namiwallet.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namiwallet.ui.components.*
import com.namiwallet.viewmodel.ImportWalletViewModel

@Composable
fun ImportWalletScreen(
    onWalletImported: () -> Unit,
    onBack: () -> Unit,
    viewModel: ImportWalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            NamiTopBar(
                title = "Import Wallet",
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
                Text(
                    text = "Enter your recovery phrase",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter the 12 or 24 words you were given when you created your wallet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle between text input and word-by-word
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { viewModel.toggleInputMode() }) {
                        Text(
                            text = if (uiState.useTextInput) "Enter word by word" else "Paste full phrase",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.useTextInput) {
                    // Text area input
                    OutlinedTextField(
                        value = uiState.mnemonicText,
                        onValueChange = { viewModel.updateMnemonicText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        placeholder = { Text("Enter your recovery phrase...") },
                        isError = uiState.isValid == false,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                } else {
                    // Word by word input
                    WordInputGrid(
                        words = uiState.words,
                        onWordChange = { index, word -> viewModel.updateWord(index, word) },
                        onSuggestionRequest = { viewModel.getWordSuggestions(it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Optional passphrase
                Text(
                    text = "Passphrase (optional)",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                NamiTextField(
                    value = uiState.passphrase,
                    onValueChange = { viewModel.updatePassphrase(it) },
                    placeholder = "Additional passphrase",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "If you used an additional passphrase when creating your wallet, enter it here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                // Validation status
                if (uiState.isValid == true) {
                    Text(
                        text = "Valid recovery phrase",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                PrimaryButton(
                    text = "Import Wallet",
                    onClick = { viewModel.importWallet(onWalletImported) },
                    enabled = uiState.words.count { it.isNotEmpty() } >= 12,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordInputGrid(
    words: List<String>,
    onWordChange: (Int, String) -> Unit,
    onSuggestionRequest: (String) -> List<String>
) {
    val displayWords = if (words.size > 12 && words.drop(12).any { it.isNotEmpty() }) {
        words.take(24)
    } else {
        words.take(12)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        displayWords.chunked(3).forEachIndexed { rowIndex, rowWords ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowWords.forEachIndexed { colIndex, word ->
                    val index = rowIndex * 3 + colIndex
                    WordInputField(
                        index = index,
                        word = word,
                        onWordChange = { onWordChange(index, it) },
                        onSuggestionRequest = onSuggestionRequest,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowWords.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordInputField(
    index: Int,
    word: String,
    onWordChange: (String) -> Unit,
    onSuggestionRequest: (String) -> List<String>,
    modifier: Modifier = Modifier
) {
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && suggestions.isNotEmpty(),
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = word,
            onValueChange = { newValue ->
                onWordChange(newValue)
                suggestions = onSuggestionRequest(newValue)
                expanded = suggestions.isNotEmpty()
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text("${index + 1}") },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium
        )

        ExposedDropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = {
                        onWordChange(suggestion)
                        expanded = false
                        suggestions = emptyList()
                    }
                )
            }
        }
    }
}
