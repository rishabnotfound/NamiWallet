package com.namiwallet.viewmodel;

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
public final class ReceiveViewModel_Factory implements Factory<ReceiveViewModel> {
  private final Provider<WalletRepository> walletRepositoryProvider;

  public ReceiveViewModel_Factory(Provider<WalletRepository> walletRepositoryProvider) {
    this.walletRepositoryProvider = walletRepositoryProvider;
  }

  @Override
  public ReceiveViewModel get() {
    return newInstance(walletRepositoryProvider.get());
  }

  public static ReceiveViewModel_Factory create(
      Provider<WalletRepository> walletRepositoryProvider) {
    return new ReceiveViewModel_Factory(walletRepositoryProvider);
  }

  public static ReceiveViewModel newInstance(WalletRepository walletRepository) {
    return new ReceiveViewModel(walletRepository);
  }
}
