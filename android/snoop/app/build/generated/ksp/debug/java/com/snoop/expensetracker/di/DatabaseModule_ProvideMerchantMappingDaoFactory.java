package com.snoop.expensetracker.di;

import com.snoop.expensetracker.data.db.AppDatabase;
import com.snoop.expensetracker.data.db.dao.MerchantMappingDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideMerchantMappingDaoFactory implements Factory<MerchantMappingDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideMerchantMappingDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MerchantMappingDao get() {
    return provideMerchantMappingDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideMerchantMappingDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideMerchantMappingDaoFactory(dbProvider);
  }

  public static MerchantMappingDao provideMerchantMappingDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMerchantMappingDao(db));
  }
}
