package com.snoop.expensetracker.di;

import android.content.Context;
import com.snoop.expensetracker.parser.ParserRegistry;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ParserModule_ProvideParserRegistryFactory implements Factory<ParserRegistry> {
  private final Provider<Context> contextProvider;

  public ParserModule_ProvideParserRegistryFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ParserRegistry get() {
    return provideParserRegistry(contextProvider.get());
  }

  public static ParserModule_ProvideParserRegistryFactory create(
      Provider<Context> contextProvider) {
    return new ParserModule_ProvideParserRegistryFactory(contextProvider);
  }

  public static ParserRegistry provideParserRegistry(Context context) {
    return Preconditions.checkNotNullFromProvides(ParserModule.INSTANCE.provideParserRegistry(context));
  }
}
