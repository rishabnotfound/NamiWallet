package com.namiwallet.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ethereum/BSC JSON-RPC client for balance queries, gas estimation, and transaction broadcasting.
 */
@Singleton
class EthereumRpcClient @Inject constructor() {
    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Get ETH/BNB balance for an address
     */
    suspend fun getBalance(address: String, chain: EvmChain): Result<BigInteger> = withContext(Dispatchers.IO) {
        try {
            val response = callRpc(
                chain.rpcUrl,
                "eth_getBalance",
                listOf(address, "latest")
            )
            val hexBalance = response.getAsJsonPrimitive("result").asString
            val balance = hexBalance.removePrefix("0x").toBigInteger(16)
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(RpcException("Failed to get balance: ${e.message}"))
        }
    }

    /**
     * Get ERC-20 token balance
     */
    suspend fun getTokenBalance(
        address: String,
        tokenContract: String,
        chain: EvmChain
    ): Result<BigInteger> = withContext(Dispatchers.IO) {
        try {
            // balanceOf(address) function selector + padded address
            val data = "0x70a08231" + address.removePrefix("0x").padStart(64, '0')
            val response = callRpc(
                chain.rpcUrl,
                "eth_call",
                listOf(
                    mapOf("to" to tokenContract, "data" to data),
                    "latest"
                )
            )
            val hexBalance = response.getAsJsonPrimitive("result").asString
            val balance = if (hexBalance == "0x") {
                BigInteger.ZERO
            } else {
                hexBalance.removePrefix("0x").toBigInteger(16)
            }
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(RpcException("Failed to get token balance: ${e.message}"))
        }
    }

    /**
     * Get the current nonce (transaction count) for an address
     */
    suspend fun getNonce(address: String, chain: EvmChain): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val response = callRpc(
                chain.rpcUrl,
                "eth_getTransactionCount",
                listOf(address, "pending")
            )
            val hexNonce = response.getAsJsonPrimitive("result").asString
            val nonce = hexNonce.removePrefix("0x").toLong(16)
            Result.success(nonce)
        } catch (e: Exception) {
            Result.failure(RpcException("Failed to get nonce: ${e.message}"))
        }
    }

    /**
     * Estimate gas for a transaction
     */
    suspend fun estimateGas(
        from: String,
        to: String,
        value: BigInteger,
        data: String?,
        chain: EvmChain
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val params = mutableMapOf(
                "from" to from,
                "to" to to,
                "value" to "0x${value.toString(16)}"
            )
            if (!data.isNullOrEmpty()) {
                params["data"] = data
            }

            val response = callRpc(
                chain.rpcUrl,
                "eth_estimateGas",
                listOf(params)
            )
            val hexGas = response.getAsJsonPrimitive("result").asString
            val gas = hexGas.removePrefix("0x").toLong(16)
            // Add 20% buffer for safety
            Result.success((gas * 1.2).toLong())
        } catch (e: Exception) {
            Result.failure(RpcException("Failed to estimate gas: ${e.message}"))
        }
    }

    /**
     * Get current gas prices (for EIP-1559)
     */
    suspend fun getGasPrices(chain: EvmChain): Result<GasPrices> = withContext(Dispatchers.IO) {
        try {
            // Get base fee from latest block
            val blockResponse = callRpc(
                chain.rpcUrl,
                "eth_getBlockByNumber",
                listOf("latest", false)
            )
            val block = blockResponse.getAsJsonObject("result")
            val baseFeeHex = block.get("baseFeePerGas")?.asString ?: "0x0"
            val baseFee = baseFeeHex.removePrefix("0x").toBigIntegerOrNull(16) ?: BigInteger.ZERO

            // Get max priority fee suggestion
            val maxPriorityResponse = callRpc(
                chain.rpcUrl,
                "eth_maxPriorityFeePerGas",
                emptyList<String>()
            )
            val maxPriorityHex = maxPriorityResponse.getAsJsonPrimitive("result").asString
            val maxPriorityFee = maxPriorityHex.removePrefix("0x").toBigInteger(16)

            // Calculate max fee (base fee * 2 + priority fee)
            val maxFee = baseFee.multiply(BigInteger.TWO) + maxPriorityFee

            Result.success(GasPrices(
                baseFee = baseFee,
                maxPriorityFeePerGas = maxPriorityFee,
                maxFeePerGas = maxFee
            ))
        } catch (e: Exception) {
            // Fallback to legacy gas price
            try {
                val gasPriceResponse = callRpc(
                    chain.rpcUrl,
                    "eth_gasPrice",
                    emptyList<String>()
                )
                val gasPriceHex = gasPriceResponse.getAsJsonPrimitive("result").asString
                val gasPrice = gasPriceHex.removePrefix("0x").toBigInteger(16)

                Result.success(GasPrices(
                    baseFee = gasPrice,
                    maxPriorityFeePerGas = gasPrice / BigInteger.TEN,
                    maxFeePerGas = gasPrice
                ))
            } catch (e2: Exception) {
                Result.failure(RpcException("Failed to get gas prices: ${e.message}"))
            }
        }
    }

    /**
     * Broadcast a signed transaction
     */
    suspend fun sendRawTransaction(signedTx: String, chain: EvmChain): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = callRpc(
                chain.rpcUrl,
                "eth_sendRawTransaction",
                listOf(signedTx)
            )

            if (response.has("error")) {
                val error = response.getAsJsonObject("error")
                return@withContext Result.failure(RpcException(error.get("message").asString))
            }

            val txHash = response.getAsJsonPrimitive("result").asString
            Result.success(txHash)
        } catch (e: Exception) {
            Result.failure(RpcException("Failed to send transaction: ${e.message}"))
        }
    }

    /**
     * Get transaction receipt
     */
    suspend fun getTransactionReceipt(txHash: String, chain: EvmChain): Result<TransactionReceipt?> = withContext(Dispatchers.IO) {
        try {
            val response = callRpc(
                chain.rpcUrl,
                "eth_getTransactionReceipt",
                listOf(txHash)
            )

            val result = response.get("result")
            if (result == null || result.isJsonNull) {
                return@withContext Result.success(null)
            }

            val receipt = result.asJsonObject
            val status = receipt.get("status")?.asString?.let {
                it.removePrefix("0x").toIntOrNull(16) == 1
            } ?: false

            Result.success(TransactionReceipt(
                transactionHash = receipt.get("transactionHash").asString,
                blockNumber = receipt.get("blockNumber")?.asString?.removePrefix("0x")?.toLongOrNull(16) ?: 0,
                status = status,
                gasUsed = receipt.get("gasUsed")?.asString?.removePrefix("0x")?.toLongOrNull(16) ?: 0
            ))
        } catch (e: Exception) {
            Result.failure(RpcException("Failed to get receipt: ${e.message}"))
        }
    }

    private suspend fun callRpc(url: String, method: String, params: List<Any>): JsonObject {
        val requestBody = mapOf(
            "jsonrpc" to "2.0",
            "method" to method,
            "params" to params,
            "id" to 1
        )

        val json = gson.toJson(requestBody)
        val request = Request.Builder()
            .url(url)
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw RpcException("Empty response")

        return gson.fromJson(responseBody, JsonObject::class.java)
    }
}

/**
 * Supported EVM chains
 */
enum class EvmChain(val rpcUrl: String, val chainId: Long, val symbol: String, val displayName: String) {
    ETHEREUM_MAINNET(
        rpcUrl = "https://eth.llamarpc.com",
        chainId = 1,
        symbol = "ETH",
        displayName = "Ethereum"
    ),
    ETHEREUM_SEPOLIA(
        rpcUrl = "https://rpc.sepolia.org",
        chainId = 11155111,
        symbol = "ETH",
        displayName = "Sepolia Testnet"
    ),
    BSC_MAINNET(
        rpcUrl = "https://bsc-dataseed.binance.org",
        chainId = 56,
        symbol = "BNB",
        displayName = "BNB Smart Chain"
    ),
    BSC_TESTNET(
        rpcUrl = "https://data-seed-prebsc-1-s1.binance.org:8545",
        chainId = 97,
        symbol = "BNB",
        displayName = "BSC Testnet"
    )
}

data class GasPrices(
    val baseFee: BigInteger,
    val maxPriorityFeePerGas: BigInteger,
    val maxFeePerGas: BigInteger
)

data class TransactionReceipt(
    val transactionHash: String,
    val blockNumber: Long,
    val status: Boolean,
    val gasUsed: Long
)

class RpcException(message: String) : Exception(message)
