package com.snoop.expensetracker.ui.budgets;

import com.snoop.expensetracker.data.db.dao.BudgetDao;
import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.db.dao.TransactionDao;
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
public final class BudgetsViewModel_Factory implements Factory<BudgetsViewModel> {
  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<BudgetDao> budgetDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  public BudgetsViewModel_Factory(Provider<CategoryDao> categoryDaoProvider,
      Provider<BudgetDao> budgetDaoProvider, Provider<TransactionDao> transactionDaoProvider) {
    this.categoryDaoProvider = categoryDaoProvider;
    this.budgetDaoProvider = budgetDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
  }

  @Override
  public BudgetsViewModel get() {
    return newInstance(categoryDaoProvider.get(), budgetDaoProvider.get(), transactionDaoProvider.get());
  }

  public static BudgetsViewModel_Factory create(Provider<CategoryDao> categoryDaoProvider,
      Provider<BudgetDao> budgetDaoProvider, Provider<TransactionDao> transactionDaoProvider) {
    return new BudgetsViewModel_Factory(categoryDaoProvider, budgetDaoProvider, transactionDaoProvider);
  }

  public static BudgetsViewModel newInstance(CategoryDao categoryDao, BudgetDao budgetDao,
      TransactionDao transactionDao) {
    return new BudgetsViewModel(categoryDao, budgetDao, transactionDao);
  }
}
