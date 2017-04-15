package sstinc.sstannouncer;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import sstinc.sstannouncer.Feed.Entry;
import sstinc.sstannouncer.Feed.Feed;
import sstinc.sstannouncer.Feed.RSSParser;
import sstinc.sstannouncer.Feed.XML;

public class FeedFragment extends ListFragment {
    public FeedFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<Entry> entries;

        try {
            XML xml = new XML();
            xml.fetch(getString(R.string.blog_rss_url));
            Feed feed = RSSParser.parse(xml);
            entries = feed.getEntries();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage());
            entries = new ArrayList<>();
        }

        setListAdapter(new FeedArrayAdapter(getActivity(), entries));
    }
}
