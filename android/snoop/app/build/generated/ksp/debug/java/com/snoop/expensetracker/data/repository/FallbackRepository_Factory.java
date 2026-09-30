package com.snoop.expensetracker.data.repository;

import com.snoop.expensetracker.data.db.dao.CategoryDao;
import com.snoop.expensetracker.data.network.FallbackApi;
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
public final class FallbackRepository_Factory implements Factory<FallbackRepository> {
  private final Provider<FallbackApi> apiProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public FallbackRepository_Factory(Provider<FallbackApi> apiProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.apiProvider = apiProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public FallbackRepository get() {
    return newInstance(apiProvider.get(), categoryDaoProvider.get());
  }

  public static FallbackRepository_Factory create(Provider<FallbackApi> apiProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    return new FallbackRepository_Factory(apiProvider, categoryDaoProvider);
  }

  public static FallbackRepository newInstance(FallbackApi api, CategoryDao categoryDao) {
    return new FallbackRepository(api, categoryDao);
  }
}
