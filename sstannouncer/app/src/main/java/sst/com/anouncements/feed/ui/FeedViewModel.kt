package sst.com.anouncements.feed.ui

import androidx.lifecycle.*
import kotlinx.coroutines.*
import sst.com.anouncements.feed.model.Feed
import sst.com.anouncements.feed.data.FeedRepository
import java.lang.IllegalArgumentException

class FeedViewModel(
    savedStateHandle: SavedStateHandle,
    feedRepository: FeedRepository
) : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val feedURL: String = savedStateHandle["feedUrl"] ?:
        throw IllegalArgumentException("missing feed URL")
    val feedLiveData: LiveData<Feed>

    init {
        feedLiveData = MutableLiveData()

        // Start retrieving data
        val deferredFeed = uiScope.async(Dispatchers.IO) {
            feedRepository.getFeed(feedURL)
        }

        // Update the UI once it's done
        deferredFeed.invokeOnCompletion {
            uiScope.launch(Dispatchers.Main) {
                feedLiveData.value = deferredFeed.getCompleted()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        // Clear any coroutines started by the ViewModel
        viewModelJob.cancel()
    }
}