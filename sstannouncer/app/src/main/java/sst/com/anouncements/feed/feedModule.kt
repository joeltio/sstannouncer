package sst.com.anouncements.feed

import org.koin.core.module.Module
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val feedModule: Module = module {
    viewModel { FeedViewModel() }
}