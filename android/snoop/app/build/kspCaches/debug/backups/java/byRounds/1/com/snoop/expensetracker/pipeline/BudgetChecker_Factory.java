package com.snoop.expensetracker.pipeline;

import com.snoop.expensetracker.data.db.dao.BudgetDao;
import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.db.dao.TransactionDao;
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
public final class BudgetChecker_Factory implements Factory<BudgetChecker> {
  private final Provider<BudgetDao> budgetDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public BudgetChecker_Factory(Provider<BudgetDao> budgetDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    this.budgetDaoProvider = budgetDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public BudgetChecker get() {
    return newInstance(budgetDaoProvider.get(), transactionDaoProvider.get(), categoryDaoProvider.get());
  }

  public static BudgetChecker_Factory create(Provider<BudgetDao> budgetDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    return new BudgetChecker_Factory(budgetDaoProvider, transactionDaoProvider, categoryDaoProvider);
  }

  public static BudgetChecker newInstance(BudgetDao budgetDao, TransactionDao transactionDao,
      CategoryDao categoryDao) {
    return new BudgetChecker(budgetDao, transactionDao, categoryDao);
  }
}
