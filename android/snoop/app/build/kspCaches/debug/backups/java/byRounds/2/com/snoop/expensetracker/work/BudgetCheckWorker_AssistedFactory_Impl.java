package com.snoop.expensetracker.work;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BudgetCheckWorker_AssistedFactory_Impl implements BudgetCheckWorker_AssistedFactory {
  private final BudgetCheckWorker_Factory delegateFactory;

  BudgetCheckWorker_AssistedFactory_Impl(BudgetCheckWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public BudgetCheckWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<BudgetCheckWorker_AssistedFactory> create(
      BudgetCheckWorker_Factory delegateFactory) {
    return InstanceFactory.create(new BudgetCheckWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<BudgetCheckWorker_AssistedFactory> createFactoryProvider(
      BudgetCheckWorker_Factory delegateFactory) {
    return InstanceFactory.create(new BudgetCheckWorker_AssistedFactory_Impl(delegateFactory));
  }
}
