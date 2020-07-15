package sst.com.anouncements.feed

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import sst.com.anouncements.feed.data.database.FeedDAL
import sst.com.anouncements.feed.data.database.room.FeedDatabase
import sst.com.anouncements.feed.data.database.room.RoomDAL
import sst.com.anouncements.feed.data.FeedRepository
import sst.com.anouncements.feed.ui.FeedViewModel
import sst.com.anouncements.http.DefaultWebservice
import sst.com.anouncements.http.Webservice

val feedModule: Module = module {
    // Use the default webservice for the repository
    factory { DefaultWebservice() } bind Webservice::class
    // Use the RoomDAL implementation for storing and retrieving data
    single {
        Room.databaseBuilder(androidApplication(), FeedDatabase::class.java, "feed-db").build()
    }
    factory { RoomDAL(get()) } bind FeedDAL::class

    single { FeedRepository(get(), get()) }

    viewModel { (handle: SavedStateHandle) -> FeedViewModel(handle, get()) }
}