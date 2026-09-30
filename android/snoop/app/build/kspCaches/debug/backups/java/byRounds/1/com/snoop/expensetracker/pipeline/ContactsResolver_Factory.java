package com.snoop.expensetracker.pipeline;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ContactsResolver_Factory implements Factory<ContactsResolver> {
  private final Provider<Context> contextProvider;

  public ContactsResolver_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ContactsResolver get() {
    return newInstance(contextProvider.get());
  }

  public static ContactsResolver_Factory create(Provider<Context> contextProvider) {
    return new ContactsResolver_Factory(contextProvider);
  }

  public static ContactsResolver newInstance(Context context) {
    return new ContactsResolver(context);
  }
}
