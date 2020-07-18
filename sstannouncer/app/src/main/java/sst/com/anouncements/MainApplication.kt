package sst.com.anouncements

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import sst.com.anouncements.feed.feedModule
import sst.com.anouncements.notification.NotificationService

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()

            androidContext(this@MainApplication)

            androidFileProperties()

            modules(appModule, feedModule)
        }

        val notificationService: NotificationService by inject()
        notificationService.setup()
    }
}