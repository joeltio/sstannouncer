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
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
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

    private SharedPreferences preferences;

    public static EventController eventController = null;
    private AndroidEventAdaptor androidEventAdaptor;

    private fetchNewFeed fetchFeedAsync;
    private DrawerLayout drawerLayout;
    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {}
        @Override
        public void onDrawerOpened(View drawerView) {}
        @Override
        public void onDrawerStateChanged(int newState) {}

        @Override
        public void onDrawerClosed(View drawerView) {
            fetchFeedAsync.execute();
            drawerLayout.removeDrawerListener(this);
        }
    };

    private long getLastModified(long defaultValue) {
        return this.preferences.getLong(LAST_MODIFIED_PREFERENCE, defaultValue);
    }

    private long getNewestEntryDate(long defaultValue) {
        return this.preferences.getLong(NEWEST_ENTRY_DATE_PREFERENCE, defaultValue);
    }

    private void setLastModified(long newValue) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putLong(LAST_MODIFIED_PREFERENCE, newValue);
        editor.apply();
    }

    private void setNewestEntryDate(long newValue) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putLong(NEWEST_ENTRY_DATE_PREFERENCE, newValue);
        editor.apply();
    }


    private void updateEntries(ArrayList<Entry> entries) {
        DbAdapter dbAdapter = new DbAdapter(getActivity());
        dbAdapter.open();

        Date oldestEntryPublished = entries.get(entries.size()-1).getPublished();
        dbAdapter.deleteEntries(oldestEntryPublished);

        long newestEntryDateMillis = getNewestEntryDate(-1);
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

        newestEntryDateMillis = entries.get(0).getPublished().getTime();
        setNewestEntryDate(newestEntryDateMillis);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
                    ArrayList<Entry> entries;
                    try {
                        XML xml = new XML(resource.getData());
                        Feed feed = RSSParser.parse(xml);

                        entries = feed.getEntries();

                        updateEntries(entries);

                        setLastModified(resource.getTimeStamp().getTime());
                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), e.getMessage());
                        entries = new ArrayList<>();
                    }

                    if (!entries.isEmpty()) {
                        setListAdapter(new FeedArrayAdapter(getActivity(), entries));
                    }
                }
            });

            fetchFeedAsync = new fetchNewFeed();

            this.drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                this.drawerLayout.addDrawerListener(this.drawerListener);
            } else {
                this.fetchFeedAsync.execute();
            }
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

        this.drawerLayout.removeDrawerListener(this.drawerListener);
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
                Log.e(this.getClass().getName(), e.getMessage());
            }
            return lastModified;
        }

        @Override
        protected ArrayList<Entry> doInBackground(Void... voids) {
            ArrayList<Entry> entries;
            long localLastModified = getLastModified(0);
            long onlineLastModified = fetchLastModified();
            boolean feedHasBeenModified = localLastModified != onlineLastModified;

            if (this.force || feedHasBeenModified) {
                try {
                    XML xml = new XML();
                    xml.fetch(getString(R.string.blog_rss_url));
                    Feed feed = RSSParser.parse(xml);
                    entries = feed.getEntries();

                    updateEntries(entries);

                    setLastModified(onlineLastModified);
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
