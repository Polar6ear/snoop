package com.snoop.expensetracker.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.snoop.expensetracker.notification.NotificationAccessUtils
import com.snoop.expensetracker.ui.budgets.BudgetsScreen
import com.snoop.expensetracker.ui.categories.CategoriesScreen
import com.snoop.expensetracker.ui.home.HomeScreen
import com.snoop.expensetracker.ui.home.InsightsScreen
import com.snoop.expensetracker.ui.more.MerchantRulesScreen
import com.snoop.expensetracker.ui.more.MoreScreen
import com.snoop.expensetracker.ui.more.RecurringScreen
import com.snoop.expensetracker.ui.onboarding.OnboardingScreen
import com.snoop.expensetracker.ui.pending.PendingScreen
import com.snoop.expensetracker.ui.portability.PortabilityScreen
import com.snoop.expensetracker.ui.security.SecurityScreen
import com.snoop.expensetracker.ui.transaction.TransactionDetailScreen
import com.snoop.expensetracker.ui.transactions.TransactionsScreen

private object Routes {

 const val ONBOARDING = "onboarding"
 const val HOME = "home"
 const val TRANSACTIONS = "transactions"
 const val BUDGETS = "budgets"
 const val MORE = "more"
 const val CATEGORIES = "categories"
 const val MERCHANT_RULES = "merchant_rules"
 const val RECURRING = "recurring"
 const val INSIGHTS = "insights"
 const val DETAIL = "detail/{id}"
 const val PORTABILITY = "portability"
 const val SECURITY = "security"
 const val PENDING = "pending"
}

@Composable
fun SnoopNavHost() {

 val context = LocalContext.current
 val nav = rememberNavController()

 val startDestination =
  if (NotificationAccessUtils.isNotificationAccessGranted(context)) {
   Routes.HOME
  } else {
   Routes.ONBOARDING
  }

 val backStackEntry by nav.currentBackStackEntryAsState()
 val currentRoute = backStackEntry?.destination?.route

 LaunchedEffect(Unit) {
  if (context is android.app.Activity &&
      context.intent.getLongExtra(com.snoop.expensetracker.notification.PendingAlertPoster.EXTRA_OPEN_PENDING_TRANSACTION_ID, -1L) > 0L
  ) {
   nav.navigate(Routes.PENDING)
   context.intent.removeExtra(com.snoop.expensetracker.notification.PendingAlertPoster.EXTRA_OPEN_PENDING_TRANSACTION_ID)
  }
 }

 val tabs = remember {
  setOf(
   Routes.HOME,
   Routes.TRANSACTIONS,
   Routes.BUDGETS,
   Routes.MORE
  )
 }

 Scaffold(

  bottomBar = {

   if (currentRoute in tabs) {

    NavigationBar(
     containerColor = com.snoop.expensetracker.ui.theme.SnoopColors.Paper,
     contentColor = com.snoop.expensetracker.ui.theme.SnoopColors.Ink,
     tonalElevation = 0.dp
    ) {

     NavigationBarItem(
      selected = currentRoute == Routes.HOME,
      onClick = {
       nav.tab(Routes.HOME)
      },
      icon = {
       Icon(
        imageVector = Icons.Filled.Home,
        contentDescription = "Home"
       )
      },
      label = {
       Text("Home", color = if (currentRoute == Routes.HOME) com.snoop.expensetracker.ui.theme.SnoopColors.Ink else com.snoop.expensetracker.ui.theme.SnoopColors.Stone)
      }
     )

     NavigationBarItem(
      selected = currentRoute == Routes.TRANSACTIONS,
      onClick = {
       nav.tab(Routes.TRANSACTIONS)
      },
      icon = {
       Icon(
        imageVector = Icons.Filled.ReceiptLong,
        contentDescription = "Transactions"
       )
      },
      label = {
       Text("History", color = if (currentRoute == Routes.TRANSACTIONS) com.snoop.expensetracker.ui.theme.SnoopColors.Ink else com.snoop.expensetracker.ui.theme.SnoopColors.Stone)
      }
     )

     NavigationBarItem(
      selected = currentRoute == Routes.BUDGETS,
      onClick = {
       nav.tab(Routes.BUDGETS)
      },
      icon = {
       Icon(
        imageVector = Icons.Filled.PieChart,
        contentDescription = "Budgets"
       )
      },
      label = {
       Text("Budgets", color = if (currentRoute == Routes.BUDGETS) com.snoop.expensetracker.ui.theme.SnoopColors.Ink else com.snoop.expensetracker.ui.theme.SnoopColors.Stone)
      }
     )

     NavigationBarItem(
      selected = currentRoute == Routes.MORE,
      onClick = {
       nav.tab(Routes.MORE)
      },
      icon = {
       Icon(
        imageVector = Icons.Filled.MoreHoriz,
        contentDescription = "More"
       )
      },
      label = {
       Text("More", color = if (currentRoute == Routes.MORE) com.snoop.expensetracker.ui.theme.SnoopColors.Ink else com.snoop.expensetracker.ui.theme.SnoopColors.Stone)
      }
     )
    }
   }
  }
 ) { paddingValues ->

  NavHost(
   navController = nav,
   startDestination = startDestination,
   modifier = Modifier.padding(
    bottom = if (currentRoute in tabs) {
     paddingValues.calculateBottomPadding()
    } else {
     0.dp
    }
   )
  ) {

   // --------------------------------
   // ONBOARDING
   // --------------------------------

   composable(Routes.ONBOARDING) {

    OnboardingScreen {

     nav.navigate(Routes.HOME) {

      popUpTo(Routes.ONBOARDING) {
       inclusive = true
      }
     }
    }
   }

   // --------------------------------
   // HOME
   // --------------------------------

   composable(Routes.HOME) {

    HomeScreen(
     onTransaction = { id ->
      nav.navigate("detail/$id")
     },
     onPending = {
      nav.navigate(Routes.PENDING)
     },
     onHistory = {
      nav.tab(Routes.TRANSACTIONS)
     }
    )
   }

   // --------------------------------
   // TRANSACTIONS
   // --------------------------------

   composable(Routes.TRANSACTIONS) {

    TransactionsScreen(
     onTransaction = { id ->
      nav.navigate("detail/$id")
     }
    )
   }

   // --------------------------------
   // BUDGETS
   // --------------------------------

   composable(Routes.BUDGETS) {

    BudgetsScreen(
     onCreateCategory = { nav.navigate(Routes.CATEGORIES) },
     onInsights = { nav.navigate(Routes.INSIGHTS) }
    )
   }

   // --------------------------------
   // MORE
   // --------------------------------

   composable(Routes.MORE) {

    MoreScreen(

     onCategories = {
      nav.navigate(Routes.CATEGORIES)
     },

     onMerchantRules = {
      nav.navigate(Routes.MERCHANT_RULES)
     },

     onRecurring = {
      nav.navigate(Routes.RECURRING)
     },

     onPortability = {
      nav.navigate(Routes.PORTABILITY)
     },

     onSecurity = {
      nav.navigate(Routes.SECURITY)
     },
     onPending = {
      nav.navigate(Routes.PENDING)
     }
    )
   }

   // --------------------------------
   // CATEGORIES
   // --------------------------------

   composable(Routes.CATEGORIES) {

    CategoriesScreen()
   }

   // --------------------------------
   // MERCHANT RULES
   // --------------------------------

   composable(Routes.MERCHANT_RULES) {

    MerchantRulesScreen(
     onBack = {
      nav.popBackStack()
     }
    )
   }

   // --------------------------------
   // RECURRING
   // --------------------------------

   composable(Routes.RECURRING) {

    RecurringScreen(
     onBack = {
      nav.popBackStack()
     }
    )
   }

   // --------------------------------
   // INSIGHTS
   // --------------------------------

   composable(Routes.INSIGHTS) {

    InsightsScreen(
     onBack = {
      nav.popBackStack()
     }
    )
   }

   // --------------------------------
   // PORTABILITY
   // --------------------------------

   composable(Routes.PORTABILITY) {

    PortabilityScreen(
     onBack = {
      nav.popBackStack()
     }
    )
   }

   // --------------------------------
   // SECURITY
   // --------------------------------

   composable(Routes.SECURITY) {

    SecurityScreen(
     onBack = {
      nav.popBackStack()
     }
    )
   }

   // --------------------------------
   // PENDING / NEEDS REVIEW
   // --------------------------------

   composable(Routes.PENDING) {
    PendingScreen()
   }

   // --------------------------------
   // TRANSACTION DETAIL
   // --------------------------------

   composable(Routes.DETAIL) { entry ->

    val id = entry.arguments
     ?.getString("id")
     ?.toLongOrNull()

    if (id != null) {

     TransactionDetailScreen(

      id = id,

      onBack = {
       nav.popBackStack()
      },

      onDeleted = {
       nav.popBackStack()
      }
     )
    }
   }
  }
 }
}

// -----------------------------------------
// Bottom navigation helper
// -----------------------------------------

private fun NavController.tab(route: String) {

 navigate(route) {

  popUpTo(
   graph.findStartDestination().id
  ) {
   saveState = true
  }

  launchSingleTop = true
  restoreState = true
 }
}