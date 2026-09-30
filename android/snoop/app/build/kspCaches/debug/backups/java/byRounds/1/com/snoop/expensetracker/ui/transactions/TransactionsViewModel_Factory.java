package com.snoop.expensetracker.ui.transactions;

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
public final class TransactionsViewModel_Factory implements Factory<TransactionsViewModel> {
  private final Provider<TransactionDao> daoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public TransactionsViewModel_Factory(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.daoProvider = daoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public TransactionsViewModel get() {
    return newInstance(daoProvider.get(), categoryDaoProvider.get());
  }

  public static TransactionsViewModel_Factory create(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    return new TransactionsViewModel_Factory(daoProvider, categoryDaoProvider);
  }

  public static TransactionsViewModel newInstance(TransactionDao dao, CategoryDao categoryDao) {
    return new TransactionsViewModel(dao, categoryDao);
  }
}
