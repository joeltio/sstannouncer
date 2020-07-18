package sst.com.anouncements.feed.ui

import androidx.lifecycle.*
import kotlinx.coroutines.*
import sst.com.anouncements.feed.model.Feed
import sst.com.anouncements.feed.data.FeedRepository
import java.lang.IllegalArgumentException

class FeedViewModel(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository
) : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val feedURL: String = savedStateHandle["feedUrl"] ?:
        throw IllegalArgumentException("missing feed URL")
    val feedLiveData: MutableLiveData<Feed> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        refresh()
    }

    fun refresh() {
        isLoading.value = true
        uiScope.launch(Dispatchers.IO) {
            feedLiveData.postValue(feedRepository.getFeed(feedURL))
            isLoading.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()

        // Clear any coroutines started by the ViewModel
        viewModelJob.cancel()
    }
}