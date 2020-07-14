package sst.com.anouncements.feed.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sst.com.anouncements.R
import sst.com.anouncements.feed.model.Entry

class PostFragment : Fragment() {
    private lateinit var entry: Entry
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        entry = requireArguments().getParcelable("post")!!
        return layoutInflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Add back button

        // 2. Add contents from argument to view

        // 3. View in browser
    }
}