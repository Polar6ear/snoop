package com.snoop.expensetracker.data.portability;

import com.snoop.expensetracker.data.db.AppDatabase;
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
public final class DataPortabilityManager_Factory implements Factory<DataPortabilityManager> {
  private final Provider<AppDatabase> dbProvider;

  public DataPortabilityManager_Factory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DataPortabilityManager get() {
    return newInstance(dbProvider.get());
  }

  public static DataPortabilityManager_Factory create(Provider<AppDatabase> dbProvider) {
    return new DataPortabilityManager_Factory(dbProvider);
  }

  public static DataPortabilityManager newInstance(AppDatabase db) {
    return new DataPortabilityManager(db);
  }
}
