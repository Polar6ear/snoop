package com.snoop.expensetracker.di;

import com.snoop.expensetracker.data.db.AppDatabase;
import com.snoop.expensetracker.data.db.dao.BalanceDao;
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
public final class DatabaseModule_ProvideBalanceDaoFactory implements Factory<BalanceDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideBalanceDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BalanceDao get() {
    return provideBalanceDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBalanceDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideBalanceDaoFactory(dbProvider);
  }

  public static BalanceDao provideBalanceDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBalanceDao(db));
  }
}
