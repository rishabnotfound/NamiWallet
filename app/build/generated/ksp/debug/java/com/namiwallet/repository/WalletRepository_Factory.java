package com.namiwallet.repository;

import com.namiwallet.bridge.NamiCore;
import com.namiwallet.network.BitcoinElectrumClient;
import com.namiwallet.network.EthereumRpcClient;
import com.namiwallet.security.SecureStorage;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class WalletRepository_Factory implements Factory<WalletRepository> {
  private final Provider<NamiCore> namiCoreProvider;

  private final Provider<SecureStorage> secureStorageProvider;

  private final Provider<EthereumRpcClient> ethereumRpcClientProvider;

  private final Provider<BitcoinElectrumClient> bitcoinElectrumClientProvider;

  public WalletRepository_Factory(Provider<NamiCore> namiCoreProvider,
      Provider<SecureStorage> secureStorageProvider,
      Provider<EthereumRpcClient> ethereumRpcClientProvider,
      Provider<BitcoinElectrumClient> bitcoinElectrumClientProvider) {
    this.namiCoreProvider = namiCoreProvider;
    this.secureStorageProvider = secureStorageProvider;
    this.ethereumRpcClientProvider = ethereumRpcClientProvider;
    this.bitcoinElectrumClientProvider = bitcoinElectrumClientProvider;
  }

  @Override
  public WalletRepository get() {
    return newInstance(namiCoreProvider.get(), secureStorageProvider.get(), ethereumRpcClientProvider.get(), bitcoinElectrumClientProvider.get());
  }

  public static WalletRepository_Factory create(Provider<NamiCore> namiCoreProvider,
      Provider<SecureStorage> secureStorageProvider,
      Provider<EthereumRpcClient> ethereumRpcClientProvider,
      Provider<BitcoinElectrumClient> bitcoinElectrumClientProvider) {
    return new WalletRepository_Factory(namiCoreProvider, secureStorageProvider, ethereumRpcClientProvider, bitcoinElectrumClientProvider);
  }

  public static WalletRepository newInstance(NamiCore namiCore, SecureStorage secureStorage,
      EthereumRpcClient ethereumRpcClient, BitcoinElectrumClient bitcoinElectrumClient) {
    return new WalletRepository(namiCore, secureStorage, ethereumRpcClient, bitcoinElectrumClient);
  }
}
