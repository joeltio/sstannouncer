package sst.com.anouncements.feed.ui

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateVMFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import sst.com.anouncements.feed.data.FeedRepository

class FeedViewModelFactory(
    private val feedRepository: FeedRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateVMFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel(handle, feedRepository) as T
    }
}