package sst.com.anouncements.feed.ui

import androidx.lifecycle.*
import kotlinx.coroutines.*
import sst.com.anouncements.feed.model.Feed
import sst.com.anouncements.feed.data.FeedRepository
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.UnknownHostException

class FeedViewModel(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository
) : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val feedURL: String = savedStateHandle["feedUrl"] ?:
        throw IllegalArgumentException("missing feed URL")
    // (Feed?, Error Message?)
    // Maybe monad would work here but that's overkill
    val feedLiveData: MutableLiveData<Pair<Feed?, Exception?>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        refresh()
    }

    fun refresh() {
        isLoading.value = true
        uiScope.launch(Dispatchers.IO) {
            try {
                val feed = feedRepository.getFeed(feedURL)
                feedLiveData.postValue(Pair(feed, null))
            } catch (e: Exception) {
                feedLiveData.postValue(Pair(null, e))
            }
            isLoading.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()

        // Clear any coroutines started by the ViewModel
        viewModelJob.cancel()
    }
}