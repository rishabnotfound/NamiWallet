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
public final class CreateWalletViewModel_Factory implements Factory<CreateWalletViewModel> {
  private final Provider<WalletRepository> walletRepositoryProvider;

  public CreateWalletViewModel_Factory(Provider<WalletRepository> walletRepositoryProvider) {
    this.walletRepositoryProvider = walletRepositoryProvider;
  }

  @Override
  public CreateWalletViewModel get() {
    return newInstance(walletRepositoryProvider.get());
  }

  public static CreateWalletViewModel_Factory create(
      Provider<WalletRepository> walletRepositoryProvider) {
    return new CreateWalletViewModel_Factory(walletRepositoryProvider);
  }

  public static CreateWalletViewModel newInstance(WalletRepository walletRepository) {
    return new CreateWalletViewModel(walletRepository);
  }
}
