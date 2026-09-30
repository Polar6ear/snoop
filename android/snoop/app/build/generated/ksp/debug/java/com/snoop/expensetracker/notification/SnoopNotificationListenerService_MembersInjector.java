package com.snoop.expensetracker.notification;

import com.snoop.expensetracker.pipeline.BudgetChecker;
import com.snoop.expensetracker.pipeline.NotificationProcessor;
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
public final class SnoopNotificationListenerService_MembersInjector implements MembersInjector<SnoopNotificationListenerService> {
  private final Provider<NotificationProcessor> notificationProcessorProvider;

  private final Provider<BudgetChecker> budgetCheckerProvider;

  private final Provider<PendingAlertPoster> pendingAlertPosterProvider;

  public SnoopNotificationListenerService_MembersInjector(
      Provider<NotificationProcessor> notificationProcessorProvider,
      Provider<BudgetChecker> budgetCheckerProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider) {
    this.notificationProcessorProvider = notificationProcessorProvider;
    this.budgetCheckerProvider = budgetCheckerProvider;
    this.pendingAlertPosterProvider = pendingAlertPosterProvider;
  }

  public static MembersInjector<SnoopNotificationListenerService> create(
      Provider<NotificationProcessor> notificationProcessorProvider,
      Provider<BudgetChecker> budgetCheckerProvider,
      Provider<PendingAlertPoster> pendingAlertPosterProvider) {
    return new SnoopNotificationListenerService_MembersInjector(notificationProcessorProvider, budgetCheckerProvider, pendingAlertPosterProvider);
  }

  @Override
  public void injectMembers(SnoopNotificationListenerService instance) {
    injectNotificationProcessor(instance, notificationProcessorProvider.get());
    injectBudgetChecker(instance, budgetCheckerProvider.get());
    injectPendingAlertPoster(instance, pendingAlertPosterProvider.get());
  }

  @InjectedFieldSignature("com.snoop.expensetracker.notification.SnoopNotificationListenerService.notificationProcessor")
  public static void injectNotificationProcessor(SnoopNotificationListenerService instance,
      NotificationProcessor notificationProcessor) {
    instance.notificationProcessor = notificationProcessor;
  }

  @InjectedFieldSignature("com.snoop.expensetracker.notification.SnoopNotificationListenerService.budgetChecker")
  public static void injectBudgetChecker(SnoopNotificationListenerService instance,
      BudgetChecker budgetChecker) {
    instance.budgetChecker = budgetChecker;
  }

  @InjectedFieldSignature("com.snoop.expensetracker.notification.SnoopNotificationListenerService.pendingAlertPoster")
  public static void injectPendingAlertPoster(SnoopNotificationListenerService instance,
      PendingAlertPoster pendingAlertPoster) {
    instance.pendingAlertPoster = pendingAlertPoster;
  }
}
