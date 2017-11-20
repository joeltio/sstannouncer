package com.sst.anouncements.Feed;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Feed {
    private static String TAG = "Feed";

    private final Date lastChanged;
    private final ArrayList<String> categories;
    private final ArrayList<Entry> entries;

    private boolean entriesSorted;

    public Feed(Date lastChanged, ArrayList<String> categories, ArrayList<Entry> entries) {
        this.lastChanged = lastChanged;
        this.categories = categories;
        this.entries = entries;
        this.entriesSorted = false;
    }

    //Create Feed from Parsing RSS
    //Returns Null if Parsing Fails
    public static Feed parse(String rss) {
        try {
            XML xml = new XML(rss);
            Feed feed = RSSParser.parse(xml);

            return feed;
        } catch (Exception e) {
            Log.e(Feed.TAG, e.getLocalizedMessage());
        }

        return null;
    }

    //Feed Equal Method
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj.getClass() == Feed.class)
        {
            Feed otherFeed = (Feed)obj;
            if(this.compareTo(otherFeed) != 0) return false;

            //Check Categories
            for(String category : this.categories)
            {
                if(!otherFeed.categories.contains(category)) return false;
            }

            //Check Entries
            boolean found = false;
            for(Entry entry : this.entries)
            {
                found = false;
                for(Entry otherEntry : otherFeed.entries)
                {
                    if(entry.equals(otherEntry))
                    {
                        found = true;
                        break;
                    }
                }

                if(!found) return false;
            }

            return true;
        }
        return  false;
    }

    // Compares Feeds with each other
    // Precision: Down to the minute.
    // Return: 0 - Equal, 1 - Greater, -1 - Less Than
    public int compareTo(Feed feed)
    {
        return this.lastChanged.compareTo(feed.lastChanged);
    }

    public ArrayList<Entry> diffEntry(Feed otherFeed)
    {
        ArrayList<Entry> diff = new ArrayList<>();
        if(this.compareTo(otherFeed) != 0)
        {
            this.sortEntries();
            otherFeed.sortEntries();

            boolean found = false;
            for(Entry entry: this.entries)
            {
                found = true;
                for(Entry otherEntry: otherFeed.entries)
                {
                    int result = entry.compareTo(otherEntry);
                    if(result == -1)
                    {
                        found = false;
                        break;
                    }
                }

                if(found) diff.add(entry);
            }
        }
        return diff;
    }

    private void sortEntries()
    {
        if(!this.entriesSorted)
        {
            //Sorted Based on Last Updated, Earliest First
            Collections.sort(this.entries, new Comparator<Entry>() {
                @Override
                public int compare(Entry lhs, Entry rhs) {
                    return lhs.compareTo(rhs);
                }
            });
            this.entriesSorted = true;
        }
    }

    public ArrayList<String> getCategories() {
        return this.categories;
    }

    public ArrayList<Entry> getEntries() {
        return this.entries;
    }

    public Date getLastChanged() {
        return lastChanged;
    }
}
