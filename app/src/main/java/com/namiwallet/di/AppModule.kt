package com.namiwallet.di

import android.content.Context
import com.namiwallet.bridge.NamiCore
import com.namiwallet.network.BitcoinElectrumClient
import com.namiwallet.network.EthereumRpcClient
import com.namiwallet.repository.WalletRepository
import com.namiwallet.security.BiometricHelper
import com.namiwallet.security.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNamiCore(): NamiCore {
        return NamiCore()
    }

    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context
    ): SecureStorage {
        return SecureStorage(context)
    }

    @Provides
    @Singleton
    fun provideBiometricHelper(
        @ApplicationContext context: Context
    ): BiometricHelper {
        return BiometricHelper(context)
    }

    @Provides
    @Singleton
    fun provideEthereumRpcClient(): EthereumRpcClient {
        return EthereumRpcClient()
    }

    @Provides
    @Singleton
    fun provideBitcoinElectrumClient(): BitcoinElectrumClient {
        return BitcoinElectrumClient()
    }

    @Provides
    @Singleton
    fun provideWalletRepository(
        namiCore: NamiCore,
        secureStorage: SecureStorage,
        ethereumRpcClient: EthereumRpcClient,
        bitcoinElectrumClient: BitcoinElectrumClient
    ): WalletRepository {
        return WalletRepository(
            namiCore = namiCore,
            secureStorage = secureStorage,
            ethereumRpcClient = ethereumRpcClient,
            bitcoinElectrumClient = bitcoinElectrumClient
        )
    }
}
