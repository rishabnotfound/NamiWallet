package com.namiwallet.di;

import com.namiwallet.network.BitcoinElectrumClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideBitcoinElectrumClientFactory implements Factory<BitcoinElectrumClient> {
  @Override
  public BitcoinElectrumClient get() {
    return provideBitcoinElectrumClient();
  }

  public static AppModule_ProvideBitcoinElectrumClientFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BitcoinElectrumClient provideBitcoinElectrumClient() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBitcoinElectrumClient());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideBitcoinElectrumClientFactory INSTANCE = new AppModule_ProvideBitcoinElectrumClientFactory();
  }
}
