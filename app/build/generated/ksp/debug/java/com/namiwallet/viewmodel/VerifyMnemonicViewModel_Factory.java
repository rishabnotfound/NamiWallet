package com.namiwallet.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class VerifyMnemonicViewModel_Factory implements Factory<VerifyMnemonicViewModel> {
  @Override
  public VerifyMnemonicViewModel get() {
    return newInstance();
  }

  public static VerifyMnemonicViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VerifyMnemonicViewModel newInstance() {
    return new VerifyMnemonicViewModel();
  }

  private static final class InstanceHolder {
    private static final VerifyMnemonicViewModel_Factory INSTANCE = new VerifyMnemonicViewModel_Factory();
  }
}
