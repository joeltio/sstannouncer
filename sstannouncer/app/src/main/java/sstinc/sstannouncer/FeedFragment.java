package sstinc.sstannouncer;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import sstinc.sstannouncer.Feed.Entry;
import sstinc.sstannouncer.Feed.Feed;
import sstinc.sstannouncer.Feed.RSSParser;
import sstinc.sstannouncer.Feed.XML;

public class FeedFragment extends ListFragment implements AdapterView.OnItemClickListener {
    public FeedFragment() {}

    private static final String FEED_FRAGMENT_PREFERENCE = "feed_fragment_preference";
    private static final String LAST_MODIFIED_PREFERENCE = "last_modified";

    private ArrayList<Entry> mEntries;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(this);
        if (savedInstanceState == null) {
            new fetchNewFeed().execute(true);
        }
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);

        mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(getActivity());
        mSwipeRefreshLayout.addView(listFragmentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mSwipeRefreshLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.progress_1),
                ContextCompat.getColor(getActivity(), R.color.progress_2),
                ContextCompat.getColor(getActivity(), R.color.progress_3)
        );

        mSwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new fetchNewFeed().execute();
                }
            }
        );

        if (savedInstanceState != null) {
            ArrayList<Entry> entries = savedInstanceState.getParcelableArrayList("entries");
            setListAdapter(new FeedArrayAdapter(getActivity(), entries));
            mEntries = entries;
        }
        return mSwipeRefreshLayout;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Entry entry = (Entry) getListAdapter().getItem(i);

        Intent intent = new Intent(getActivity(), EntryActivity.class);
        intent.putExtra(EntryActivity.ENTRY_EXTRA, entry);
        startActivity(intent);
    }

    // Fetches and updates the feed only if it has been modified
    private class fetchNewFeed extends AsyncTask<Boolean, Void, ArrayList<Entry>> {
        private boolean feedHasBeenModified() {
            boolean feedHasBeenModified = true;

            SharedPreferences preferences = getActivity().getSharedPreferences(
                    FEED_FRAGMENT_PREFERENCE, Context.MODE_PRIVATE);
            String lastModified = preferences.getString(LAST_MODIFIED_PREFERENCE, "");

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(getResources().getString(R.string.blog_rss_url));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("HEAD");

                feedHasBeenModified = lastModified.equals(urlConnection.getHeaderField("Last-Modified"));

                urlConnection.getInputStream().close();
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return feedHasBeenModified;
        }

        @Override
        protected ArrayList<Entry> doInBackground(Boolean... force_bools) {
            // true if first element of force_bools is true else false
            boolean force = force_bools.length > 0 && force_bools[0];

            ArrayList<Entry> entries;
            if (force || feedHasBeenModified()) {
                try {
                    XML xml = new XML();
                    xml.fetch(getString(R.string.blog_rss_url));
                    Feed feed = RSSParser.parse(xml);
                    entries = feed.getEntries();
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.getMessage());
                    entries = new ArrayList<>();
                }
            } else {
                entries = new ArrayList<>();
            }

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            super.onPostExecute(entries);
            if (!entries.isEmpty()) {
                setListAdapter(new FeedArrayAdapter(getActivity(), entries));
                mEntries = entries;
            }
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("entries", mEntries);
    }

    private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {
        public ListFragmentSwipeRefreshLayout(Context context) { super(context); }

        @Override
        public boolean canChildScrollUp() {
            final ListView listView = getListView();
            return listView.getVisibility() == View.VISIBLE &&
                    ViewCompat.canScrollVertically(listView, -1);
        }

    }
}
