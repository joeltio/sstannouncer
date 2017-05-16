package com.sst.anouncements.event;


import com.sst.anouncements.EntryActivity;
import com.sst.anouncements.Feed.Entry;
import com.sst.anouncements.Feed.Feed;
import com.sst.anouncements.Feed.RSSParser;
import com.sst.anouncements.Feed.XML;
import com.sst.anouncements.android.AndroidNotificationAdaptor;
import com.sst.anouncements.filter.Filter;
import com.sst.anouncements.filter.FilterPredicate;
import com.sst.anouncements.resource.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Feed Event Interpreter.
 *
 * Interprets the types of Feeds, triggering the necessary actions to handle the Feed event.
 */
public class FeedEventInterpreter implements EventHandler {
    public static String FEED_TYPE_STUDENT_BLOG = "feed.type.student_blog";
    public static String FEED_TYPE_UNKNOWN = "feed.type.unknown";

    private static Event feedChangedEvent =
            new Event("feed.changed", new Date(0), "");

    private EventController eventController;
    private AndroidNotificationAdaptor notificationAdaptor;

    /**
     * Feed Event Interpreter Constructor.
     * Creates a new Feed Event Interpreter that interpret and handle resource events.
     * Binds to the given Event Controller <code>eventController</code>, listening for resource
     * events on the event controller.
     * Notifies the user using the <code>notificationAdaptor</code> passed.
     * @param eventController The event controller to bind to
     * @param notificationAdaptor The notification to use to notify the user.
     */
    public FeedEventInterpreter(EventController eventController,
                                AndroidNotificationAdaptor notificationAdaptor)
    {
        this.bind(eventController);
        this.notificationAdaptor = notificationAdaptor;
    }

    /**
     * Bind to a Event Controller.
     * Bind to the passed Event Controller, listening for resource events raised on that event
     * controller.
     * If currently bound to a event controller, the method unbind as defined by
     * <code>unBind()</code> from the current event controller and bind to the passed event
     * Controller.
     * @param eventController The event controler to bind to.
     */
    public void bind(EventController eventController)
    {
        if(this.eventController != null) this.unBind();
        this.eventController = eventController;
        this.eventController.
                listen(this.toString(), this.feedChangedEvent.getIdentifier(), this);
    }

    /**
     * Unbind from the Event Controller.
     * Unbind from the currently bound event controller.
     * If not currently bound to a event controller, nothing would be done.
     */
    public void unBind()
    {
        if(this.eventController != null)
        {
            this.eventController.unlisten(this.toString(),
                    this.feedChangedEvent.getIdentifier());
            this.eventController = null;
        }
    }


    /**
     * Get Feed Changed Event
     * Raise this event to trigger actions for Feed Changes
     * The event data field would the changed resource that is encoded in a string format defined by
     * <code>Resource.toString()</code>.
     * The events <code>timeStamp</code> should be the previous time stamp of the feed.
     * By default the event identifier is defined as "feed.changed "
     *
     * @see Resource#toString()
     */
    public static Event getFeedChangedEvent() {
        return feedChangedEvent;
    }

    @Override
    public void handle(Event event)
    {
        if(event.getIdentifier().equals(this.getFeedChangedEvent().getIdentifier()))
        {
            this.handleFeedChanged(event);
        }
        else
        {

        }
    }

    //Private Utility Methods
    private String interpret(Feed feed)
    {
        //TODO: Actually Interpret Feed
        return FeedEventInterpreter.FEED_TYPE_STUDENT_BLOG;
    }


    private void handleFeedChanged(Event event)
    {
        //TODO: Send Feed Objects Instead
        Resource resource = new Resource(event.toString());
        final Date previousTimeStamp = event.getTimeStamp();

        final Feed feed;
        try {
            XML resourceXML = new XML(resource.getData());
            feed = RSSParser.parse(resourceXML);
        }catch(Exception exp){
            //Failed to Parse Feed
            return;
        };


        //Filter Entries Entries
        Collection<Object> filterFeed = new ArrayList<Object>();
        filterFeed.addAll(feed.getEntries());

        Filter diffFeed = new Filter(new FilterPredicate() {
            @Override
            public boolean filter(Object object) {
                Entry entry = (Entry) object;

                if(entry.getLastUpdated().after(previousTimeStamp))
                {
                    return false; //Keep
                }
                else
                {
                    return true; //Filter
                }
            }
        }, filterFeed);

        filterFeed = diffFeed.filter();
        ArrayList<Entry> changedEntries =  new ArrayList<Entry>();
        for(Object object : filterFeed)
        {
            changedEntries.add((Entry) object);
        }

        //Notify User Of Changed Entries
        if(this.interpret(feed).equals(FeedEventInterpreter.FEED_TYPE_STUDENT_BLOG))
        {
            this.notifyChangedStudentBlog(changedEntries);
        }
        else
        {

        }
    }

    private void notifyChangedStudentBlog(ArrayList<Entry> changedEntries)
    {
        for(Entry entry : changedEntries)
        {
            //@TODO: Remove hardcoded notification title

            String title = "New Post on Student's Blog";
            String content = entry.getTitle();
            int notificationID = (int)entry.getLastUpdated().getTime();

            this.notificationAdaptor.setNotificationAutoCancel(true);
            this.notificationAdaptor.create(title, content, EntryActivity.ENTRY_EXTRA, entry);
            this.notificationAdaptor.display(notificationID);
        }
    }
}
