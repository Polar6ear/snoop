package com.snoop.expensetracker.pipeline;

import com.snoop.expensetracker.data.db.dao.CategoryDao;
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
public final class CategoryClassifier_Factory implements Factory<CategoryClassifier> {
  private final Provider<CategoryDao> categoryDaoProvider;

  public CategoryClassifier_Factory(Provider<CategoryDao> categoryDaoProvider) {
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public CategoryClassifier get() {
    return newInstance(categoryDaoProvider.get());
  }

  public static CategoryClassifier_Factory create(Provider<CategoryDao> categoryDaoProvider) {
    return new CategoryClassifier_Factory(categoryDaoProvider);
  }

  public static CategoryClassifier newInstance(CategoryDao categoryDao) {
    return new CategoryClassifier(categoryDao);
  }
}
