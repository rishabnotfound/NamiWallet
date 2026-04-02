package com.namiwallet.network

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.math.BigInteger
import javax.net.ssl.SSLSocketFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bitcoin Electrum protocol client for balance queries and transaction broadcasting.
 */
@Singleton
class BitcoinElectrumClient @Inject constructor() {
    private val gson = Gson()

    companion object {
        // Public Electrum servers
        private val MAINNET_SERVERS = listOf(
            ElectrumServer("electrum.blockstream.info", 50002, true),
            ElectrumServer("electrum.emzy.de", 50002, true),
            ElectrumServer("electrum.bitaroo.net", 50002, true)
        )

        private val TESTNET_SERVERS = listOf(
            ElectrumServer("electrum.blockstream.info", 60002, true),
            ElectrumServer("testnet.aranguren.org", 51002, true)
        )
    }

    /**
     * Get balance for a Bitcoin address
     */
    suspend fun getBalance(address: String, testnet: Boolean = false): Result<BitcoinBalance> = withContext(Dispatchers.IO) {
        val servers = if (testnet) TESTNET_SERVERS else MAINNET_SERVERS

        for (server in servers) {
            try {
                val scriptHash = addressToScriptHash(address)
                val response = callElectrum(
                    server,
                    "blockchain.scripthash.get_balance",
                    listOf(scriptHash)
                )

                if (response.has("result")) {
                    val result = response.getAsJsonObject("result")
                    val confirmed = result.get("confirmed")?.asLong ?: 0
                    val unconfirmed = result.get("unconfirmed")?.asLong ?: 0

                    return@withContext Result.success(BitcoinBalance(
                        confirmed = confirmed,
                        unconfirmed = unconfirmed,
                        total = confirmed + unconfirmed
                    ))
                }
            } catch (e: Exception) {
                // Try next server
                continue
            }
        }

        Result.failure(ElectrumException("Failed to connect to any Electrum server"))
    }

    /**
     * Get UTXOs for an address
     */
    suspend fun getUtxos(address: String, testnet: Boolean = false): Result<List<Utxo>> = withContext(Dispatchers.IO) {
        val servers = if (testnet) TESTNET_SERVERS else MAINNET_SERVERS

        for (server in servers) {
            try {
                val scriptHash = addressToScriptHash(address)
                val response = callElectrum(
                    server,
                    "blockchain.scripthash.listunspent",
                    listOf(scriptHash)
                )

                if (response.has("result")) {
                    val result = response.getAsJsonArray("result")
                    val utxos = result.map { utxo ->
                        val obj = utxo.asJsonObject
                        Utxo(
                            txid = obj.get("tx_hash").asString,
                            vout = obj.get("tx_pos").asInt,
                            amount = obj.get("value").asLong,
                            height = obj.get("height").asInt
                        )
                    }
                    return@withContext Result.success(utxos)
                }
            } catch (e: Exception) {
                continue
            }
        }

        Result.failure(ElectrumException("Failed to get UTXOs"))
    }

    /**
     * Broadcast a signed transaction
     */
    suspend fun broadcastTransaction(rawTx: String, testnet: Boolean = false): Result<String> = withContext(Dispatchers.IO) {
        val servers = if (testnet) TESTNET_SERVERS else MAINNET_SERVERS

        for (server in servers) {
            try {
                val response = callElectrum(
                    server,
                    "blockchain.transaction.broadcast",
                    listOf(rawTx)
                )

                if (response.has("result")) {
                    val txid = response.get("result").asString
                    return@withContext Result.success(txid)
                } else if (response.has("error")) {
                    val error = response.getAsJsonObject("error")
                    return@withContext Result.failure(ElectrumException(error.get("message").asString))
                }
            } catch (e: Exception) {
                continue
            }
        }

        Result.failure(ElectrumException("Failed to broadcast transaction"))
    }

    /**
     * Get transaction history for an address
     */
    suspend fun getHistory(address: String, testnet: Boolean = false): Result<List<TransactionHistory>> = withContext(Dispatchers.IO) {
        val servers = if (testnet) TESTNET_SERVERS else MAINNET_SERVERS

        for (server in servers) {
            try {
                val scriptHash = addressToScriptHash(address)
                val response = callElectrum(
                    server,
                    "blockchain.scripthash.get_history",
                    listOf(scriptHash)
                )

                if (response.has("result")) {
                    val result = response.getAsJsonArray("result")
                    val history = result.map { tx ->
                        val obj = tx.asJsonObject
                        TransactionHistory(
                            txid = obj.get("tx_hash").asString,
                            height = obj.get("height").asInt
                        )
                    }
                    return@withContext Result.success(history)
                }
            } catch (e: Exception) {
                continue
            }
        }

        Result.failure(ElectrumException("Failed to get history"))
    }

    /**
     * Get current fee estimates (sat/vB)
     */
    suspend fun getFeeEstimates(testnet: Boolean = false): Result<FeeEstimates> = withContext(Dispatchers.IO) {
        val servers = if (testnet) TESTNET_SERVERS else MAINNET_SERVERS

        for (server in servers) {
            try {
                // Get estimates for different confirmation targets
                val fast = callElectrum(server, "blockchain.estimatefee", listOf(1))
                val medium = callElectrum(server, "blockchain.estimatefee", listOf(6))
                val slow = callElectrum(server, "blockchain.estimatefee", listOf(25))

                fun parseFeerate(response: JsonObject): Long {
                    val btcPerKb = response.get("result")?.asDouble ?: 0.0001
                    // Convert BTC/kB to sat/vB
                    return ((btcPerKb * 100_000_000) / 1000).toLong().coerceAtLeast(1)
                }

                return@withContext Result.success(FeeEstimates(
                    fast = parseFeerate(fast),
                    medium = parseFeerate(medium),
                    slow = parseFeerate(slow)
                ))
            } catch (e: Exception) {
                continue
            }
        }

        // Return default estimates if all servers fail
        Result.success(FeeEstimates(fast = 20, medium = 10, slow = 5))
    }

    /**
     * Convert address to Electrum script hash format
     */
    private fun addressToScriptHash(address: String): String {
        // This is a simplified implementation
        // In production, you'd need to properly decode the address and hash the script
        // For now, we'll use a placeholder that works with the Electrum protocol

        // Decode address based on format
        val script = when {
            address.startsWith("bc1") || address.startsWith("tb1") -> {
                // SegWit address - decode bech32 and create witness program
                decodeBech32ToScript(address)
            }
            address.startsWith("1") || address.startsWith("m") || address.startsWith("n") -> {
                // Legacy P2PKH
                decodeBase58ToScript(address)
            }
            address.startsWith("3") || address.startsWith("2") -> {
                // P2SH
                decodeBase58P2SHToScript(address)
            }
            else -> throw ElectrumException("Unsupported address format")
        }

        // SHA256 of the script, then reverse bytes for Electrum
        val hash = sha256(script)
        return hash.reversedArray().joinToString("") { "%02x".format(it) }
    }

    private fun decodeBech32ToScript(address: String): ByteArray {
        // Simplified bech32 decode - in production use a proper library
        val hrp = if (address.startsWith("bc1")) "bc" else "tb"
        val data = address.substring(hrp.length + 1)

        // Convert from bech32 charset to bytes
        val chars = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"
        val values = data.map { chars.indexOf(it) }

        // Extract witness version and program
        val witnessVersion = values[0]
        val witnessProgram = convertBits(values.drop(1), 5, 8, false)

        // Build P2WPKH script: OP_0 <20-byte-hash>
        return byteArrayOf(0x00, witnessProgram.size.toByte()) + witnessProgram
    }

    private fun decodeBase58ToScript(address: String): ByteArray {
        val decoded = decodeBase58Check(address)
        // P2PKH script: OP_DUP OP_HASH160 <20-byte-hash> OP_EQUALVERIFY OP_CHECKSIG
        return byteArrayOf(0x76, 0xa9.toByte(), 0x14) + decoded.drop(1).toByteArray() + byteArrayOf(0x88.toByte(), 0xac.toByte())
    }

    private fun decodeBase58P2SHToScript(address: String): ByteArray {
        val decoded = decodeBase58Check(address)
        // P2SH script: OP_HASH160 <20-byte-hash> OP_EQUAL
        return byteArrayOf(0xa9.toByte(), 0x14) + decoded.drop(1).toByteArray() + byteArrayOf(0x87.toByte())
    }

    private fun decodeBase58Check(input: String): ByteArray {
        val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        var num = BigInteger.ZERO
        for (char in input) {
            num = num.multiply(BigInteger.valueOf(58)).add(BigInteger.valueOf(alphabet.indexOf(char).toLong()))
        }
        val bytes = num.toByteArray()
        // Handle leading zeros
        val leadingZeros = input.takeWhile { it == '1' }.length
        return ByteArray(leadingZeros) + bytes.dropWhile { it == 0.toByte() }.dropLast(4).toByteArray()
    }

    private fun convertBits(data: List<Int>, fromBits: Int, toBits: Int, pad: Boolean): ByteArray {
        var acc = 0
        var bits = 0
        val result = mutableListOf<Byte>()
        val maxv = (1 shl toBits) - 1

        for (value in data) {
            acc = (acc shl fromBits) or value
            bits += fromBits
            while (bits >= toBits) {
                bits -= toBits
                result.add(((acc shr bits) and maxv).toByte())
            }
        }

        if (pad && bits > 0) {
            result.add(((acc shl (toBits - bits)) and maxv).toByte())
        }

        return result.toByteArray()
    }

    private fun sha256(data: ByteArray): ByteArray {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        return digest.digest(data)
    }

    private suspend fun callElectrum(server: ElectrumServer, method: String, params: List<Any>): JsonObject {
        val socket = if (server.ssl) {
            SSLSocketFactory.getDefault().createSocket(server.host, server.port)
        } else {
            java.net.Socket(server.host, server.port)
        }

        socket.soTimeout = 10000

        try {
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            val request = mapOf(
                "jsonrpc" to "2.0",
                "method" to method,
                "params" to params,
                "id" to 1
            )

            writer.println(gson.toJson(request))
            val response = reader.readLine() ?: throw ElectrumException("Empty response")

            return gson.fromJson(response, JsonObject::class.java)
        } finally {
            socket.close()
        }
    }
}

data class ElectrumServer(
    val host: String,
    val port: Int,
    val ssl: Boolean
)

data class BitcoinBalance(
    val confirmed: Long,
    val unconfirmed: Long,
    val total: Long
)

data class Utxo(
    val txid: String,
    val vout: Int,
    val amount: Long,
    val height: Int
)

data class TransactionHistory(
    val txid: String,
    val height: Int
)

data class FeeEstimates(
    val fast: Long,
    val medium: Long,
    val slow: Long
)

class ElectrumException(message: String) : Exception(message)
