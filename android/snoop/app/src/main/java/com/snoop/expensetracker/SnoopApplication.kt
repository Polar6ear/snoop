package com.snoop.expensetracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.snoop.expensetracker.data.db.dao.CategoryDao
import com.snoop.expensetracker.data.db.entities.CategoryEntity
import com.snoop.expensetracker.data.db.entities.DefaultCategories
import com.snoop.expensetracker.work.BudgetCheckScheduler
import com.snoop.expensetracker.work.FallbackRetryScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SnoopApplication : Application(), Configuration.Provider {

    @Inject lateinit var categoryDao: CategoryDao
    @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory
    @Inject lateinit var budgetCheckScheduler: BudgetCheckScheduler
    @Inject lateinit var fallbackRetryScheduler: FallbackRetryScheduler

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // WorkManager's default Configuration.Provider hook — required so BudgetCheckWorker (which
    // has @Inject constructor params) can actually be instantiated by WorkManager instead of
    // crashing with "no default constructor" the first time the job runs.
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        seedDefaultCategoriesIfNeeded()
        budgetCheckScheduler.schedulePeriodicCheck()
        fallbackRetryScheduler.schedule()
    }

    /** Spec §11: one universal default set, seeded once. insertAll uses IGNORE conflict so this is safe to call every launch. */
    private fun seedDefaultCategoriesIfNeeded() {
        applicationScope.launch {
            if (categoryDao.count() > 0) return@launch
            val seeded = DefaultCategories.seeds.map { seed ->
                CategoryEntity(name = seed.name, icon = seed.icon, color = seed.color, isCustom = false)
            }
            categoryDao.insertAll(seeded)
        }
    }
}
