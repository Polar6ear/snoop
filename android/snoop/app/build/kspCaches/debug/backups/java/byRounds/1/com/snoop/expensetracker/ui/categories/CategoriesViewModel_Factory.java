package com.snoop.expensetracker.ui.categories;

import com.snoop.expensetracker.data.db.dao.CategoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CategoriesViewModel_Factory implements Factory<CategoriesViewModel> {
  private final Provider<CategoryDao> categoryDaoProvider;

  public CategoriesViewModel_Factory(Provider<CategoryDao> categoryDaoProvider) {
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public CategoriesViewModel get() {
    return newInstance(categoryDaoProvider.get());
  }

  public static CategoriesViewModel_Factory create(Provider<CategoryDao> categoryDaoProvider) {
    return new CategoriesViewModel_Factory(categoryDaoProvider);
  }

  public static CategoriesViewModel newInstance(CategoryDao categoryDao) {
    return new CategoriesViewModel(categoryDao);
  }
}
