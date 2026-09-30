package com.snoop.expensetracker.ui.home;

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
public final class InsightsViewModel_Factory implements Factory<InsightsViewModel> {
  private final Provider<TransactionDao> daoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public InsightsViewModel_Factory(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.daoProvider = daoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public InsightsViewModel get() {
    return newInstance(daoProvider.get(), categoryDaoProvider.get());
  }

  public static InsightsViewModel_Factory create(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    return new InsightsViewModel_Factory(daoProvider, categoryDaoProvider);
  }

  public static InsightsViewModel newInstance(TransactionDao dao, CategoryDao categoryDao) {
    return new InsightsViewModel(dao, categoryDao);
  }
}
