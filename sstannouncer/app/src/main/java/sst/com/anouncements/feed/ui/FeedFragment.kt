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
import androidx.recyclerview.widget.RecyclerView
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

        // Set an observer on the Feed LiveData for when the data is ready
        feedViewModel.feedLiveData.observe(viewLifecycleOwner, Observer<Feed> {
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