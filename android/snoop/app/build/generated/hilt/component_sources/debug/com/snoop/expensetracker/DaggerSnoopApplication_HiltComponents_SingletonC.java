package com.snoop.expensetracker;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.snoop.expensetracker.data.db.AppDatabase;
import com.snoop.expensetracker.data.db.dao.BalanceDao;
import com.snoop.expensetracker.data.db.dao.BudgetDao;
import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao;
import com.snoop.expensetracker.data.db.dao.MerchantMappingDao;
import com.snoop.expensetracker.data.db.dao.TransactionDao;
import com.snoop.expensetracker.data.network.FallbackApi;
import com.snoop.expensetracker.data.portability.DataPortabilityManager;
import com.snoop.expensetracker.data.repository.FallbackRepository;
import com.snoop.expensetracker.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.snoop.expensetracker.di.DatabaseModule_ProvideBalanceDaoFactory;
import com.snoop.expensetracker.di.DatabaseModule_ProvideBudgetDaoFactory;
import com.snoop.expensetracker.di.DatabaseModule_ProvideCategoryDaoFactory;
import com.snoop.expensetracker.di.DatabaseModule_ProvideFallbackPendingDaoFactory;
import com.snoop.expensetracker.di.DatabaseModule_ProvideMerchantMappingDaoFactory;
import com.snoop.expensetracker.di.DatabaseModule_ProvideTransactionDaoFactory;
import com.snoop.expensetracker.di.NetworkModule_ProvideAuthInterceptorFactory;
import com.snoop.expensetracker.di.NetworkModule_ProvideFallbackApiFactory;
import com.snoop.expensetracker.di.NetworkModule_ProvideMoshiFactory;
import com.snoop.expensetracker.di.NetworkModule_ProvideOkHttpClientFactory;
import com.snoop.expensetracker.di.NetworkModule_ProvideRetrofitFactory;
import com.snoop.expensetracker.di.ParserModule_ProvideParserRegistryFactory;
import com.snoop.expensetracker.notification.PendingAlertPoster;
import com.snoop.expensetracker.notification.SnoopNotificationListenerService;
import com.snoop.expensetracker.notification.SnoopNotificationListenerService_MembersInjector;
import com.snoop.expensetracker.parser.ParserRegistry;
import com.snoop.expensetracker.pipeline.BudgetChecker;
import com.snoop.expensetracker.pipeline.CategoryClassifier;
import com.snoop.expensetracker.pipeline.ContactsResolver;
import com.snoop.expensetracker.pipeline.MerchantResolver;
import com.snoop.expensetracker.pipeline.NotificationProcessor;
import com.snoop.expensetracker.security.SecurityPreferences;
import com.snoop.expensetracker.security.UserProfilePreferences;
import com.snoop.expensetracker.ui.MainActivity;
import com.snoop.expensetracker.ui.MainActivity_MembersInjector;
import com.snoop.expensetracker.ui.budgets.BudgetsViewModel;
import com.snoop.expensetracker.ui.budgets.BudgetsViewModel_HiltModules;
import com.snoop.expensetracker.ui.categories.CategoriesViewModel;
import com.snoop.expensetracker.ui.categories.CategoriesViewModel_HiltModules;
import com.snoop.expensetracker.ui.home.HomeViewModel;
import com.snoop.expensetracker.ui.home.HomeViewModel_HiltModules;
import com.snoop.expensetracker.ui.home.InsightsViewModel;
import com.snoop.expensetracker.ui.home.InsightsViewModel_HiltModules;
import com.snoop.expensetracker.ui.manualentry.ManualEntryViewModel;
import com.snoop.expensetracker.ui.manualentry.ManualEntryViewModel_HiltModules;
import com.snoop.expensetracker.ui.more.MerchantRulesViewModel;
import com.snoop.expensetracker.ui.more.MerchantRulesViewModel_HiltModules;
import com.snoop.expensetracker.ui.more.MoreViewModel;
import com.snoop.expensetracker.ui.more.MoreViewModel_HiltModules;
import com.snoop.expensetracker.ui.more.RecurringViewModel;
import com.snoop.expensetracker.ui.more.RecurringViewModel_HiltModules;
import com.snoop.expensetracker.ui.pending.PendingViewModel;
import com.snoop.expensetracker.ui.pending.PendingViewModel_HiltModules;
import com.snoop.expensetracker.ui.portability.PortabilityViewModel;
import com.snoop.expensetracker.ui.portability.PortabilityViewModel_HiltModules;
import com.snoop.expensetracker.ui.security.SecurityViewModel;
import com.snoop.expensetracker.ui.security.SecurityViewModel_HiltModules;
import com.snoop.expensetracker.ui.transaction.TransactionViewModel;
import com.snoop.expensetracker.ui.transaction.TransactionViewModel_HiltModules;
import com.snoop.expensetracker.ui.transactions.TransactionsViewModel;
import com.snoop.expensetracker.ui.transactions.TransactionsViewModel_HiltModules;
import com.snoop.expensetracker.work.BudgetCheckScheduler;
import com.snoop.expensetracker.work.BudgetCheckWorker;
import com.snoop.expensetracker.work.BudgetCheckWorker_AssistedFactory;
import com.snoop.expensetracker.work.FallbackRetryScheduler;
import com.snoop.expensetracker.work.FallbackRetryWorker;
import com.snoop.expensetracker.work.FallbackRetryWorker_AssistedFactory;
import com.squareup.moshi.Moshi;
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
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
    "cast",
    "deprecation"
})
public final class DaggerSnoopApplication_HiltComponents_SingletonC {
  private DaggerSnoopApplication_HiltComponents_SingletonC() {
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

    public SnoopApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SnoopApplication_HiltComponents.ActivityRetainedC.Builder {
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
    public SnoopApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SnoopApplication_HiltComponents.ActivityC.Builder {
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
    public SnoopApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SnoopApplication_HiltComponents.FragmentC.Builder {
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
    public SnoopApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SnoopApplication_HiltComponents.ViewWithFragmentC.Builder {
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
    public SnoopApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SnoopApplication_HiltComponents.ViewC.Builder {
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
    public SnoopApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SnoopApplication_HiltComponents.ViewModelC.Builder {
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
    public SnoopApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SnoopApplication_HiltComponents.ServiceC.Builder {
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
    public SnoopApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SnoopApplication_HiltComponents.ViewWithFragmentC {
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

  private static final class FragmentCImpl extends SnoopApplication_HiltComponents.FragmentC {
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

  private static final class ViewCImpl extends SnoopApplication_HiltComponents.ViewC {
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

  private static final class ActivityCImpl extends SnoopApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(13).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_budgets_BudgetsViewModel, BudgetsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_categories_CategoriesViewModel, CategoriesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_home_InsightsViewModel, InsightsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_manualentry_ManualEntryViewModel, ManualEntryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_more_MerchantRulesViewModel, MerchantRulesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_more_MoreViewModel, MoreViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_pending_PendingViewModel, PendingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_portability_PortabilityViewModel, PortabilityViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_more_RecurringViewModel, RecurringViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_security_SecurityViewModel, SecurityViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_transaction_TransactionViewModel, TransactionViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_transactions_TransactionsViewModel, TransactionsViewModel_HiltModules.KeyModule.provide()).build());
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

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectSecurityPreferences(instance, singletonCImpl.securityPreferencesProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_snoop_expensetracker_ui_budgets_BudgetsViewModel = "com.snoop.expensetracker.ui.budgets.BudgetsViewModel";

      static String com_snoop_expensetracker_ui_categories_CategoriesViewModel = "com.snoop.expensetracker.ui.categories.CategoriesViewModel";

      static String com_snoop_expensetracker_ui_manualentry_ManualEntryViewModel = "com.snoop.expensetracker.ui.manualentry.ManualEntryViewModel";

      static String com_snoop_expensetracker_ui_transaction_TransactionViewModel = "com.snoop.expensetracker.ui.transaction.TransactionViewModel";

      static String com_snoop_expensetracker_ui_home_HomeViewModel = "com.snoop.expensetracker.ui.home.HomeViewModel";

      static String com_snoop_expensetracker_ui_portability_PortabilityViewModel = "com.snoop.expensetracker.ui.portability.PortabilityViewModel";

      static String com_snoop_expensetracker_ui_transactions_TransactionsViewModel = "com.snoop.expensetracker.ui.transactions.TransactionsViewModel";

      static String com_snoop_expensetracker_ui_more_MerchantRulesViewModel = "com.snoop.expensetracker.ui.more.MerchantRulesViewModel";

      static String com_snoop_expensetracker_ui_more_RecurringViewModel = "com.snoop.expensetracker.ui.more.RecurringViewModel";

      static String com_snoop_expensetracker_ui_security_SecurityViewModel = "com.snoop.expensetracker.ui.security.SecurityViewModel";

      static String com_snoop_expensetracker_ui_pending_PendingViewModel = "com.snoop.expensetracker.ui.pending.PendingViewModel";

      static String com_snoop_expensetracker_ui_home_InsightsViewModel = "com.snoop.expensetracker.ui.home.InsightsViewModel";

      static String com_snoop_expensetracker_ui_more_MoreViewModel = "com.snoop.expensetracker.ui.more.MoreViewModel";

      @KeepFieldType
      BudgetsViewModel com_snoop_expensetracker_ui_budgets_BudgetsViewModel2;

      @KeepFieldType
      CategoriesViewModel com_snoop_expensetracker_ui_categories_CategoriesViewModel2;

      @KeepFieldType
      ManualEntryViewModel com_snoop_expensetracker_ui_manualentry_ManualEntryViewModel2;

      @KeepFieldType
      TransactionViewModel com_snoop_expensetracker_ui_transaction_TransactionViewModel2;

      @KeepFieldType
      HomeViewModel com_snoop_expensetracker_ui_home_HomeViewModel2;

      @KeepFieldType
      PortabilityViewModel com_snoop_expensetracker_ui_portability_PortabilityViewModel2;

      @KeepFieldType
      TransactionsViewModel com_snoop_expensetracker_ui_transactions_TransactionsViewModel2;

      @KeepFieldType
      MerchantRulesViewModel com_snoop_expensetracker_ui_more_MerchantRulesViewModel2;

      @KeepFieldType
      RecurringViewModel com_snoop_expensetracker_ui_more_RecurringViewModel2;

      @KeepFieldType
      SecurityViewModel com_snoop_expensetracker_ui_security_SecurityViewModel2;

      @KeepFieldType
      PendingViewModel com_snoop_expensetracker_ui_pending_PendingViewModel2;

      @KeepFieldType
      InsightsViewModel com_snoop_expensetracker_ui_home_InsightsViewModel2;

      @KeepFieldType
      MoreViewModel com_snoop_expensetracker_ui_more_MoreViewModel2;
    }
  }

  private static final class ViewModelCImpl extends SnoopApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<BudgetsViewModel> budgetsViewModelProvider;

    private Provider<CategoriesViewModel> categoriesViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<InsightsViewModel> insightsViewModelProvider;

    private Provider<ManualEntryViewModel> manualEntryViewModelProvider;

    private Provider<MerchantRulesViewModel> merchantRulesViewModelProvider;

    private Provider<MoreViewModel> moreViewModelProvider;

    private Provider<PendingViewModel> pendingViewModelProvider;

    private Provider<PortabilityViewModel> portabilityViewModelProvider;

    private Provider<RecurringViewModel> recurringViewModelProvider;

    private Provider<SecurityViewModel> securityViewModelProvider;

    private Provider<TransactionViewModel> transactionViewModelProvider;

    private Provider<TransactionsViewModel> transactionsViewModelProvider;

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
      this.budgetsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.categoriesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.insightsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.manualEntryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.merchantRulesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.moreViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.pendingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.portabilityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.recurringViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.securityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.transactionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.transactionsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(13).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_budgets_BudgetsViewModel, ((Provider) budgetsViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_categories_CategoriesViewModel, ((Provider) categoriesViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_home_InsightsViewModel, ((Provider) insightsViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_manualentry_ManualEntryViewModel, ((Provider) manualEntryViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_more_MerchantRulesViewModel, ((Provider) merchantRulesViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_more_MoreViewModel, ((Provider) moreViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_pending_PendingViewModel, ((Provider) pendingViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_portability_PortabilityViewModel, ((Provider) portabilityViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_more_RecurringViewModel, ((Provider) recurringViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_security_SecurityViewModel, ((Provider) securityViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_transaction_TransactionViewModel, ((Provider) transactionViewModelProvider)).put(LazyClassKeyProvider.com_snoop_expensetracker_ui_transactions_TransactionsViewModel, ((Provider) transactionsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_snoop_expensetracker_ui_pending_PendingViewModel = "com.snoop.expensetracker.ui.pending.PendingViewModel";

      static String com_snoop_expensetracker_ui_portability_PortabilityViewModel = "com.snoop.expensetracker.ui.portability.PortabilityViewModel";

      static String com_snoop_expensetracker_ui_manualentry_ManualEntryViewModel = "com.snoop.expensetracker.ui.manualentry.ManualEntryViewModel";

      static String com_snoop_expensetracker_ui_transactions_TransactionsViewModel = "com.snoop.expensetracker.ui.transactions.TransactionsViewModel";

      static String com_snoop_expensetracker_ui_categories_CategoriesViewModel = "com.snoop.expensetracker.ui.categories.CategoriesViewModel";

      static String com_snoop_expensetracker_ui_budgets_BudgetsViewModel = "com.snoop.expensetracker.ui.budgets.BudgetsViewModel";

      static String com_snoop_expensetracker_ui_transaction_TransactionViewModel = "com.snoop.expensetracker.ui.transaction.TransactionViewModel";

      static String com_snoop_expensetracker_ui_more_MoreViewModel = "com.snoop.expensetracker.ui.more.MoreViewModel";

      static String com_snoop_expensetracker_ui_more_RecurringViewModel = "com.snoop.expensetracker.ui.more.RecurringViewModel";

      static String com_snoop_expensetracker_ui_security_SecurityViewModel = "com.snoop.expensetracker.ui.security.SecurityViewModel";

      static String com_snoop_expensetracker_ui_home_HomeViewModel = "com.snoop.expensetracker.ui.home.HomeViewModel";

      static String com_snoop_expensetracker_ui_home_InsightsViewModel = "com.snoop.expensetracker.ui.home.InsightsViewModel";

      static String com_snoop_expensetracker_ui_more_MerchantRulesViewModel = "com.snoop.expensetracker.ui.more.MerchantRulesViewModel";

      @KeepFieldType
      PendingViewModel com_snoop_expensetracker_ui_pending_PendingViewModel2;

      @KeepFieldType
      PortabilityViewModel com_snoop_expensetracker_ui_portability_PortabilityViewModel2;

      @KeepFieldType
      ManualEntryViewModel com_snoop_expensetracker_ui_manualentry_ManualEntryViewModel2;

      @KeepFieldType
      TransactionsViewModel com_snoop_expensetracker_ui_transactions_TransactionsViewModel2;

      @KeepFieldType
      CategoriesViewModel com_snoop_expensetracker_ui_categories_CategoriesViewModel2;

      @KeepFieldType
      BudgetsViewModel com_snoop_expensetracker_ui_budgets_BudgetsViewModel2;

      @KeepFieldType
      TransactionViewModel com_snoop_expensetracker_ui_transaction_TransactionViewModel2;

      @KeepFieldType
      MoreViewModel com_snoop_expensetracker_ui_more_MoreViewModel2;

      @KeepFieldType
      RecurringViewModel com_snoop_expensetracker_ui_more_RecurringViewModel2;

      @KeepFieldType
      SecurityViewModel com_snoop_expensetracker_ui_security_SecurityViewModel2;

      @KeepFieldType
      HomeViewModel com_snoop_expensetracker_ui_home_HomeViewModel2;

      @KeepFieldType
      InsightsViewModel com_snoop_expensetracker_ui_home_InsightsViewModel2;

      @KeepFieldType
      MerchantRulesViewModel com_snoop_expensetracker_ui_more_MerchantRulesViewModel2;
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
          case 0: // com.snoop.expensetracker.ui.budgets.BudgetsViewModel 
          return (T) new BudgetsViewModel(singletonCImpl.categoryDao(), singletonCImpl.budgetDao(), singletonCImpl.transactionDao());

          case 1: // com.snoop.expensetracker.ui.categories.CategoriesViewModel 
          return (T) new CategoriesViewModel(singletonCImpl.categoryDao());

          case 2: // com.snoop.expensetracker.ui.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.transactionDao(), singletonCImpl.categoryDao(), singletonCImpl.balanceDao());

          case 3: // com.snoop.expensetracker.ui.home.InsightsViewModel 
          return (T) new InsightsViewModel(singletonCImpl.transactionDao(), singletonCImpl.categoryDao());

          case 4: // com.snoop.expensetracker.ui.manualentry.ManualEntryViewModel 
          return (T) new ManualEntryViewModel(singletonCImpl.transactionDao(), singletonCImpl.categoryDao(), singletonCImpl.merchantResolverProvider.get(), singletonCImpl.budgetCheckerProvider.get(), singletonCImpl.pendingAlertPosterProvider.get());

          case 5: // com.snoop.expensetracker.ui.more.MerchantRulesViewModel 
          return (T) new MerchantRulesViewModel(singletonCImpl.merchantMappingDao(), singletonCImpl.categoryDao());

          case 6: // com.snoop.expensetracker.ui.more.MoreViewModel 
          return (T) new MoreViewModel(singletonCImpl.userProfilePreferencesProvider.get());

          case 7: // com.snoop.expensetracker.ui.pending.PendingViewModel 
          return (T) new PendingViewModel(singletonCImpl.transactionDao(), singletonCImpl.categoryDao(), singletonCImpl.fallbackPendingDao(), singletonCImpl.merchantResolverProvider.get(), singletonCImpl.budgetCheckerProvider.get(), singletonCImpl.pendingAlertPosterProvider.get());

          case 8: // com.snoop.expensetracker.ui.portability.PortabilityViewModel 
          return (T) new PortabilityViewModel(singletonCImpl.dataPortabilityManagerProvider.get());

          case 9: // com.snoop.expensetracker.ui.more.RecurringViewModel 
          return (T) new RecurringViewModel(singletonCImpl.transactionDao());

          case 10: // com.snoop.expensetracker.ui.security.SecurityViewModel 
          return (T) new SecurityViewModel(singletonCImpl.securityPreferencesProvider.get());

          case 11: // com.snoop.expensetracker.ui.transaction.TransactionViewModel 
          return (T) new TransactionViewModel(singletonCImpl.transactionDao(), singletonCImpl.categoryDao(), singletonCImpl.merchantResolverProvider.get());

          case 12: // com.snoop.expensetracker.ui.transactions.TransactionsViewModel 
          return (T) new TransactionsViewModel(singletonCImpl.transactionDao(), singletonCImpl.categoryDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SnoopApplication_HiltComponents.ActivityRetainedC {
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

  private static final class ServiceCImpl extends SnoopApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectSnoopNotificationListenerService(
        SnoopNotificationListenerService snoopNotificationListenerService) {
      injectSnoopNotificationListenerService2(snoopNotificationListenerService);
    }

    private SnoopNotificationListenerService injectSnoopNotificationListenerService2(
        SnoopNotificationListenerService instance) {
      SnoopNotificationListenerService_MembersInjector.injectNotificationProcessor(instance, singletonCImpl.notificationProcessorProvider.get());
      SnoopNotificationListenerService_MembersInjector.injectBudgetChecker(instance, singletonCImpl.budgetCheckerProvider.get());
      SnoopNotificationListenerService_MembersInjector.injectPendingAlertPoster(instance, singletonCImpl.pendingAlertPosterProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends SnoopApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<BudgetChecker> budgetCheckerProvider;

    private Provider<PendingAlertPoster> pendingAlertPosterProvider;

    private Provider<BudgetCheckWorker_AssistedFactory> budgetCheckWorker_AssistedFactoryProvider;

    private Provider<ParserRegistry> provideParserRegistryProvider;

    private Provider<Interceptor> provideAuthInterceptorProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Moshi> provideMoshiProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<FallbackApi> provideFallbackApiProvider;

    private Provider<FallbackRepository> fallbackRepositoryProvider;

    private Provider<MerchantResolver> merchantResolverProvider;

    private Provider<FallbackRetryScheduler> fallbackRetrySchedulerProvider;

    private Provider<SecurityPreferences> securityPreferencesProvider;

    private Provider<ContactsResolver> contactsResolverProvider;

    private Provider<CategoryClassifier> categoryClassifierProvider;

    private Provider<NotificationProcessor> notificationProcessorProvider;

    private Provider<FallbackRetryWorker_AssistedFactory> fallbackRetryWorker_AssistedFactoryProvider;

    private Provider<BudgetCheckScheduler> budgetCheckSchedulerProvider;

    private Provider<UserProfilePreferences> userProfilePreferencesProvider;

    private Provider<DataPortabilityManager> dataPortabilityManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private CategoryDao categoryDao() {
      return DatabaseModule_ProvideCategoryDaoFactory.provideCategoryDao(provideAppDatabaseProvider.get());
    }

    private BudgetDao budgetDao() {
      return DatabaseModule_ProvideBudgetDaoFactory.provideBudgetDao(provideAppDatabaseProvider.get());
    }

    private TransactionDao transactionDao() {
      return DatabaseModule_ProvideTransactionDaoFactory.provideTransactionDao(provideAppDatabaseProvider.get());
    }

    private FallbackPendingDao fallbackPendingDao() {
      return DatabaseModule_ProvideFallbackPendingDaoFactory.provideFallbackPendingDao(provideAppDatabaseProvider.get());
    }

    private MerchantMappingDao merchantMappingDao() {
      return DatabaseModule_ProvideMerchantMappingDaoFactory.provideMerchantMappingDao(provideAppDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return MapBuilder.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>newMapBuilder(2).put("com.snoop.expensetracker.work.BudgetCheckWorker", ((Provider) budgetCheckWorker_AssistedFactoryProvider)).put("com.snoop.expensetracker.work.FallbackRetryWorker", ((Provider) fallbackRetryWorker_AssistedFactoryProvider)).build();
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private BalanceDao balanceDao() {
      return DatabaseModule_ProvideBalanceDaoFactory.provideBalanceDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 0));
      this.budgetCheckerProvider = DoubleCheck.provider(new SwitchingProvider<BudgetChecker>(singletonCImpl, 2));
      this.pendingAlertPosterProvider = DoubleCheck.provider(new SwitchingProvider<PendingAlertPoster>(singletonCImpl, 3));
      this.budgetCheckWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<BudgetCheckWorker_AssistedFactory>(singletonCImpl, 1));
      this.provideParserRegistryProvider = DoubleCheck.provider(new SwitchingProvider<ParserRegistry>(singletonCImpl, 6));
      this.provideAuthInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<Interceptor>(singletonCImpl, 11));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 10));
      this.provideMoshiProvider = DoubleCheck.provider(new SwitchingProvider<Moshi>(singletonCImpl, 12));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 9));
      this.provideFallbackApiProvider = DoubleCheck.provider(new SwitchingProvider<FallbackApi>(singletonCImpl, 8));
      this.fallbackRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FallbackRepository>(singletonCImpl, 7));
      this.merchantResolverProvider = DoubleCheck.provider(new SwitchingProvider<MerchantResolver>(singletonCImpl, 13));
      this.fallbackRetrySchedulerProvider = DoubleCheck.provider(new SwitchingProvider<FallbackRetryScheduler>(singletonCImpl, 14));
      this.securityPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<SecurityPreferences>(singletonCImpl, 15));
      this.contactsResolverProvider = DoubleCheck.provider(new SwitchingProvider<ContactsResolver>(singletonCImpl, 16));
      this.categoryClassifierProvider = DoubleCheck.provider(new SwitchingProvider<CategoryClassifier>(singletonCImpl, 17));
      this.notificationProcessorProvider = DoubleCheck.provider(new SwitchingProvider<NotificationProcessor>(singletonCImpl, 5));
      this.fallbackRetryWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<FallbackRetryWorker_AssistedFactory>(singletonCImpl, 4));
      this.budgetCheckSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<BudgetCheckScheduler>(singletonCImpl, 18));
      this.userProfilePreferencesProvider = DoubleCheck.provider(new SwitchingProvider<UserProfilePreferences>(singletonCImpl, 19));
      this.dataPortabilityManagerProvider = DoubleCheck.provider(new SwitchingProvider<DataPortabilityManager>(singletonCImpl, 20));
    }

    @Override
    public void injectSnoopApplication(SnoopApplication snoopApplication) {
      injectSnoopApplication2(snoopApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private SnoopApplication injectSnoopApplication2(SnoopApplication instance) {
      SnoopApplication_MembersInjector.injectCategoryDao(instance, categoryDao());
      SnoopApplication_MembersInjector.injectHiltWorkerFactory(instance, hiltWorkerFactory());
      SnoopApplication_MembersInjector.injectBudgetCheckScheduler(instance, budgetCheckSchedulerProvider.get());
      SnoopApplication_MembersInjector.injectFallbackRetryScheduler(instance, fallbackRetrySchedulerProvider.get());
      return instance;
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
          case 0: // com.snoop.expensetracker.data.db.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.snoop.expensetracker.work.BudgetCheckWorker_AssistedFactory 
          return (T) new BudgetCheckWorker_AssistedFactory() {
            @Override
            public BudgetCheckWorker create(Context appContext, WorkerParameters workerParams) {
              return new BudgetCheckWorker(appContext, workerParams, singletonCImpl.budgetCheckerProvider.get(), singletonCImpl.pendingAlertPosterProvider.get());
            }
          };

          case 2: // com.snoop.expensetracker.pipeline.BudgetChecker 
          return (T) new BudgetChecker(singletonCImpl.budgetDao(), singletonCImpl.transactionDao(), singletonCImpl.categoryDao());

          case 3: // com.snoop.expensetracker.notification.PendingAlertPoster 
          return (T) new PendingAlertPoster(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.snoop.expensetracker.work.FallbackRetryWorker_AssistedFactory 
          return (T) new FallbackRetryWorker_AssistedFactory() {
            @Override
            public FallbackRetryWorker create(Context appContext2, WorkerParameters workerParams2) {
              return new FallbackRetryWorker(appContext2, workerParams2, singletonCImpl.fallbackPendingDao(), singletonCImpl.notificationProcessorProvider.get(), singletonCImpl.pendingAlertPosterProvider.get(), singletonCImpl.budgetCheckerProvider.get());
            }
          };

          case 5: // com.snoop.expensetracker.pipeline.NotificationProcessor 
          return (T) new NotificationProcessor(singletonCImpl.provideParserRegistryProvider.get(), singletonCImpl.fallbackRepositoryProvider.get(), singletonCImpl.merchantResolverProvider.get(), singletonCImpl.transactionDao(), singletonCImpl.fallbackPendingDao(), singletonCImpl.fallbackRetrySchedulerProvider.get(), singletonCImpl.securityPreferencesProvider.get(), singletonCImpl.contactsResolverProvider.get(), singletonCImpl.categoryDao(), singletonCImpl.categoryClassifierProvider.get());

          case 6: // com.snoop.expensetracker.parser.ParserRegistry 
          return (T) ParserModule_ProvideParserRegistryFactory.provideParserRegistry(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.snoop.expensetracker.data.repository.FallbackRepository 
          return (T) new FallbackRepository(singletonCImpl.provideFallbackApiProvider.get(), singletonCImpl.categoryDao());

          case 8: // com.snoop.expensetracker.data.network.FallbackApi 
          return (T) NetworkModule_ProvideFallbackApiFactory.provideFallbackApi(singletonCImpl.provideRetrofitProvider.get());

          case 9: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideMoshiProvider.get());

          case 10: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.provideAuthInterceptorProvider.get());

          case 11: // okhttp3.Interceptor 
          return (T) NetworkModule_ProvideAuthInterceptorFactory.provideAuthInterceptor();

          case 12: // com.squareup.moshi.Moshi 
          return (T) NetworkModule_ProvideMoshiFactory.provideMoshi();

          case 13: // com.snoop.expensetracker.pipeline.MerchantResolver 
          return (T) new MerchantResolver(singletonCImpl.merchantMappingDao());

          case 14: // com.snoop.expensetracker.work.FallbackRetryScheduler 
          return (T) new FallbackRetryScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 15: // com.snoop.expensetracker.security.SecurityPreferences 
          return (T) new SecurityPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 16: // com.snoop.expensetracker.pipeline.ContactsResolver 
          return (T) new ContactsResolver(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 17: // com.snoop.expensetracker.pipeline.CategoryClassifier 
          return (T) new CategoryClassifier(singletonCImpl.categoryDao());

          case 18: // com.snoop.expensetracker.work.BudgetCheckScheduler 
          return (T) new BudgetCheckScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 19: // com.snoop.expensetracker.security.UserProfilePreferences 
          return (T) new UserProfilePreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 20: // com.snoop.expensetracker.data.portability.DataPortabilityManager 
          return (T) new DataPortabilityManager(singletonCImpl.provideAppDatabaseProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
