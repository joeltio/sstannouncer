package sst.com.anouncements.feed.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import sst.com.anouncements.R
import sst.com.anouncements.feed.model.Entry


class FeedAdapter(private var entries: List<Entry>) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    class FeedViewHolder(feedView: ViewGroup) : RecyclerView.ViewHolder(feedView) {
        val titleTextView: TextView = feedView.findViewById(R.id.title_text_view)
        val excerptTextView: TextView = feedView.findViewById(R.id.excerpt_text_view)
        val dateTextView: TextView = feedView.findViewById(R.id.date_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val feedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_feed_item, parent, false) as ViewGroup

        return FeedViewHolder(feedView)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val entry: Entry = entries[position]

        holder.titleTextView.text = entry.title
        holder.excerptTextView.text = entry.contentWithoutHTML
        holder.dateTextView.text = entry.relativePublishedDate
    }

    override fun getItemCount() = entries.size

    fun setEntries(newEntries: List<Entry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}