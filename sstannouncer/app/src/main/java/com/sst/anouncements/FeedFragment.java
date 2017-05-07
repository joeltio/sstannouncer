package com.sst.anouncements;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.sst.anouncements.Feed.Entry;
import com.sst.anouncements.Feed.Feed;
import com.sst.anouncements.Feed.RSSParser;
import com.sst.anouncements.Feed.XML;
import com.sst.anouncements.android.AndroidEventAdaptor;
import com.sst.anouncements.android.AndroidServiceAdaptor;
import com.sst.anouncements.event.Event;
import com.sst.anouncements.event.EventController;
import com.sst.anouncements.event.EventHandler;
import com.sst.anouncements.resource.Resource;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class FeedFragment extends ListFragment implements AdapterView.OnItemClickListener {
    public FeedFragment() {}

    private static final String LAST_MODIFIED_PREFERENCE = "last_modified";
    private static final String NEWEST_ENTRY_DATE_PREFERENCE = "newest_entry";
    private fetchNewFeed fetchFeedAsync;
    public static EventController eventController = null;
    private AndroidEventAdaptor androidEventAdaptor;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        getListView().setOnItemClickListener(this);
        if (savedInstanceState == null) {
            if (FeedFragment.eventController == null) {
                FeedFragment.eventController = new EventController();
            }
            this.androidEventAdaptor = new AndroidEventAdaptor(FeedFragment.eventController);

            // Connect to service
            Intent connectIntent = new Intent(getActivity(), AndroidServiceAdaptor.class);
            connectIntent.putExtra(AndroidServiceAdaptor.INTENT_EXTRA_REMOTE_MESSENGER,
                    this.androidEventAdaptor.getLocalMessenger());
            getActivity().startService(connectIntent);

            eventController.listen(this.toString(),
                    getResources().getString(R.string.event_resource_changed_blog),
                    new EventHandler() {
                @Override
                public void handle(Event event) {
                    Resource resource = new Resource(event.getData());
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    try {
                        XML xml = new XML(resource.getData());
                        Feed feed = RSSParser.parse(xml);

                        entries = feed.getEntries();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                                getActivity());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putLong(LAST_MODIFIED_PREFERENCE, resource.getTimeStamp().getTime());
                        editor.apply();
                    } catch (Exception e) {
                    }

                    if (!entries.isEmpty()) {
                        setListAdapter(new FeedArrayAdapter(getActivity(), entries));
                    }
                }
            });
            this.fetchFeedAsync = new fetchNewFeed();
            this.fetchFeedAsync.execute();
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
                    fetchFeedAsync = new fetchNewFeed();
                    fetchFeedAsync.execute();
                }
            }
        );

        return mSwipeRefreshLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (fetchFeedAsync != null) {
            fetchFeedAsync.cancel(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Entry entry = (Entry) getListAdapter().getItem(i);

        Intent intent = new Intent(getActivity(), EntryActivity.class);
        intent.putExtra(EntryActivity.ENTRY_EXTRA, entry);
        startActivity(intent);
    }

    // Fetches and updates the feed only if it has been modified
    private class fetchNewFeed extends AsyncTask<Void, Void, ArrayList<Entry>> {
        boolean force = false;
        private fetchNewFeed() {}
        private fetchNewFeed(boolean force) {
            this.force = force;
        }

        private long fetchLastModified() {
            long lastModified = -1;
            try {
                URL url = new URL(getResources().getString(R.string.blog_rss_url));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("HEAD");
                urlConnection.connect();

                lastModified =  urlConnection.getLastModified();
                urlConnection.disconnect();
            } catch (Exception e) {
            }
            return lastModified;
        }

        @Override
        protected ArrayList<Entry> doInBackground(Void... voids) {
            ArrayList<Entry> entries;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                    getActivity());
            long localLastModified = preferences.getLong(LAST_MODIFIED_PREFERENCE, 0);
            long onlineLastModified = fetchLastModified();
            boolean feedHasBeenModified = localLastModified != onlineLastModified;

            if (this.force || feedHasBeenModified) {
                try {
                    XML xml = new XML();
                    xml.fetch(getString(R.string.blog_rss_url));
                    Feed feed = RSSParser.parse(xml);
                    entries = feed.getEntries();

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(LAST_MODIFIED_PREFERENCE, onlineLastModified);
                    editor.apply();
                } catch (Exception e) {
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
            } else {
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();

                ArrayList<Entry> databaseEntries = dbAdapter.getAllEntries();
                Collections.reverse(databaseEntries);

                dbAdapter.close();

                setListAdapter(new FeedArrayAdapter(getActivity(), databaseEntries));
            }

            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            if (!entries.isEmpty()) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                        getActivity());
                long newestEntryDateMillis = sharedPreferences.getLong(NEWEST_ENTRY_DATE_PREFERENCE, -1);

                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();

                Date oldestEntryPublished = entries.get(entries.size()-1).getPublished();
                dbAdapter.deleteEntries(oldestEntryPublished);

                if (newestEntryDateMillis != -1) {
                    Date newestEntryDate = new Date(newestEntryDateMillis);
                    for (int i=entries.size()-1; i>=0; i--) {
                        Entry entry = entries.get(i);

                        if (entry.getPublished().after(newestEntryDate)) {
                            dbAdapter.insertEntry(entry);
                        }
                    }
                } else {
                    dbAdapter.deleteAll();
                    for (Entry entry : entries) {
                        dbAdapter.insertEntry(entry);
                    }
                }

                dbAdapter.close();
            }
        }
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
