package com.snoop.expensetracker.ui.transaction;

import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.db.dao.TransactionDao;
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
public final class TransactionViewModel_Factory implements Factory<TransactionViewModel> {
  private final Provider<TransactionDao> daoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<MerchantResolver> merchantResolverProvider;

  public TransactionViewModel_Factory(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<MerchantResolver> merchantResolverProvider) {
    this.daoProvider = daoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.merchantResolverProvider = merchantResolverProvider;
  }

  @Override
  public TransactionViewModel get() {
    return newInstance(daoProvider.get(), categoryDaoProvider.get(), merchantResolverProvider.get());
  }

  public static TransactionViewModel_Factory create(Provider<TransactionDao> daoProvider,
      Provider<CategoryDao> categoryDaoProvider,
      Provider<MerchantResolver> merchantResolverProvider) {
    return new TransactionViewModel_Factory(daoProvider, categoryDaoProvider, merchantResolverProvider);
  }

  public static TransactionViewModel newInstance(TransactionDao dao, CategoryDao categoryDao,
      MerchantResolver merchantResolver) {
    return new TransactionViewModel(dao, categoryDao, merchantResolver);
  }
}
