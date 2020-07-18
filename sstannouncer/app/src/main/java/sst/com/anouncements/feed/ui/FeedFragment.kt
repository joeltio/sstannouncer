package sst.com.anouncements.feed.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import sst.com.anouncements.R
import sst.com.anouncements.feed.model.Feed

class FeedFragment : Fragment() {
    private val feedViewModel: FeedViewModel by stateViewModel(bundle = { requireArguments() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Prepare for feed RecyclerView
        val viewManager = LinearLayoutManager(context)
        // Start with no elements, update later when the data is ready
        val viewAdapter = FeedAdapter(listOf()) {
            val postArguments: Bundle = Bundle()
            postArguments.putParcelable("post", it)
            findNavController().navigate(R.id.action_feedFragment_to_postFragment, postArguments)
        }

        feed_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // Add dividers to RecyclerView
        val dividerItemDecoration = DividerItemDecoration(
            feed_recycler_view.context, viewManager.orientation)
        feed_recycler_view.addItemDecoration(dividerItemDecoration)


        // Create view model observers
        feedViewModel.feedLiveData.observe(viewLifecycleOwner, Observer { feed ->
            // Set adapter entries when the feed live data is updated
            val sortedEntries = feed.entries.sortedByDescending { entry ->
                entry.publishedDate.time
            }
            viewAdapter.setEntries(sortedEntries)
        })

        feedViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            // Update swipe refresh layout status
            feed_swipe_refresh_layout.isRefreshing = it
        })


        // Setup swipe refresh layout
        feed_swipe_refresh_layout.setOnRefreshListener {
            feedViewModel.refresh()
        }
    }
}