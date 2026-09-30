package com.snoop.expensetracker.pipeline;

import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao;
import com.snoop.expensetracker.data.db.dao.TransactionDao;
import com.snoop.expensetracker.data.repository.FallbackRepository;
import com.snoop.expensetracker.parser.ParserRegistry;
import com.snoop.expensetracker.security.SecurityPreferences;
import com.snoop.expensetracker.work.FallbackRetryScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "cast",
    "deprecation"
})
public final class NotificationProcessor_Factory implements Factory<NotificationProcessor> {
  private final Provider<ParserRegistry> parserRegistryProvider;

  private final Provider<FallbackRepository> fallbackRepositoryProvider;

  private final Provider<MerchantResolver> merchantResolverProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<FallbackPendingDao> fallbackPendingDaoProvider;

  private final Provider<FallbackRetryScheduler> fallbackRetrySchedulerProvider;

  private final Provider<SecurityPreferences> securityPreferencesProvider;

  private final Provider<ContactsResolver> contactsResolverProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<CategoryClassifier> categoryClassifierProvider;

  public NotificationProcessor_Factory(Provider<ParserRegistry> parserRegistryProvider,
      Provider<FallbackRepository> fallbackRepositoryProvider,
      Provider<MerchantResolver> merchantResolverProvider,
      Provider<TransactionDao> transactionDaoProvider,
      Provider<FallbackPendingDao> fallbackPendingDaoProvider,
      Provider<FallbackRetryScheduler> fallbackRetrySchedulerProvider,
      Provider<SecurityPreferences> securityPreferencesProvider,
      Provider<ContactsResolver> contactsResolverProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<CategoryClassifier> categoryClassifierProvider) {
    this.parserRegistryProvider = parserRegistryProvider;
    this.fallbackRepositoryProvider = fallbackRepositoryProvider;
    this.merchantResolverProvider = merchantResolverProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.fallbackPendingDaoProvider = fallbackPendingDaoProvider;
    this.fallbackRetrySchedulerProvider = fallbackRetrySchedulerProvider;
    this.securityPreferencesProvider = securityPreferencesProvider;
    this.contactsResolverProvider = contactsResolverProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.categoryClassifierProvider = categoryClassifierProvider;
  }

  @Override
  public NotificationProcessor get() {
    return newInstance(parserRegistryProvider.get(), fallbackRepositoryProvider.get(), merchantResolverProvider.get(), transactionDaoProvider.get(), fallbackPendingDaoProvider.get(), fallbackRetrySchedulerProvider.get(), securityPreferencesProvider.get(), contactsResolverProvider.get(), categoryDaoProvider.get(), categoryClassifierProvider.get());
  }

  public static NotificationProcessor_Factory create(
      Provider<ParserRegistry> parserRegistryProvider,
      Provider<FallbackRepository> fallbackRepositoryProvider,
      Provider<MerchantResolver> merchantResolverProvider,
      Provider<TransactionDao> transactionDaoProvider,
      Provider<FallbackPendingDao> fallbackPendingDaoProvider,
      Provider<FallbackRetryScheduler> fallbackRetrySchedulerProvider,
      Provider<SecurityPreferences> securityPreferencesProvider,
      Provider<ContactsResolver> contactsResolverProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<CategoryClassifier> categoryClassifierProvider) {
    return new NotificationProcessor_Factory(parserRegistryProvider, fallbackRepositoryProvider, merchantResolverProvider, transactionDaoProvider, fallbackPendingDaoProvider, fallbackRetrySchedulerProvider, securityPreferencesProvider, contactsResolverProvider, categoryDaoProvider, categoryClassifierProvider);
  }

  public static NotificationProcessor newInstance(ParserRegistry parserRegistry,
      FallbackRepository fallbackRepository, MerchantResolver merchantResolver,
      TransactionDao transactionDao, FallbackPendingDao fallbackPendingDao,
      FallbackRetryScheduler fallbackRetryScheduler, SecurityPreferences securityPreferences,
      ContactsResolver contactsResolver, CategoryDao categoryDao,
      CategoryClassifier categoryClassifier) {
    return new NotificationProcessor(parserRegistry, fallbackRepository, merchantResolver, transactionDao, fallbackPendingDao, fallbackRetryScheduler, securityPreferences, contactsResolver, categoryDao, categoryClassifier);
  }
}
