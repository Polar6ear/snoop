package com.snoop.expensetracker.work;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.snoop.expensetracker.notification.PendingAlertPoster;
import com.snoop.expensetracker.pipeline.BudgetChecker;
import dagger.internal.DaggerGenerated;
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
public final class BudgetCheckWorker_Factory {
  private final Provider<BudgetChecker> budgetCheckerProvider;

  private final Provider<PendingAlertPoster> pendingAlertPosterProvider;

  public BudgetCheckWorker_Factory(Provider<BudgetChecker> budgetCheckerProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider) {
    this.budgetCheckerProvider = budgetCheckerProvider;
    this.pendingAlertPosterProvider = pendingAlertPosterProvider;
  }

  public BudgetCheckWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, budgetCheckerProvider.get(), pendingAlertPosterProvider.get());
  }

  public static BudgetCheckWorker_Factory create(Provider<BudgetChecker> budgetCheckerProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider) {
    return new BudgetCheckWorker_Factory(budgetCheckerProvider, pendingAlertPosterProvider);
  }

  public static BudgetCheckWorker newInstance(Context appContext, WorkerParameters workerParams,
      BudgetChecker budgetChecker, PendingAlertPoster pendingAlertPoster) {
    return new BudgetCheckWorker(appContext, workerParams, budgetChecker, pendingAlertPoster);
  }
}
