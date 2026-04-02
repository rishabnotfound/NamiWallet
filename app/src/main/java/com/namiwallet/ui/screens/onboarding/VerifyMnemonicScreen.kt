package com.namiwallet.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namiwallet.ui.components.*
import com.namiwallet.viewmodel.VerifyMnemonicViewModel

@Composable
fun VerifyMnemonicScreen(
    mnemonic: String,
    onVerified: () -> Unit,
    onBack: () -> Unit,
    viewModel: VerifyMnemonicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mnemonic) {
        viewModel.initializeMnemonic(mnemonic)
    }

    Scaffold(
        topBar = {
            NamiTopBar(
                title = "Verify Recovery Phrase",
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
                    text = "Let's verify your recovery phrase",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select the correct words from the options below to confirm you've saved your recovery phrase.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Verification slots
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.verificationIndices.forEachIndexed { idx, wordIndex ->
                        VerificationSlot(
                            wordNumber = wordIndex + 1,
                            selectedWord = uiState.selectedWords.getOrNull(idx) ?: "",
                            onClear = { viewModel.clearWord(idx) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Available words",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Word options grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.shuffledOptions) { word ->
                        val isSelected = uiState.selectedWords.contains(word)
                        WordOption(
                            word = word,
                            isSelected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    // Find first empty slot
                                    val emptySlotIndex = uiState.selectedWords.indexOfFirst { it.isEmpty() }
                                    if (emptySlotIndex != -1) {
                                        viewModel.selectWord(emptySlotIndex, word)
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "Verify",
                    onClick = {
                        if (viewModel.verify()) {
                            onVerified()
                        }
                    },
                    enabled = uiState.selectedWords.all { it.isNotEmpty() }
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
private fun VerificationSlot(
    wordNumber: Int,
    selectedWord: String,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (selectedWord.isNotEmpty())
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                if (selectedWord.isNotEmpty())
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                else
                    MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Word #$wordNumber",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        if (selectedWord.isNotEmpty()) {
            Text(
                text = selectedWord,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = "Select a word",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun WordOption(
    word: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = !isSelected) { onClick() },
        color = if (isSelected)
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else
            MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
