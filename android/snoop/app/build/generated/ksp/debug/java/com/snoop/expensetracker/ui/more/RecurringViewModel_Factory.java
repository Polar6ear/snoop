package com.snoop.expensetracker.ui.more;

import com.snoop.expensetracker.data.db.dao.TransactionDao;
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
public final class RecurringViewModel_Factory implements Factory<RecurringViewModel> {
  private final Provider<TransactionDao> daoProvider;

  public RecurringViewModel_Factory(Provider<TransactionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RecurringViewModel get() {
    return newInstance(daoProvider.get());
  }

  public static RecurringViewModel_Factory create(Provider<TransactionDao> daoProvider) {
    return new RecurringViewModel_Factory(daoProvider);
  }

  public static RecurringViewModel newInstance(TransactionDao dao) {
    return new RecurringViewModel(dao);
  }
}
