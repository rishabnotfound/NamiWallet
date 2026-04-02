package com.namiwallet.di;

import android.content.Context;
import com.namiwallet.security.BiometricHelper;
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
public final class AppModule_ProvideBiometricHelperFactory implements Factory<BiometricHelper> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideBiometricHelperFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BiometricHelper get() {
    return provideBiometricHelper(contextProvider.get());
  }

  public static AppModule_ProvideBiometricHelperFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideBiometricHelperFactory(contextProvider);
  }

  public static BiometricHelper provideBiometricHelper(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBiometricHelper(context));
  }
}
