package com.namiwallet.viewmodel;

import com.namiwallet.repository.WalletRepository;
import com.namiwallet.security.BiometricHelper;
import com.namiwallet.security.SecureStorage;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<WalletRepository> walletRepositoryProvider;

  private final Provider<SecureStorage> secureStorageProvider;

  private final Provider<BiometricHelper> biometricHelperProvider;

  public SettingsViewModel_Factory(Provider<WalletRepository> walletRepositoryProvider,
      Provider<SecureStorage> secureStorageProvider,
      Provider<BiometricHelper> biometricHelperProvider) {
    this.walletRepositoryProvider = walletRepositoryProvider;
    this.secureStorageProvider = secureStorageProvider;
    this.biometricHelperProvider = biometricHelperProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(walletRepositoryProvider.get(), secureStorageProvider.get(), biometricHelperProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<WalletRepository> walletRepositoryProvider,
      Provider<SecureStorage> secureStorageProvider,
      Provider<BiometricHelper> biometricHelperProvider) {
    return new SettingsViewModel_Factory(walletRepositoryProvider, secureStorageProvider, biometricHelperProvider);
  }

  public static SettingsViewModel newInstance(WalletRepository walletRepository,
      SecureStorage secureStorage, BiometricHelper biometricHelper) {
    return new SettingsViewModel(walletRepository, secureStorage, biometricHelper);
  }
}
