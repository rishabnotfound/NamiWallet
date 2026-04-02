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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<WalletRepository> walletRepositoryProvider;

  public HomeViewModel_Factory(Provider<WalletRepository> walletRepositoryProvider) {
    this.walletRepositoryProvider = walletRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(walletRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<WalletRepository> walletRepositoryProvider) {
    return new HomeViewModel_Factory(walletRepositoryProvider);
  }

  public static HomeViewModel newInstance(WalletRepository walletRepository) {
    return new HomeViewModel(walletRepository);
  }
}
