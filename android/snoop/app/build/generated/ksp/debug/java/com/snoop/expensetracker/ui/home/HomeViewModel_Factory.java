package com.snoop.expensetracker.ui.home;

import com.snoop.expensetracker.data.db.dao.BalanceDao;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<TransactionDao> daoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<BalanceDao> balanceDaoProvider;

  public HomeViewModel_Factory(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<BalanceDao> balanceDaoProvider) {
    this.daoProvider = daoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.balanceDaoProvider = balanceDaoProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(daoProvider.get(), categoryDaoProvider.get(), balanceDaoProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<BalanceDao> balanceDaoProvider) {
    return new HomeViewModel_Factory(daoProvider, categoryDaoProvider, balanceDaoProvider);
  }

  public static HomeViewModel newInstance(TransactionDao dao, CategoryDao categoryDao,
      BalanceDao balanceDao) {
    return new HomeViewModel(dao, categoryDao, balanceDao);
  }
}
