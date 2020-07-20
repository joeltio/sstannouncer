package com.sst.anouncements.feed

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import com.sst.anouncements.feed.data.database.FeedDAL
import com.sst.anouncements.feed.data.database.room.FeedDatabase
import com.sst.anouncements.feed.data.database.room.RoomDAL
import com.sst.anouncements.feed.data.FeedRepository
import com.sst.anouncements.feed.ui.FeedViewModel

val feedModule: Module = module {
    // Use the RoomDAL implementation for storing and retrieving data
    single {
        Room.databaseBuilder(androidApplication(), FeedDatabase::class.java, "feed-db").build()
    }
    factory { RoomDAL(get()) } bind FeedDAL::class

    single { FeedRepository(get(), get()) }

    viewModel { (handle: SavedStateHandle) -> FeedViewModel(handle, get()) }
}