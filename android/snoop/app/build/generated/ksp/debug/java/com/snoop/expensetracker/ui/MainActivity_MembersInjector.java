package com.snoop.expensetracker.ui;

import com.snoop.expensetracker.security.SecurityPreferences;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SecurityPreferences> securityPreferencesProvider;

  public MainActivity_MembersInjector(Provider<SecurityPreferences> securityPreferencesProvider) {
    this.securityPreferencesProvider = securityPreferencesProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SecurityPreferences> securityPreferencesProvider) {
    return new MainActivity_MembersInjector(securityPreferencesProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSecurityPreferences(instance, securityPreferencesProvider.get());
  }

  @InjectedFieldSignature("com.snoop.expensetracker.ui.MainActivity.securityPreferences")
  public static void injectSecurityPreferences(MainActivity instance,
      SecurityPreferences securityPreferences) {
    instance.securityPreferences = securityPreferences;
  }
}
