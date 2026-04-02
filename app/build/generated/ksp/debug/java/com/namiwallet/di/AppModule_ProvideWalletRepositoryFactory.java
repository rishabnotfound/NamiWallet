package com.namiwallet.di;

import com.namiwallet.bridge.NamiCore;
import com.namiwallet.network.BitcoinElectrumClient;
import com.namiwallet.network.EthereumRpcClient;
import com.namiwallet.repository.WalletRepository;
import com.namiwallet.security.SecureStorage;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideWalletRepositoryFactory implements Factory<WalletRepository> {
  private final Provider<NamiCore> namiCoreProvider;

  private final Provider<SecureStorage> secureStorageProvider;

  private final Provider<EthereumRpcClient> ethereumRpcClientProvider;

  private final Provider<BitcoinElectrumClient> bitcoinElectrumClientProvider;

  public AppModule_ProvideWalletRepositoryFactory(Provider<NamiCore> namiCoreProvider,
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
    return provideWalletRepository(namiCoreProvider.get(), secureStorageProvider.get(), ethereumRpcClientProvider.get(), bitcoinElectrumClientProvider.get());
  }

  public static AppModule_ProvideWalletRepositoryFactory create(Provider<NamiCore> namiCoreProvider,
      Provider<SecureStorage> secureStorageProvider,
      Provider<EthereumRpcClient> ethereumRpcClientProvider,
      Provider<BitcoinElectrumClient> bitcoinElectrumClientProvider) {
    return new AppModule_ProvideWalletRepositoryFactory(namiCoreProvider, secureStorageProvider, ethereumRpcClientProvider, bitcoinElectrumClientProvider);
  }

  public static WalletRepository provideWalletRepository(NamiCore namiCore,
      SecureStorage secureStorage, EthereumRpcClient ethereumRpcClient,
      BitcoinElectrumClient bitcoinElectrumClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideWalletRepository(namiCore, secureStorage, ethereumRpcClient, bitcoinElectrumClient));
  }
}
