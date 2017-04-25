package com.sst.anouncements.Feed;

//**** REMOVE ON NOTIFICATION REDESIGN

import com.sst.anouncements.android.AndroidNotificationAdaptor;
import com.sst.anouncements.filter.Filter;
import com.sst.anouncements.filter.FilterPredicate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Feed Notification Adaptor
 * Defines a adaptor between a Feed and Notifications
 */

public class FeedNotificationAdaptor {
    private AndroidNotificationAdaptor adaptor;
    private Feed feed;
    private String title;
    private Date lastModified;

    /**
     * Feed Notification Adaptor Constructor
     * Constructs a new feed notification adaptor for the given feed, title of the feed,
     * and the Operating System specific notification adaptor.
     *
     * @param feed The feed to deliver notifications from
     * @param title The title of the feed.
     * @param adaptor The notification adaptor to use to deploy notifications.
     */
    public FeedNotificationAdaptor(Feed feed, String title, AndroidNotificationAdaptor adaptor)
    {
        this.feed = feed;
        this.title = title;
        this.lastModified = feed.getLastChanged();
        this.adaptor = adaptor;
    }

    /**
     * Deploy a feed change notification.
     * Deploy a feed changed notification with the passed new feed.
     *
     * @param feed The new feed to change to, the feed must be newer then the current feed.
     */
    public void changeFeed(Feed feed)
    {
        if(this.lastModified.compareTo(feed.getLastChanged()) <= 0)
        {
            final Collection<Object> feedEntries = new ArrayList<Object>() ;
            feedEntries.addAll(feed.getEntries());
            final Date lastModified =  this.lastModified;
            DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            Filter changeFilter = new Filter(new FilterPredicate() {
                @Override
                public boolean filter(Object object) {
                    if(object.getClass() == Entry.class)
                    {
                        Entry feedEntry = (Entry)object;
                        if(lastModified.compareTo(feedEntry.getLastUpdated()) < 0)
                        {
                            return false; //Don't Filter
                        }
                    }

                    return true; //Filter
                }
            }, feedEntries);

            this.lastModified = feed.getLastChanged();

            Collection<Object> filteredObject = changeFilter.filter();
            ArrayList<Entry> changedEntries = new ArrayList<>();
            for(Object object : filteredObject)
            {
                if(object.getClass() == Entry.class)
                {
                    changedEntries.add((Entry) object);
                }
            }

            for(Entry entry : changedEntries)
            {
                String notificationTitle = "New Entry on " + this.title;
                this.adaptor.build((int)System.currentTimeMillis(),
                        notificationTitle, entry.getTitle(), entry);

                this.adaptor.display();
            }

        }
    }


}
