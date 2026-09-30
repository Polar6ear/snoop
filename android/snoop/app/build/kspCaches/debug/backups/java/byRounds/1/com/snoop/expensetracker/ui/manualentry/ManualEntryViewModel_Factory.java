package com.snoop.expensetracker.ui.manualentry;

import com.snoop.expensetracker.data.db.dao.CategoryDao;
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
public final class ManualEntryViewModel_Factory implements Factory<ManualEntryViewModel> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<MerchantResolver> merchantResolverProvider;

  private final Provider<BudgetChecker> budgetCheckerProvider;

  private final Provider<PendingAlertPoster> pendingAlertPosterProvider;

  public ManualEntryViewModel_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<MerchantResolver> merchantResolverProvider,
      Provider<BudgetChecker> budgetCheckerProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.merchantResolverProvider = merchantResolverProvider;
    this.budgetCheckerProvider = budgetCheckerProvider;
    this.pendingAlertPosterProvider = pendingAlertPosterProvider;
  }

  @Override
  public ManualEntryViewModel get() {
    return newInstance(transactionDaoProvider.get(), categoryDaoProvider.get(), merchantResolverProvider.get(), budgetCheckerProvider.get(), pendingAlertPosterProvider.get());
  }

  public static ManualEntryViewModel_Factory create(Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<MerchantResolver> merchantResolverProvider,
      Provider<BudgetChecker> budgetCheckerProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider) {
    return new ManualEntryViewModel_Factory(transactionDaoProvider, categoryDaoProvider, merchantResolverProvider, budgetCheckerProvider, pendingAlertPosterProvider);
  }

  public static ManualEntryViewModel newInstance(TransactionDao transactionDao,
      CategoryDao categoryDao, MerchantResolver merchantResolver, BudgetChecker budgetChecker,
      PendingAlertPoster pendingAlertPoster) {
    return new ManualEntryViewModel(transactionDao, categoryDao, merchantResolver, budgetChecker, pendingAlertPoster);
  }
}
