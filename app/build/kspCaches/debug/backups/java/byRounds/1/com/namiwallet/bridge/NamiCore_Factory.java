package com.namiwallet.bridge;

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
public final class NamiCore_Factory implements Factory<NamiCore> {
  @Override
  public NamiCore get() {
    return newInstance();
  }

  public static NamiCore_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NamiCore newInstance() {
    return new NamiCore();
  }

  private static final class InstanceHolder {
    private static final NamiCore_Factory INSTANCE = new NamiCore_Factory();
  }
}
