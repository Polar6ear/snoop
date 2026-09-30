package com.snoop.expensetracker.di

import android.content.Context
import android.provider.Telephony
import com.snoop.expensetracker.parser.ParserRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ParserModule {

    /**
     * ParserRegistry needs to know the device's current default SMS app package (so SBI/HDFC
     * can match notifications from it — see SbiParser/HdfcParser). Resolved once at DI-graph
     * creation time; if the user changes their default SMS app later without restarting the app,
     * this won't pick it up until next process start, which is an acceptable Phase-1 gap.
     */
    @Provides
    @Singleton
    fun provideParserRegistry(@ApplicationContext context: Context): ParserRegistry {
        val defaultSmsPackage = try {
            Telephony.Sms.getDefaultSmsPackage(context)
        } catch (e: Exception) {
            null
        }
        return ParserRegistry(defaultSmsPackageName = defaultSmsPackage)
    }
}
