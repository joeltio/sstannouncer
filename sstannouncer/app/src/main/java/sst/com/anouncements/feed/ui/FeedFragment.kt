package sst.com.anouncements.feed.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.android.ext.android.inject
import sst.com.anouncements.R
import sst.com.anouncements.feed.model.Feed
import sst.com.anouncements.feed.data.FeedRepository

class FeedFragment : Fragment() {
    private val feedRepository: FeedRepository by inject()
    private lateinit var feedViewModel: FeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        feedViewModel = ViewModelProviders.of(this,
            FeedViewModelFactory(feedRepository, this, arguments)
        )
            .get(FeedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Prepare for feed RecyclerView
        val viewManager = LinearLayoutManager(context)
        // Start with no elements, update later when the data is ready
        val viewAdapter = FeedAdapter(listOf())

        // Set an observer on the Feed LiveData for when the data is ready
        feedViewModel.feedLiveData.observe(this, Observer<Feed> {
            viewAdapter.setEntries(it.entries)
        })

        feed_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // Add dividers to RecyclerView
        val dividerItemDecoration = DividerItemDecoration(
            feed_recycler_view.context, viewManager.orientation)
        feed_recycler_view.addItemDecoration(dividerItemDecoration)
    }
}