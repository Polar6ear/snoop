package com.snoop.expensetracker.work;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = FallbackRetryWorker.class
)
public interface FallbackRetryWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.snoop.expensetracker.work.FallbackRetryWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      FallbackRetryWorker_AssistedFactory factory);
}
