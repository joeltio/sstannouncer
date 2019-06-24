package sst.com.anouncements.feed

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import sst.com.anouncements.http.DefaultWebservice
import sst.com.anouncements.http.Webservice

val feedModule: Module = module {
    factory { DefaultWebservice() } bind Webservice::class
    single { FeedRepository(get()) }
}