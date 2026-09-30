package com.snoop.expensetracker.ui.more;

import com.snoop.expensetracker.security.UserProfilePreferences;
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
public final class MoreViewModel_Factory implements Factory<MoreViewModel> {
  private final Provider<UserProfilePreferences> prefsProvider;

  public MoreViewModel_Factory(Provider<UserProfilePreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public MoreViewModel get() {
    return newInstance(prefsProvider.get());
  }

  public static MoreViewModel_Factory create(Provider<UserProfilePreferences> prefsProvider) {
    return new MoreViewModel_Factory(prefsProvider);
  }

  public static MoreViewModel newInstance(UserProfilePreferences prefs) {
    return new MoreViewModel(prefs);
  }
}
