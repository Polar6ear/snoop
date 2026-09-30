package com.snoop.expensetracker.work;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class BudgetCheckScheduler_Factory implements Factory<BudgetCheckScheduler> {
  private final Provider<Context> contextProvider;

  public BudgetCheckScheduler_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BudgetCheckScheduler get() {
    return newInstance(contextProvider.get());
  }

  public static BudgetCheckScheduler_Factory create(Provider<Context> contextProvider) {
    return new BudgetCheckScheduler_Factory(contextProvider);
  }

  public static BudgetCheckScheduler newInstance(Context context) {
    return new BudgetCheckScheduler(context);
  }
}
