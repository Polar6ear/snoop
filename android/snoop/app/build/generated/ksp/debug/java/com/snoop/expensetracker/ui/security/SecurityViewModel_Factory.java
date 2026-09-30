package com.snoop.expensetracker.ui.security;

import com.snoop.expensetracker.security.SecurityPreferences;
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
public final class SecurityViewModel_Factory implements Factory<SecurityViewModel> {
  private final Provider<SecurityPreferences> prefsProvider;

  public SecurityViewModel_Factory(Provider<SecurityPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public SecurityViewModel get() {
    return newInstance(prefsProvider.get());
  }

  public static SecurityViewModel_Factory create(Provider<SecurityPreferences> prefsProvider) {
    return new SecurityViewModel_Factory(prefsProvider);
  }

  public static SecurityViewModel newInstance(SecurityPreferences prefs) {
    return new SecurityViewModel(prefs);
  }
}
