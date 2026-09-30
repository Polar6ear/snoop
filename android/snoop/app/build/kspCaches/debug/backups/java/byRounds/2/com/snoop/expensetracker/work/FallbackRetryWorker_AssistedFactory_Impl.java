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
public final class FallbackRetryWorker_AssistedFactory_Impl implements FallbackRetryWorker_AssistedFactory {
  private final FallbackRetryWorker_Factory delegateFactory;

  FallbackRetryWorker_AssistedFactory_Impl(FallbackRetryWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public FallbackRetryWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<FallbackRetryWorker_AssistedFactory> create(
      FallbackRetryWorker_Factory delegateFactory) {
    return InstanceFactory.create(new FallbackRetryWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<FallbackRetryWorker_AssistedFactory> createFactoryProvider(
      FallbackRetryWorker_Factory delegateFactory) {
    return InstanceFactory.create(new FallbackRetryWorker_AssistedFactory_Impl(delegateFactory));
  }
}
