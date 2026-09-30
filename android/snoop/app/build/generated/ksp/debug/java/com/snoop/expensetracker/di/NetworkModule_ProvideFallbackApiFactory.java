package com.snoop.expensetracker.di;

import com.snoop.expensetracker.data.network.FallbackApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideFallbackApiFactory implements Factory<FallbackApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideFallbackApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public FallbackApi get() {
    return provideFallbackApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideFallbackApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideFallbackApiFactory(retrofitProvider);
  }

  public static FallbackApi provideFallbackApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFallbackApi(retrofit));
  }
}
