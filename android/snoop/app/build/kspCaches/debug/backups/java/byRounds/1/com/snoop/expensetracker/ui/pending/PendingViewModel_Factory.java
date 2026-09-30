package com.snoop.expensetracker.ui.pending;

import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao;
import com.snoop.expensetracker.data.db.dao.TransactionDao;
import com.snoop.expensetracker.notification.PendingAlertPoster;
import com.snoop.expensetracker.pipeline.BudgetChecker;
import com.snoop.expensetracker.pipeline.MerchantResolver;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "cast",
    "deprecation"
})
public final class PendingViewModel_Factory implements Factory<PendingViewModel> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<FallbackPendingDao> fallbackPendingDaoProvider;

  private final Provider<MerchantResolver> merchantResolverProvider;

  private final Provider<BudgetChecker> budgetCheckerProvider;

  private final Provider<PendingAlertPoster> pendingAlertPosterProvider;

  public PendingViewModel_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<FallbackPendingDao> fallbackPendingDaoProvider,
      Provider<MerchantResolver> merchantResolverProvider,
      Provider<BudgetChecker> budgetCheckerProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.fallbackPendingDaoProvider = fallbackPendingDaoProvider;
    this.merchantResolverProvider = merchantResolverProvider;
    this.budgetCheckerProvider = budgetCheckerProvider;
    this.pendingAlertPosterProvider = pendingAlertPosterProvider;
  }

  @Override
  public PendingViewModel get() {
    return newInstance(transactionDaoProvider.get(), categoryDaoProvider.get(), fallbackPendingDaoProvider.get(), merchantResolverProvider.get(), budgetCheckerProvider.get(), pendingAlertPosterProvider.get());
  }

  public static PendingViewModel_Factory create(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<FallbackPendingDao> fallbackPendingDaoProvider,
      Provider<MerchantResolver> merchantResolverProvider,
      Provider<BudgetChecker> budgetCheckerProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider) {
    return new PendingViewModel_Factory(transactionDaoProvider, categoryDaoProvider, fallbackPendingDaoProvider, merchantResolverProvider, budgetCheckerProvider, pendingAlertPosterProvider);
  }

  public static PendingViewModel newInstance(TransactionDao transactionDao, CategoryDao categoryDao,
      FallbackPendingDao fallbackPendingDao, MerchantResolver merchantResolver,
      BudgetChecker budgetChecker, PendingAlertPoster pendingAlertPoster) {
    return new PendingViewModel(transactionDao, categoryDao, fallbackPendingDao, merchantResolver, budgetChecker, pendingAlertPoster);
  }
}
