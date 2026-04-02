package com.namiwallet.di;

import android.content.Context;
import com.namiwallet.security.SecureStorage;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideSecureStorageFactory implements Factory<SecureStorage> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideSecureStorageFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SecureStorage get() {
    return provideSecureStorage(contextProvider.get());
  }

  public static AppModule_ProvideSecureStorageFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideSecureStorageFactory(contextProvider);
  }

  public static SecureStorage provideSecureStorage(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSecureStorage(context));
  }
}
