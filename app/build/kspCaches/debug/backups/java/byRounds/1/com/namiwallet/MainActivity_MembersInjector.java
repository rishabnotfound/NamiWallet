package com.namiwallet;

import com.namiwallet.security.SecureStorage;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SecureStorage> secureStorageProvider;

  public MainActivity_MembersInjector(Provider<SecureStorage> secureStorageProvider) {
    this.secureStorageProvider = secureStorageProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SecureStorage> secureStorageProvider) {
    return new MainActivity_MembersInjector(secureStorageProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSecureStorage(instance, secureStorageProvider.get());
  }

  @InjectedFieldSignature("com.namiwallet.MainActivity.secureStorage")
  public static void injectSecureStorage(MainActivity instance, SecureStorage secureStorage) {
    instance.secureStorage = secureStorage;
  }
}
