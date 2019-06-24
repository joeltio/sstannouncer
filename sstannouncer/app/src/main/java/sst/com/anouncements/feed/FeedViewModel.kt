package sst.com.anouncements.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.lang.IllegalArgumentException

class FeedViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val feedURL : String = savedStateHandle["feedUrl"] ?:
        throw IllegalArgumentException("missing feed URL")
    val feed : LiveData<Feed> = TODO()
}