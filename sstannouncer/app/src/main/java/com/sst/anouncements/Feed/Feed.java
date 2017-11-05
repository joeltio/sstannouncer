package com.sst.anouncements.Feed;

import android.util.Log;
import android.util.Xml;

import java.util.ArrayList;
import java.util.Date;

public class Feed {
    private static String TAG = "Feed";

    private final Date lastChanged;
    private final ArrayList<String> categories;
    private final ArrayList<Entry> entries;

    public Feed(Date lastChanged, ArrayList<String> categories, ArrayList<Entry> entries) {
        this.lastChanged = lastChanged;
        this.categories = categories;
        this.entries = entries;
    }

    //Create Feed from Parsing RSS
    //Returns Null if Parsing Fails
    public static Feed parse(String rss) {
        try {
            XML xml = new XML(rss);
            Feed feed = RSSParser.parse(xml);

            return Feed
        } catch (Exception e) {
            Log.e(Feed.TAG, e.getLocalizedMessage());
        }

        return null;
    }

    //Feed Equal Method
    @Override
    public boolean equals(Object obj) {
        if(obj != this && obj.getClass() == Feed.class)
        {
            Feed otherFeed = (Feed)obj;
            if(!this.lastChanged.equals(otherFeed.lastChanged)) return false;
            for(int i = 0; i < this.categories.size(); i ++)
            {
                if(!this.categories.get(i).equals(otherFeed.categories.get(i))) return false;
            }
            for(int i = 0; i < this.entries.size(); i ++)
            {
                if(!this.entries.get(i).equals(otherFeed.entries.get(i))) return false;
            }

            return true;
        }
        return  false;
    }

    // Compares Feeds with each other
    // Return: 0 - Equal, 1 - Greater, -1 - Less Than
    public int compareTo(Feed feed)
    {
        if(this.equals(feed)) return 0;

        if(this.lastChanged.after(feed.lastChanged)) return 1;
        return -1;
    }

    public ArrayList<String> getCategories() {
        return this.categories;
    }

    public ArrayList<Entry> getEntries() {
        return this.entries;
    }
}
