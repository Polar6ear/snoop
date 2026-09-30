package com.snoop.expensetracker.ui.more;

import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.db.dao.MerchantMappingDao;
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
public final class MerchantRulesViewModel_Factory implements Factory<MerchantRulesViewModel> {
  private final Provider<MerchantMappingDao> merchantDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public MerchantRulesViewModel_Factory(Provider<MerchantMappingDao> merchantDaoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.merchantDaoProvider = merchantDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public MerchantRulesViewModel get() {
    return newInstance(merchantDaoProvider.get(), categoryDaoProvider.get());
  }

  public static MerchantRulesViewModel_Factory create(
      Provider<MerchantMappingDao> merchantDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    return new MerchantRulesViewModel_Factory(merchantDaoProvider, categoryDaoProvider);
  }

  public static MerchantRulesViewModel newInstance(MerchantMappingDao merchantDao,
      CategoryDao categoryDao) {
    return new MerchantRulesViewModel(merchantDao, categoryDao);
  }
}
