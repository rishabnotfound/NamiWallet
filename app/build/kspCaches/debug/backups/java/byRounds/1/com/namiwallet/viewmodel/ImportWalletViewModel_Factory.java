package com.namiwallet.viewmodel;

import com.namiwallet.bridge.NamiCore;
import com.namiwallet.repository.WalletRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ImportWalletViewModel_Factory implements Factory<ImportWalletViewModel> {
  private final Provider<WalletRepository> walletRepositoryProvider;

  private final Provider<NamiCore> namiCoreProvider;

  public ImportWalletViewModel_Factory(Provider<WalletRepository> walletRepositoryProvider,
      Provider<NamiCore> namiCoreProvider) {
    this.walletRepositoryProvider = walletRepositoryProvider;
    this.namiCoreProvider = namiCoreProvider;
  }

  @Override
  public ImportWalletViewModel get() {
    return newInstance(walletRepositoryProvider.get(), namiCoreProvider.get());
  }

  public static ImportWalletViewModel_Factory create(
      Provider<WalletRepository> walletRepositoryProvider, Provider<NamiCore> namiCoreProvider) {
    return new ImportWalletViewModel_Factory(walletRepositoryProvider, namiCoreProvider);
  }

  public static ImportWalletViewModel newInstance(WalletRepository walletRepository,
      NamiCore namiCore) {
    return new ImportWalletViewModel(walletRepository, namiCore);
  }
}
