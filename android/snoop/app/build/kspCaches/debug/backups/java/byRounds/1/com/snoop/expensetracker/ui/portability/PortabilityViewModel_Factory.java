package com.snoop.expensetracker.ui.portability;

import com.snoop.expensetracker.data.portability.DataPortabilityManager;
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
public final class PortabilityViewModel_Factory implements Factory<PortabilityViewModel> {
  private final Provider<DataPortabilityManager> managerProvider;

  public PortabilityViewModel_Factory(Provider<DataPortabilityManager> managerProvider) {
    this.managerProvider = managerProvider;
  }

  @Override
  public PortabilityViewModel get() {
    return newInstance(managerProvider.get());
  }

  public static PortabilityViewModel_Factory create(
      Provider<DataPortabilityManager> managerProvider) {
    return new PortabilityViewModel_Factory(managerProvider);
  }

  public static PortabilityViewModel newInstance(DataPortabilityManager manager) {
    return new PortabilityViewModel(manager);
  }
}
