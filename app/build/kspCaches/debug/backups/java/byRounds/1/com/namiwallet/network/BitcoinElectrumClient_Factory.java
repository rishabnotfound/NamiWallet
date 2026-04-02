package com.namiwallet.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class BitcoinElectrumClient_Factory implements Factory<BitcoinElectrumClient> {
  @Override
  public BitcoinElectrumClient get() {
    return newInstance();
  }

  public static BitcoinElectrumClient_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BitcoinElectrumClient newInstance() {
    return new BitcoinElectrumClient();
  }

  private static final class InstanceHolder {
    private static final BitcoinElectrumClient_Factory INSTANCE = new BitcoinElectrumClient_Factory();
  }
}
