package com.snoop.expensetracker.work;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.snoop.expensetracker.data.db.dao.FallbackPendingDao;
import com.snoop.expensetracker.notification.PendingAlertPoster;
import com.snoop.expensetracker.pipeline.BudgetChecker;
import com.snoop.expensetracker.pipeline.NotificationProcessor;
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
public final class FallbackRetryWorker_Factory {
  private final Provider<FallbackPendingDao> fallbackPendingDaoProvider;

  private final Provider<NotificationProcessor> notificationProcessorProvider;

  private final Provider<PendingAlertPoster> pendingAlertPosterProvider;

  private final Provider<BudgetChecker> budgetCheckerProvider;

  public FallbackRetryWorker_Factory(Provider<FallbackPendingDao> fallbackPendingDaoProvider,
      Provider<NotificationProcessor> notificationProcessorProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider,
      Provider<BudgetChecker> budgetCheckerProvider) {
    this.fallbackPendingDaoProvider = fallbackPendingDaoProvider;
    this.notificationProcessorProvider = notificationProcessorProvider;
    this.pendingAlertPosterProvider = pendingAlertPosterProvider;
    this.budgetCheckerProvider = budgetCheckerProvider;
  }

  public FallbackRetryWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, fallbackPendingDaoProvider.get(), notificationProcessorProvider.get(), pendingAlertPosterProvider.get(), budgetCheckerProvider.get());
  }

  public static FallbackRetryWorker_Factory create(
      Provider<FallbackPendingDao> fallbackPendingDaoProvider,
      Provider<NotificationProcessor> notificationProcessorProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider,
      Provider<BudgetChecker> budgetCheckerProvider) {
    return new FallbackRetryWorker_Factory(fallbackPendingDaoProvider, notificationProcessorProvider, pendingAlertPosterProvider, budgetCheckerProvider);
  }

  public static FallbackRetryWorker newInstance(Context appContext, WorkerParameters workerParams,
      FallbackPendingDao fallbackPendingDao, NotificationProcessor notificationProcessor,
      PendingAlertPoster pendingAlertPoster, BudgetChecker budgetChecker) {
    return new FallbackRetryWorker(appContext, workerParams, fallbackPendingDao, notificationProcessor, pendingAlertPoster, budgetChecker);
  }
}
