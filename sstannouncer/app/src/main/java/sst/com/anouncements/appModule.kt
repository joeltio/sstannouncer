package sst.com.anouncements

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import sst.com.anouncements.http.DefaultWebservice
import sst.com.anouncements.http.Webservice
import sst.com.anouncements.notification.DefaultNotificationService
import sst.com.anouncements.notification.NotificationService

val appModule: Module = module {
    // Use the default webservice for the repository
    factory { DefaultWebservice() } bind Webservice::class

    // Use the default notification service
    factory { DefaultNotificationService(get()) } bind NotificationService::class
}