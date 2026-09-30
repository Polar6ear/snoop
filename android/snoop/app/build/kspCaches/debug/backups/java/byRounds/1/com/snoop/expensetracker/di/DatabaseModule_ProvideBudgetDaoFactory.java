package com.snoop.expensetracker.di;

import com.snoop.expensetracker.data.db.AppDatabase;
import com.snoop.expensetracker.data.db.dao.BudgetDao;
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
public final class DatabaseModule_ProvideBudgetDaoFactory implements Factory<BudgetDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideBudgetDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BudgetDao get() {
    return provideBudgetDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBudgetDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideBudgetDaoFactory(dbProvider);
  }

  public static BudgetDao provideBudgetDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBudgetDao(db));
  }
}
