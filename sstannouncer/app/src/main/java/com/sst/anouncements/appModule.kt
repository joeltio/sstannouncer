package com.sst.anouncements

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import com.sst.anouncements.http.DefaultWebservice
import com.sst.anouncements.http.Webservice
import com.sst.anouncements.notification.DefaultNotificationService
import com.sst.anouncements.notification.NotificationService

val appModule: Module = module {
    // Use the default webservice for the repository
    factory { DefaultWebservice() } bind Webservice::class

    // Use the default notification service
    factory { DefaultNotificationService(get()) } bind NotificationService::class
}