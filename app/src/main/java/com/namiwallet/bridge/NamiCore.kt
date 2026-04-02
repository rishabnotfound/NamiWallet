package com.namiwallet.bridge

import org.bitcoinj.core.*
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.DeterministicHierarchy
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.crypto.TransactionSignature
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.ScriptBuilder
import org.web3j.crypto.*
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pure Kotlin implementation of crypto operations using web3j and bitcoinj.
 * No native/JNI code required.
 */
@Singleton
class NamiCore @Inject constructor() {

    private val secureRandom = SecureRandom()

    // ========================================================================
    // BIP-39 Mnemonic Functions
    // ========================================================================

    /**
     * Generate a new 12-word mnemonic phrase
     */
    fun generateMnemonic12(): Result<String> = runCatching {
        val entropy = ByteArray(16) // 128 bits for 12 words
        secureRandom.nextBytes(entropy)
        MnemonicCode.INSTANCE.toMnemonic(entropy).joinToString(" ")
    }

    /**
     * Generate a new 24-word mnemonic phrase
     */
    fun generateMnemonic24(): Result<String> = runCatching {
        val entropy = ByteArray(32) // 256 bits for 24 words
        secureRandom.nextBytes(entropy)
        MnemonicCode.INSTANCE.toMnemonic(entropy).joinToString(" ")
    }

    /**
     * Validate a mnemonic phrase
     */
    fun validateMnemonic(mnemonic: String): Boolean {
        return try {
            val words = mnemonic.trim().split("\\s+".toRegex())
            MnemonicCode.INSTANCE.check(words)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get word suggestions for autocomplete
     */
    fun getWordSuggestions(prefix: String): List<String> {
        if (prefix.isEmpty()) return emptyList()
        return MnemonicCode.INSTANCE.wordList
            .filter { it.startsWith(prefix.lowercase()) }
            .take(5)
    }

    /**
     * Check if a word is valid in the BIP-39 wordlist
     */
    fun isValidWord(word: String): Boolean {
        return MnemonicCode.INSTANCE.wordList.contains(word.lowercase())
    }

    // ========================================================================
    // Key Derivation
    // ========================================================================

    private fun mnemonicToSeed(mnemonic: String, passphrase: String = ""): ByteArray {
        val words = mnemonic.trim().split("\\s+".toRegex())
        return MnemonicCode.toSeed(words, passphrase)
    }

    private fun deriveEthereumKey(mnemonic: String, passphrase: String, account: Int, index: Int): ECKeyPair {
        // BIP-44 path: m/44'/60'/account'/0/index
        val seed = mnemonicToSeed(mnemonic, passphrase)
        val masterKey = HDKeyDerivation.createMasterPrivateKey(seed)

        val hierarchy = DeterministicHierarchy(masterKey)
        val path = listOf(
            ChildNumber(44, true),
            ChildNumber(60, true),
            ChildNumber(account, true),
            ChildNumber(0, false),
            ChildNumber(index, false)
        )

        val derivedKey = hierarchy.get(path, true, true)
        return ECKeyPair.create(derivedKey.privKey)
    }

    private fun deriveBitcoinKey(
        mnemonic: String,
        passphrase: String,
        testnet: Boolean,
        account: Int,
        index: Int
    ): org.bitcoinj.crypto.DeterministicKey {
        // BIP-84 path for native segwit: m/84'/0'/account'/0/index (mainnet)
        // BIP-84 path for native segwit: m/84'/1'/account'/0/index (testnet)
        val seed = mnemonicToSeed(mnemonic, passphrase)
        val masterKey = HDKeyDerivation.createMasterPrivateKey(seed)

        val coinType = if (testnet) 1 else 0
        val hierarchy = DeterministicHierarchy(masterKey)
        val path = listOf(
            ChildNumber(84, true),
            ChildNumber(coinType, true),
            ChildNumber(account, true),
            ChildNumber(0, false),
            ChildNumber(index, false)
        )

        return hierarchy.get(path, true, true)
    }

    // ========================================================================
    // Ethereum/BSC Functions
    // ========================================================================

    /**
     * Get an Ethereum/BSC address (checksummed)
     */
    fun getEthereumAddress(
        mnemonic: String,
        passphrase: String = "",
        account: Int = 0,
        index: Int = 0
    ): Result<String> = runCatching {
        val keyPair = deriveEthereumKey(mnemonic, passphrase, account, index)
        val address = Keys.getAddress(keyPair)
        Keys.toChecksumAddress("0x$address")
    }

    /**
     * Validate an Ethereum address
     */
    fun validateEthereumAddress(address: String): Boolean {
        return try {
            WalletUtils.isValidAddress(address)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Sign an EIP-1559 transaction
     */
    fun signEthereumTransaction(
        mnemonic: String,
        passphrase: String = "",
        account: Int = 0,
        index: Int = 0,
        tx: Eip1559Transaction
    ): Result<SignedEthereumTransaction> = runCatching {
        val keyPair = deriveEthereumKey(mnemonic, passphrase, account, index)
        val credentials = Credentials.create(keyPair)

        val rawTransaction = RawTransaction.createTransaction(
            tx.chainId,
            BigInteger.valueOf(tx.nonce),
            BigInteger.valueOf(tx.gasLimit),
            tx.to ?: "",
            BigInteger(tx.value),
            tx.data ?: "",
            BigInteger(tx.maxPriorityFeePerGas),
            BigInteger(tx.maxFeePerGas)
        )

        val signedMessage = TransactionEncoder.signMessage(rawTransaction, tx.chainId, credentials)
        val hexValue = Numeric.toHexString(signedMessage)
        val hash = Hash.sha3(hexValue)

        SignedEthereumTransaction(hash = hash, rawTx = hexValue)
    }

    /**
     * Sign a legacy transaction (for BSC or older networks)
     */
    fun signLegacyTransaction(
        mnemonic: String,
        passphrase: String = "",
        account: Int = 0,
        index: Int = 0,
        tx: LegacyTransaction
    ): Result<SignedEthereumTransaction> = runCatching {
        val keyPair = deriveEthereumKey(mnemonic, passphrase, account, index)
        val credentials = Credentials.create(keyPair)

        val rawTransaction = RawTransaction.createTransaction(
            BigInteger.valueOf(tx.nonce),
            BigInteger(tx.gasPrice),
            BigInteger.valueOf(tx.gasLimit),
            tx.to ?: "",
            BigInteger(tx.value),
            tx.data ?: ""
        )

        val signedMessage = TransactionEncoder.signMessage(rawTransaction, tx.chainId, credentials)
        val hexValue = Numeric.toHexString(signedMessage)
        val hash = Hash.sha3(hexValue)

        SignedEthereumTransaction(hash = hash, rawTx = hexValue)
    }

    /**
     * Encode ERC-20 transfer data
     */
    fun encodeErc20Transfer(to: String, amount: BigInteger): String {
        // transfer(address,uint256) selector: 0xa9059cbb
        val methodId = "a9059cbb"
        val addressPadded = Numeric.toHexStringNoPrefixZeroPadded(Numeric.toBigInt(to), 64)
        val amountPadded = Numeric.toHexStringNoPrefixZeroPadded(amount, 64)
        return "0x$methodId$addressPadded$amountPadded"
    }

    /**
     * Parse ether amount to wei
     */
    fun parseEther(amount: String): Result<String> = runCatching {
        val parts = amount.split(".")
        when (parts.size) {
            1 -> {
                BigInteger(parts[0]).multiply(BigInteger.TEN.pow(18)).toString()
            }
            2 -> {
                val whole = BigInteger(parts[0]).multiply(BigInteger.TEN.pow(18))
                val decimals = parts[1].take(18).padEnd(18, '0')
                val frac = BigInteger(decimals)
                whole.add(frac).toString()
            }
            else -> throw IllegalArgumentException("Invalid amount format")
        }
    }

    /**
     * Format wei to ether string
     */
    fun formatEther(wei: String): String {
        val value = BigInteger(wei)
        val divisor = BigInteger.TEN.pow(18)
        val whole = value.divide(divisor)
        val remainder = value.mod(divisor)

        return if (remainder == BigInteger.ZERO) {
            whole.toString()
        } else {
            val fracStr = remainder.toString().padStart(18, '0').trimEnd('0')
            "$whole.$fracStr"
        }
    }

    /**
     * Sign a message with personal_sign (EIP-191)
     */
    fun personalSign(
        mnemonic: String,
        passphrase: String = "",
        account: Int = 0,
        index: Int = 0,
        message: String
    ): Result<String> = runCatching {
        val keyPair = deriveEthereumKey(mnemonic, passphrase, account, index)
        val signatureData = Sign.signPrefixedMessage(message.toByteArray(), keyPair)

        val r = Numeric.toHexStringNoPrefix(signatureData.r)
        val s = Numeric.toHexStringNoPrefix(signatureData.s)
        val v = Numeric.toHexStringNoPrefix(signatureData.v)

        "0x$r$s$v"
    }

    // ========================================================================
    // Bitcoin Functions
    // ========================================================================

    /**
     * Get a Bitcoin SegWit (bech32) address
     */
    fun getBitcoinAddress(
        mnemonic: String,
        passphrase: String = "",
        testnet: Boolean = false,
        account: Int = 0,
        index: Int = 0
    ): Result<String> = runCatching {
        val params = if (testnet) TestNet3Params.get() else MainNetParams.get()
        val key = deriveBitcoinKey(mnemonic, passphrase, testnet, account, index)

        // Native SegWit (P2WPKH) address
        val pubKeyHash = key.pubKeyHash
        SegwitAddress.fromHash(params, pubKeyHash).toBech32()
    }

    /**
     * Get a Bitcoin legacy (P2PKH) address
     */
    fun getBitcoinLegacyAddress(
        mnemonic: String,
        passphrase: String = "",
        testnet: Boolean = false,
        account: Int = 0,
        index: Int = 0
    ): Result<String> = runCatching {
        val params = if (testnet) TestNet3Params.get() else MainNetParams.get()
        val key = deriveBitcoinKey(mnemonic, passphrase, testnet, account, index)
        LegacyAddress.fromKey(params, key).toBase58()
    }

    /**
     * Validate a Bitcoin address
     */
    fun validateBitcoinAddress(address: String, testnet: Boolean = false): Boolean {
        return try {
            val params = if (testnet) TestNet3Params.get() else MainNetParams.get()
            Address.fromString(params, address)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Sign a Bitcoin transaction
     */
    fun signBitcoinTransaction(
        mnemonic: String,
        passphrase: String = "",
        testnet: Boolean = false,
        account: Int = 0,
        index: Int = 0,
        inputs: List<BitcoinInput>,
        outputs: List<BitcoinOutput>
    ): Result<SignedBitcoinTransaction> = runCatching {
        val params = if (testnet) TestNet3Params.get() else MainNetParams.get()
        val key = deriveBitcoinKey(mnemonic, passphrase, testnet, account, index)

        val transaction = Transaction(params)

        // Add outputs
        outputs.forEach { output ->
            val address = Address.fromString(params, output.address)
            transaction.addOutput(Coin.valueOf(output.amountSatoshi), address)
        }

        // Add inputs
        inputs.forEach { input ->
            val outPoint = TransactionOutPoint(
                params,
                input.vout.toLong(),
                Sha256Hash.wrap(input.txid)
            )
            val txInput = TransactionInput(
                params,
                transaction,
                ByteArray(0),
                outPoint,
                Coin.valueOf(input.amountSatoshi)
            )
            transaction.addInput(txInput)
        }

        // Sign each input (P2WPKH)
        inputs.forEachIndexed { idx, input ->
            val scriptCode = ScriptBuilder.createP2PKHOutputScript(key)
            val hash = transaction.hashForWitnessSignature(
                idx,
                scriptCode,
                Coin.valueOf(input.amountSatoshi),
                Transaction.SigHash.ALL,
                false
            )
            val signature = key.sign(hash)
            val txSig = TransactionSignature(signature, Transaction.SigHash.ALL, false)

            val witness = TransactionWitness(2)
            witness.setPush(0, txSig.encodeToBitcoin())
            witness.setPush(1, key.pubKey)
            transaction.getInput(idx.toLong()).witness = witness
        }

        SignedBitcoinTransaction(
            txid = transaction.txId.toString(),
            rawTx = Numeric.toHexString(transaction.bitcoinSerialize())
        )
    }
}

// ============================================================================
// Data Classes
// ============================================================================

data class BitcoinInput(
    val txid: String,
    val vout: Int,
    val amountSatoshi: Long
)

data class BitcoinOutput(
    val address: String,
    val amountSatoshi: Long
)

data class SignedBitcoinTransaction(
    val txid: String,
    val rawTx: String
)

data class Eip1559Transaction(
    val chainId: Long,
    val nonce: Long,
    val maxPriorityFeePerGas: String,
    val maxFeePerGas: String,
    val gasLimit: Long,
    val to: String?,
    val value: String,
    val data: String? = null
)

data class LegacyTransaction(
    val chainId: Long,
    val nonce: Long,
    val gasPrice: String,
    val gasLimit: Long,
    val to: String?,
    val value: String,
    val data: String? = null
)

data class SignedEthereumTransaction(
    val hash: String,
    val rawTx: String
)

class NamiCoreException(message: String) : Exception(message)

// Chain IDs
object ChainIds {
    const val ETHEREUM_MAINNET = 1L
    const val ETHEREUM_GOERLI = 5L
    const val ETHEREUM_SEPOLIA = 11155111L
    const val BSC_MAINNET = 56L
    const val BSC_TESTNET = 97L
}
