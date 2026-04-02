package com.namiwallet;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.namiwallet.bridge.NamiCore;
import com.namiwallet.di.AppModule_ProvideBiometricHelperFactory;
import com.namiwallet.di.AppModule_ProvideBitcoinElectrumClientFactory;
import com.namiwallet.di.AppModule_ProvideEthereumRpcClientFactory;
import com.namiwallet.di.AppModule_ProvideNamiCoreFactory;
import com.namiwallet.di.AppModule_ProvideSecureStorageFactory;
import com.namiwallet.di.AppModule_ProvideWalletRepositoryFactory;
import com.namiwallet.network.BitcoinElectrumClient;
import com.namiwallet.network.EthereumRpcClient;
import com.namiwallet.repository.WalletRepository;
import com.namiwallet.security.BiometricHelper;
import com.namiwallet.security.SecureStorage;
import com.namiwallet.viewmodel.CreateWalletViewModel;
import com.namiwallet.viewmodel.CreateWalletViewModel_HiltModules;
import com.namiwallet.viewmodel.HomeViewModel;
import com.namiwallet.viewmodel.HomeViewModel_HiltModules;
import com.namiwallet.viewmodel.ImportWalletViewModel;
import com.namiwallet.viewmodel.ImportWalletViewModel_HiltModules;
import com.namiwallet.viewmodel.ReceiveViewModel;
import com.namiwallet.viewmodel.ReceiveViewModel_HiltModules;
import com.namiwallet.viewmodel.SendViewModel;
import com.namiwallet.viewmodel.SendViewModel_HiltModules;
import com.namiwallet.viewmodel.SettingsViewModel;
import com.namiwallet.viewmodel.SettingsViewModel_HiltModules;
import com.namiwallet.viewmodel.VerifyMnemonicViewModel;
import com.namiwallet.viewmodel.VerifyMnemonicViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerNamiWalletApp_HiltComponents_SingletonC {
  private DaggerNamiWalletApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public NamiWalletApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements NamiWalletApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public NamiWalletApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements NamiWalletApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public NamiWalletApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements NamiWalletApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public NamiWalletApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements NamiWalletApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public NamiWalletApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements NamiWalletApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public NamiWalletApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements NamiWalletApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public NamiWalletApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements NamiWalletApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public NamiWalletApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends NamiWalletApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends NamiWalletApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends NamiWalletApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends NamiWalletApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
      injectMainActivity2(arg0);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(7).put(LazyClassKeyProvider.com_namiwallet_viewmodel_CreateWalletViewModel, CreateWalletViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_namiwallet_viewmodel_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_namiwallet_viewmodel_ImportWalletViewModel, ImportWalletViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_namiwallet_viewmodel_ReceiveViewModel, ReceiveViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_namiwallet_viewmodel_SendViewModel, SendViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_namiwallet_viewmodel_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_namiwallet_viewmodel_VerifyMnemonicViewModel, VerifyMnemonicViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectSecureStorage(instance, singletonCImpl.provideSecureStorageProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_namiwallet_viewmodel_CreateWalletViewModel = "com.namiwallet.viewmodel.CreateWalletViewModel";

      static String com_namiwallet_viewmodel_HomeViewModel = "com.namiwallet.viewmodel.HomeViewModel";

      static String com_namiwallet_viewmodel_SendViewModel = "com.namiwallet.viewmodel.SendViewModel";

      static String com_namiwallet_viewmodel_VerifyMnemonicViewModel = "com.namiwallet.viewmodel.VerifyMnemonicViewModel";

      static String com_namiwallet_viewmodel_ImportWalletViewModel = "com.namiwallet.viewmodel.ImportWalletViewModel";

      static String com_namiwallet_viewmodel_ReceiveViewModel = "com.namiwallet.viewmodel.ReceiveViewModel";

      static String com_namiwallet_viewmodel_SettingsViewModel = "com.namiwallet.viewmodel.SettingsViewModel";

      @KeepFieldType
      CreateWalletViewModel com_namiwallet_viewmodel_CreateWalletViewModel2;

      @KeepFieldType
      HomeViewModel com_namiwallet_viewmodel_HomeViewModel2;

      @KeepFieldType
      SendViewModel com_namiwallet_viewmodel_SendViewModel2;

      @KeepFieldType
      VerifyMnemonicViewModel com_namiwallet_viewmodel_VerifyMnemonicViewModel2;

      @KeepFieldType
      ImportWalletViewModel com_namiwallet_viewmodel_ImportWalletViewModel2;

      @KeepFieldType
      ReceiveViewModel com_namiwallet_viewmodel_ReceiveViewModel2;

      @KeepFieldType
      SettingsViewModel com_namiwallet_viewmodel_SettingsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends NamiWalletApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<CreateWalletViewModel> createWalletViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<ImportWalletViewModel> importWalletViewModelProvider;

    private Provider<ReceiveViewModel> receiveViewModelProvider;

    private Provider<SendViewModel> sendViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<VerifyMnemonicViewModel> verifyMnemonicViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.createWalletViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.importWalletViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.receiveViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.sendViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.verifyMnemonicViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(7).put(LazyClassKeyProvider.com_namiwallet_viewmodel_CreateWalletViewModel, ((Provider) createWalletViewModelProvider)).put(LazyClassKeyProvider.com_namiwallet_viewmodel_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_namiwallet_viewmodel_ImportWalletViewModel, ((Provider) importWalletViewModelProvider)).put(LazyClassKeyProvider.com_namiwallet_viewmodel_ReceiveViewModel, ((Provider) receiveViewModelProvider)).put(LazyClassKeyProvider.com_namiwallet_viewmodel_SendViewModel, ((Provider) sendViewModelProvider)).put(LazyClassKeyProvider.com_namiwallet_viewmodel_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_namiwallet_viewmodel_VerifyMnemonicViewModel, ((Provider) verifyMnemonicViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_namiwallet_viewmodel_ReceiveViewModel = "com.namiwallet.viewmodel.ReceiveViewModel";

      static String com_namiwallet_viewmodel_CreateWalletViewModel = "com.namiwallet.viewmodel.CreateWalletViewModel";

      static String com_namiwallet_viewmodel_SettingsViewModel = "com.namiwallet.viewmodel.SettingsViewModel";

      static String com_namiwallet_viewmodel_SendViewModel = "com.namiwallet.viewmodel.SendViewModel";

      static String com_namiwallet_viewmodel_VerifyMnemonicViewModel = "com.namiwallet.viewmodel.VerifyMnemonicViewModel";

      static String com_namiwallet_viewmodel_ImportWalletViewModel = "com.namiwallet.viewmodel.ImportWalletViewModel";

      static String com_namiwallet_viewmodel_HomeViewModel = "com.namiwallet.viewmodel.HomeViewModel";

      @KeepFieldType
      ReceiveViewModel com_namiwallet_viewmodel_ReceiveViewModel2;

      @KeepFieldType
      CreateWalletViewModel com_namiwallet_viewmodel_CreateWalletViewModel2;

      @KeepFieldType
      SettingsViewModel com_namiwallet_viewmodel_SettingsViewModel2;

      @KeepFieldType
      SendViewModel com_namiwallet_viewmodel_SendViewModel2;

      @KeepFieldType
      VerifyMnemonicViewModel com_namiwallet_viewmodel_VerifyMnemonicViewModel2;

      @KeepFieldType
      ImportWalletViewModel com_namiwallet_viewmodel_ImportWalletViewModel2;

      @KeepFieldType
      HomeViewModel com_namiwallet_viewmodel_HomeViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.namiwallet.viewmodel.CreateWalletViewModel 
          return (T) new CreateWalletViewModel(singletonCImpl.provideWalletRepositoryProvider.get());

          case 1: // com.namiwallet.viewmodel.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.provideWalletRepositoryProvider.get());

          case 2: // com.namiwallet.viewmodel.ImportWalletViewModel 
          return (T) new ImportWalletViewModel(singletonCImpl.provideWalletRepositoryProvider.get(), singletonCImpl.provideNamiCoreProvider.get());

          case 3: // com.namiwallet.viewmodel.ReceiveViewModel 
          return (T) new ReceiveViewModel(singletonCImpl.provideWalletRepositoryProvider.get());

          case 4: // com.namiwallet.viewmodel.SendViewModel 
          return (T) new SendViewModel(singletonCImpl.provideWalletRepositoryProvider.get());

          case 5: // com.namiwallet.viewmodel.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideWalletRepositoryProvider.get(), singletonCImpl.provideSecureStorageProvider.get(), singletonCImpl.provideBiometricHelperProvider.get());

          case 6: // com.namiwallet.viewmodel.VerifyMnemonicViewModel 
          return (T) new VerifyMnemonicViewModel();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends NamiWalletApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends NamiWalletApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends NamiWalletApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<SecureStorage> provideSecureStorageProvider;

    private Provider<NamiCore> provideNamiCoreProvider;

    private Provider<EthereumRpcClient> provideEthereumRpcClientProvider;

    private Provider<BitcoinElectrumClient> provideBitcoinElectrumClientProvider;

    private Provider<WalletRepository> provideWalletRepositoryProvider;

    private Provider<BiometricHelper> provideBiometricHelperProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideSecureStorageProvider = DoubleCheck.provider(new SwitchingProvider<SecureStorage>(singletonCImpl, 0));
      this.provideNamiCoreProvider = DoubleCheck.provider(new SwitchingProvider<NamiCore>(singletonCImpl, 2));
      this.provideEthereumRpcClientProvider = DoubleCheck.provider(new SwitchingProvider<EthereumRpcClient>(singletonCImpl, 3));
      this.provideBitcoinElectrumClientProvider = DoubleCheck.provider(new SwitchingProvider<BitcoinElectrumClient>(singletonCImpl, 4));
      this.provideWalletRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<WalletRepository>(singletonCImpl, 1));
      this.provideBiometricHelperProvider = DoubleCheck.provider(new SwitchingProvider<BiometricHelper>(singletonCImpl, 5));
    }

    @Override
    public void injectNamiWalletApp(NamiWalletApp arg0) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.namiwallet.security.SecureStorage 
          return (T) AppModule_ProvideSecureStorageFactory.provideSecureStorage(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.namiwallet.repository.WalletRepository 
          return (T) AppModule_ProvideWalletRepositoryFactory.provideWalletRepository(singletonCImpl.provideNamiCoreProvider.get(), singletonCImpl.provideSecureStorageProvider.get(), singletonCImpl.provideEthereumRpcClientProvider.get(), singletonCImpl.provideBitcoinElectrumClientProvider.get());

          case 2: // com.namiwallet.bridge.NamiCore 
          return (T) AppModule_ProvideNamiCoreFactory.provideNamiCore();

          case 3: // com.namiwallet.network.EthereumRpcClient 
          return (T) AppModule_ProvideEthereumRpcClientFactory.provideEthereumRpcClient();

          case 4: // com.namiwallet.network.BitcoinElectrumClient 
          return (T) AppModule_ProvideBitcoinElectrumClientFactory.provideBitcoinElectrumClient();

          case 5: // com.namiwallet.security.BiometricHelper 
          return (T) AppModule_ProvideBiometricHelperFactory.provideBiometricHelper(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
