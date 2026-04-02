package com.namiwallet.repository

import com.namiwallet.bridge.*
import com.namiwallet.network.*
import com.namiwallet.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for wallet operations, combining native crypto engine with network queries.
 */
@Singleton
class WalletRepository @Inject constructor(
    private val namiCore: NamiCore,
    private val secureStorage: SecureStorage,
    private val ethereumRpcClient: EthereumRpcClient,
    private val bitcoinElectrumClient: BitcoinElectrumClient
) {
    // ========================================================================
    // Wallet Setup
    // ========================================================================

    /**
     * Create a new wallet with a generated mnemonic
     */
    suspend fun createWallet(use24Words: Boolean = false): Result<String> = withContext(Dispatchers.IO) {
        val mnemonicResult = if (use24Words) {
            namiCore.generateMnemonic24()
        } else {
            namiCore.generateMnemonic12()
        }

        mnemonicResult.fold(
            onSuccess = { mnemonic ->
                secureStorage.storeMnemonic(mnemonic).fold(
                    onSuccess = {
                        secureStorage.setWalletSetupComplete(true)
                        Result.success(mnemonic)
                    },
                    onFailure = { Result.failure(it) }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }

    /**
     * Import wallet from mnemonic
     */
    suspend fun importWallet(mnemonic: String, passphrase: String = ""): Result<Unit> = withContext(Dispatchers.IO) {
        if (!namiCore.validateMnemonic(mnemonic)) {
            return@withContext Result.failure(WalletException("Invalid mnemonic phrase"))
        }

        secureStorage.storeMnemonic(mnemonic).fold(
            onSuccess = {
                if (passphrase.isNotEmpty()) {
                    secureStorage.storePassphrase(passphrase)
                }
                secureStorage.setWalletSetupComplete(true)
                Result.success(Unit)
            },
            onFailure = { Result.failure(it) }
        )
    }

    /**
     * Check if wallet exists
     */
    fun hasWallet(): Boolean = secureStorage.hasMnemonic()

    /**
     * Check if wallet setup is complete
     */
    fun isWalletSetupComplete(): Boolean = secureStorage.isWalletSetupComplete()

    // ========================================================================
    // Address Generation
    // ========================================================================

    /**
     * Get all addresses for the wallet
     */
    suspend fun getAllAddresses(account: Int = 0, index: Int = 0): Result<WalletAddresses> = withContext(Dispatchers.IO) {
        val mnemonic = secureStorage.getMnemonic().getOrNull()
            ?: return@withContext Result.failure(WalletException("No wallet found"))
        val passphrase = secureStorage.getPassphrase()

        val ethAddress = namiCore.getEthereumAddress(mnemonic, passphrase, account, index)
        val btcAddress = namiCore.getBitcoinAddress(mnemonic, passphrase, false, account, index)

        if (ethAddress.isFailure || btcAddress.isFailure) {
            return@withContext Result.failure(WalletException("Failed to derive addresses"))
        }

        Result.success(WalletAddresses(
            ethereum = ethAddress.getOrThrow(),
            bsc = ethAddress.getOrThrow(), // Same address for BSC
            bitcoin = btcAddress.getOrThrow()
        ))
    }

    /**
     * Get address for specific chain
     */
    suspend fun getAddress(chain: SupportedChain, account: Int = 0, index: Int = 0): Result<String> = withContext(Dispatchers.IO) {
        val mnemonic = secureStorage.getMnemonic().getOrNull()
            ?: return@withContext Result.failure(WalletException("No wallet found"))
        val passphrase = secureStorage.getPassphrase()

        when (chain) {
            SupportedChain.ETHEREUM, SupportedChain.BSC -> {
                namiCore.getEthereumAddress(mnemonic, passphrase, account, index)
            }
            SupportedChain.BITCOIN -> {
                namiCore.getBitcoinAddress(mnemonic, passphrase, false, account, index)
            }
        }
    }

    // ========================================================================
    // Balance Queries
    // ========================================================================

    /**
     * Get all balances as a flow for real-time updates
     */
    fun getAllBalances(account: Int = 0, index: Int = 0): Flow<Result<WalletBalances>> = flow {
        val addresses = getAllAddresses(account, index).getOrNull()
            ?: run {
                emit(Result.failure(WalletException("Failed to get addresses")))
                return@flow
            }

        // Fetch all balances concurrently
        val ethBalance = ethereumRpcClient.getBalance(addresses.ethereum, EvmChain.ETHEREUM_MAINNET)
        val bscBalance = ethereumRpcClient.getBalance(addresses.bsc, EvmChain.BSC_MAINNET)
        val btcBalance = bitcoinElectrumClient.getBalance(addresses.bitcoin, false)

        val balances = WalletBalances(
            ethereum = ethBalance.getOrNull()?.let { formatEthBalance(it) } ?: "0",
            ethereumWei = ethBalance.getOrNull() ?: BigInteger.ZERO,
            bsc = bscBalance.getOrNull()?.let { formatEthBalance(it) } ?: "0",
            bscWei = bscBalance.getOrNull() ?: BigInteger.ZERO,
            bitcoin = btcBalance.getOrNull()?.let { formatBtcBalance(it.total) } ?: "0",
            bitcoinSatoshi = btcBalance.getOrNull()?.total ?: 0
        )

        emit(Result.success(balances))
    }

    /**
     * Get balance for a specific chain
     */
    suspend fun getBalance(chain: SupportedChain, account: Int = 0, index: Int = 0): Result<String> = withContext(Dispatchers.IO) {
        val address = getAddress(chain, account, index).getOrNull()
            ?: return@withContext Result.failure(WalletException("Failed to get address"))

        when (chain) {
            SupportedChain.ETHEREUM -> {
                ethereumRpcClient.getBalance(address, EvmChain.ETHEREUM_MAINNET).map { formatEthBalance(it) }
            }
            SupportedChain.BSC -> {
                ethereumRpcClient.getBalance(address, EvmChain.BSC_MAINNET).map { formatEthBalance(it) }
            }
            SupportedChain.BITCOIN -> {
                bitcoinElectrumClient.getBalance(address, false).map { formatBtcBalance(it.total) }
            }
        }
    }

    // ========================================================================
    // Transaction Signing and Sending
    // ========================================================================

    /**
     * Send Ethereum or BSC transaction
     */
    suspend fun sendEvmTransaction(
        chain: SupportedChain,
        to: String,
        amount: String,
        data: String? = null,
        account: Int = 0,
        index: Int = 0
    ): Result<String> = withContext(Dispatchers.IO) {
        val mnemonic = secureStorage.getMnemonic().getOrNull()
            ?: return@withContext Result.failure(WalletException("No wallet found"))
        val passphrase = secureStorage.getPassphrase()

        val evmChain = when (chain) {
            SupportedChain.ETHEREUM -> EvmChain.ETHEREUM_MAINNET
            SupportedChain.BSC -> EvmChain.BSC_MAINNET
            else -> return@withContext Result.failure(WalletException("Invalid chain for EVM transaction"))
        }

        val address = getAddress(chain, account, index).getOrNull()
            ?: return@withContext Result.failure(WalletException("Failed to get address"))

        // Parse amount to wei
        val weiAmount = namiCore.parseEther(amount).getOrNull()?.let { BigInteger(it) }
            ?: return@withContext Result.failure(WalletException("Invalid amount"))

        // Get nonce
        val nonce = ethereumRpcClient.getNonce(address, evmChain).getOrNull()
            ?: return@withContext Result.failure(WalletException("Failed to get nonce"))

        // Estimate gas
        val gasLimit = ethereumRpcClient.estimateGas(address, to, weiAmount, data, evmChain).getOrNull()
            ?: return@withContext Result.failure(WalletException("Failed to estimate gas"))

        // Get gas prices
        val gasPrices = ethereumRpcClient.getGasPrices(evmChain).getOrNull()
            ?: return@withContext Result.failure(WalletException("Failed to get gas prices"))

        // Build and sign transaction
        val tx = Eip1559Transaction(
            chainId = evmChain.chainId,
            nonce = nonce,
            maxPriorityFeePerGas = gasPrices.maxPriorityFeePerGas.toString(),
            maxFeePerGas = gasPrices.maxFeePerGas.toString(),
            gasLimit = gasLimit,
            to = to,
            value = weiAmount.toString(),
            data = data
        )

        val signedTx = namiCore.signEthereumTransaction(mnemonic, passphrase, account, index, tx)
            .getOrNull() ?: return@withContext Result.failure(WalletException("Failed to sign transaction"))

        // Broadcast
        ethereumRpcClient.sendRawTransaction(signedTx.rawTx, evmChain)
    }

    /**
     * Send Bitcoin transaction
     */
    suspend fun sendBitcoinTransaction(
        to: String,
        amountSatoshi: Long,
        feeRate: Long? = null,
        account: Int = 0,
        index: Int = 0
    ): Result<String> = withContext(Dispatchers.IO) {
        val mnemonic = secureStorage.getMnemonic().getOrNull()
            ?: return@withContext Result.failure(WalletException("No wallet found"))
        val passphrase = secureStorage.getPassphrase()

        val address = getAddress(SupportedChain.BITCOIN, account, index).getOrNull()
            ?: return@withContext Result.failure(WalletException("Failed to get address"))

        // Get UTXOs
        val utxos = bitcoinElectrumClient.getUtxos(address, false).getOrNull()
            ?: return@withContext Result.failure(WalletException("Failed to get UTXOs"))

        // Get fee estimate
        val fees = bitcoinElectrumClient.getFeeEstimates(false).getOrNull()
            ?: FeeEstimates(20, 10, 5)
        val selectedFeeRate = feeRate ?: fees.medium

        // Select UTXOs and calculate change
        val inputs = mutableListOf<BitcoinInput>()
        var totalInput = 0L

        for (utxo in utxos.sortedByDescending { it.amount }) {
            inputs.add(BitcoinInput(utxo.txid, utxo.vout, utxo.amount))
            totalInput += utxo.amount

            // Estimate transaction size and fee
            val estimatedSize = 10 + (inputs.size * 68) + (2 * 31) // 2 outputs: recipient + change
            val estimatedFee = estimatedSize * selectedFeeRate

            if (totalInput >= amountSatoshi + estimatedFee) {
                break
            }
        }

        val estimatedSize = 10 + (inputs.size * 68) + (2 * 31)
        val fee = estimatedSize * selectedFeeRate
        val change = totalInput - amountSatoshi - fee

        if (change < 0) {
            return@withContext Result.failure(WalletException("Insufficient balance"))
        }

        // Build outputs
        val outputs = mutableListOf(BitcoinOutput(to, amountSatoshi))
        if (change > 546) { // Dust threshold
            outputs.add(BitcoinOutput(address, change))
        }

        // Sign transaction
        val signedTx = namiCore.signBitcoinTransaction(
            mnemonic, passphrase, false, account, index, inputs, outputs
        ).getOrNull() ?: return@withContext Result.failure(WalletException("Failed to sign transaction"))

        // Broadcast
        bitcoinElectrumClient.broadcastTransaction(signedTx.rawTx, false)
    }

    // ========================================================================
    // Validation
    // ========================================================================

    /**
     * Validate an address for a specific chain
     */
    fun validateAddress(address: String, chain: SupportedChain): Boolean {
        return when (chain) {
            SupportedChain.ETHEREUM, SupportedChain.BSC -> namiCore.validateEthereumAddress(address)
            SupportedChain.BITCOIN -> namiCore.validateBitcoinAddress(address, false)
        }
    }

    // ========================================================================
    // Mnemonic Operations
    // ========================================================================

    /**
     * Get the mnemonic for backup (requires authentication)
     */
    suspend fun getMnemonicForBackup(): Result<String> = withContext(Dispatchers.IO) {
        secureStorage.getMnemonic().fold(
            onSuccess = { mnemonic ->
                if (mnemonic != null) {
                    Result.success(mnemonic)
                } else {
                    Result.failure(WalletException("No wallet found"))
                }
            },
            onFailure = { Result.failure(it) }
        )
    }

    /**
     * Delete wallet and all data
     */
    suspend fun deleteWallet(): Result<Unit> = withContext(Dispatchers.IO) {
        secureStorage.clearAll()
    }

    // ========================================================================
    // Helper Functions
    // ========================================================================

    private fun formatEthBalance(wei: BigInteger): String {
        val eth = BigDecimal(wei).divide(BigDecimal("1000000000000000000"))
        return eth.stripTrailingZeros().toPlainString()
    }

    private fun formatBtcBalance(satoshi: Long): String {
        val btc = BigDecimal(satoshi).divide(BigDecimal("100000000"))
        return btc.stripTrailingZeros().toPlainString()
    }
}

// ============================================================================
// Data Classes
// ============================================================================

enum class SupportedChain(val displayName: String, val symbol: String) {
    ETHEREUM("Ethereum", "ETH"),
    BSC("BNB Smart Chain", "BNB"),
    BITCOIN("Bitcoin", "BTC")
}

data class WalletAddresses(
    val ethereum: String,
    val bsc: String,
    val bitcoin: String
)

data class WalletBalances(
    val ethereum: String,
    val ethereumWei: BigInteger,
    val bsc: String,
    val bscWei: BigInteger,
    val bitcoin: String,
    val bitcoinSatoshi: Long
)

class WalletException(message: String) : Exception(message)
