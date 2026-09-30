package com.snoop.expensetracker;

import androidx.hilt.work.HiltWorkerFactory;
import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.work.BudgetCheckScheduler;
import com.snoop.expensetracker.work.FallbackRetryScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SnoopApplication_MembersInjector implements MembersInjector<SnoopApplication> {
  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<HiltWorkerFactory> hiltWorkerFactoryProvider;

  private final Provider<BudgetCheckScheduler> budgetCheckSchedulerProvider;

  private final Provider<FallbackRetryScheduler> fallbackRetrySchedulerProvider;

  public SnoopApplication_MembersInjector(Provider<CategoryDao> categoryDaoProvider,
      Provider<HiltWorkerFactory> hiltWorkerFactoryProvider,
      Provider<BudgetCheckScheduler> budgetCheckSchedulerProvider,
      Provider<FallbackRetryScheduler> fallbackRetrySchedulerProvider) {
    this.categoryDaoProvider = categoryDaoProvider;
    this.hiltWorkerFactoryProvider = hiltWorkerFactoryProvider;
    this.budgetCheckSchedulerProvider = budgetCheckSchedulerProvider;
    this.fallbackRetrySchedulerProvider = fallbackRetrySchedulerProvider;
  }

  public static MembersInjector<SnoopApplication> create(Provider<CategoryDao> categoryDaoProvider,
      Provider<HiltWorkerFactory> hiltWorkerFactoryProvider,
      Provider<BudgetCheckScheduler> budgetCheckSchedulerProvider,
      Provider<FallbackRetryScheduler> fallbackRetrySchedulerProvider) {
    return new SnoopApplication_MembersInjector(categoryDaoProvider, hiltWorkerFactoryProvider, budgetCheckSchedulerProvider, fallbackRetrySchedulerProvider);
  }

  @Override
  public void injectMembers(SnoopApplication instance) {
    injectCategoryDao(instance, categoryDaoProvider.get());
    injectHiltWorkerFactory(instance, hiltWorkerFactoryProvider.get());
    injectBudgetCheckScheduler(instance, budgetCheckSchedulerProvider.get());
    injectFallbackRetryScheduler(instance, fallbackRetrySchedulerProvider.get());
  }

  @InjectedFieldSignature("com.snoop.expensetracker.SnoopApplication.categoryDao")
  public static void injectCategoryDao(SnoopApplication instance, CategoryDao categoryDao) {
    instance.categoryDao = categoryDao;
  }

  @InjectedFieldSignature("com.snoop.expensetracker.SnoopApplication.hiltWorkerFactory")
  public static void injectHiltWorkerFactory(SnoopApplication instance,
      HiltWorkerFactory hiltWorkerFactory) {
    instance.hiltWorkerFactory = hiltWorkerFactory;
  }

  @InjectedFieldSignature("com.snoop.expensetracker.SnoopApplication.budgetCheckScheduler")
  public static void injectBudgetCheckScheduler(SnoopApplication instance,
      BudgetCheckScheduler budgetCheckScheduler) {
    instance.budgetCheckScheduler = budgetCheckScheduler;
  }

  @InjectedFieldSignature("com.snoop.expensetracker.SnoopApplication.fallbackRetryScheduler")
  public static void injectFallbackRetryScheduler(SnoopApplication instance,
      FallbackRetryScheduler fallbackRetryScheduler) {
    instance.fallbackRetryScheduler = fallbackRetryScheduler;
  }
}
