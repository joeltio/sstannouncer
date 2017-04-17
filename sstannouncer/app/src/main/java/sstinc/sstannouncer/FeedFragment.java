package sstinc.sstannouncer;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;

import sstinc.sstannouncer.Feed.Entry;
import sstinc.sstannouncer.Feed.Feed;
import sstinc.sstannouncer.Feed.RSSParser;
import sstinc.sstannouncer.Feed.XML;

public class FeedFragment extends ListFragment implements AdapterView.OnItemClickListener {
    public FeedFragment() {}

    private class fetchFeed extends AsyncTask<Void, Void, ArrayList<Entry>> {
        @Override
        protected ArrayList<Entry> doInBackground(Void... voids) {
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

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            super.onPostExecute(entries);
            setListAdapter(new FeedArrayAdapter(getActivity(), entries));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(this);

        new fetchFeed().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Entry entry = (Entry) getListAdapter().getItem(i);

        Intent intent = new Intent(getActivity(), EntryActivity.class);
        intent.putExtra(EntryActivity.ENTRY_EXTRA, entry);
        startActivity(intent);
    }
}
