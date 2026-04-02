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
public final class SendViewModel_Factory implements Factory<SendViewModel> {
  private final Provider<WalletRepository> walletRepositoryProvider;

  public SendViewModel_Factory(Provider<WalletRepository> walletRepositoryProvider) {
    this.walletRepositoryProvider = walletRepositoryProvider;
  }

  @Override
  public SendViewModel get() {
    return newInstance(walletRepositoryProvider.get());
  }

  public static SendViewModel_Factory create(Provider<WalletRepository> walletRepositoryProvider) {
    return new SendViewModel_Factory(walletRepositoryProvider);
  }

  public static SendViewModel newInstance(WalletRepository walletRepository) {
    return new SendViewModel(walletRepository);
  }
}
