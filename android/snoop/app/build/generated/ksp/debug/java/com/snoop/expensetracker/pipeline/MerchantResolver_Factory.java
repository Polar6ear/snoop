package com.snoop.expensetracker.pipeline;

import com.snoop.expensetracker.data.db.dao.MerchantMappingDao;
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
public final class MerchantResolver_Factory implements Factory<MerchantResolver> {
  private final Provider<MerchantMappingDao> merchantMappingDaoProvider;

  public MerchantResolver_Factory(Provider<MerchantMappingDao> merchantMappingDaoProvider) {
    this.merchantMappingDaoProvider = merchantMappingDaoProvider;
  }

  @Override
  public MerchantResolver get() {
    return newInstance(merchantMappingDaoProvider.get());
  }

  public static MerchantResolver_Factory create(
      Provider<MerchantMappingDao> merchantMappingDaoProvider) {
    return new MerchantResolver_Factory(merchantMappingDaoProvider);
  }

  public static MerchantResolver newInstance(MerchantMappingDao merchantMappingDao) {
    return new MerchantResolver(merchantMappingDao);
  }
}
