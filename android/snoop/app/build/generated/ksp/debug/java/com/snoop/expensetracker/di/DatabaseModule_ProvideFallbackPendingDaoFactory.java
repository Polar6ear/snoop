package com.snoop.expensetracker.di;

import com.snoop.expensetracker.data.db.AppDatabase;
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao;
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
public final class DatabaseModule_ProvideFallbackPendingDaoFactory implements Factory<FallbackPendingDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideFallbackPendingDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FallbackPendingDao get() {
    return provideFallbackPendingDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideFallbackPendingDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideFallbackPendingDaoFactory(dbProvider);
  }

  public static FallbackPendingDao provideFallbackPendingDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFallbackPendingDao(db));
  }
}
