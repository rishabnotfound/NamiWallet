package com.namiwallet.di;

import com.namiwallet.bridge.NamiCore;
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
public final class AppModule_ProvideNamiCoreFactory implements Factory<NamiCore> {
  @Override
  public NamiCore get() {
    return provideNamiCore();
  }

  public static AppModule_ProvideNamiCoreFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NamiCore provideNamiCore() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNamiCore());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideNamiCoreFactory INSTANCE = new AppModule_ProvideNamiCoreFactory();
  }
}
