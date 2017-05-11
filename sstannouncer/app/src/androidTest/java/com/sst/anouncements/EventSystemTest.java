package com.sst.anouncements;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.sst.anouncements.android.AndroidEventAdaptor;
import com.sst.anouncements.android.AndroidNotificationAdaptor;
import com.sst.anouncements.event.Event;
import com.sst.anouncements.event.EventController;
import com.sst.anouncements.event.EventHandler;
import com.sst.anouncements.event.FeedEventInterpreter;
import com.sst.anouncements.event.ResourceEventInterpreter;
import com.sst.anouncements.resource.HTTPResourceAcquirer;
import com.sst.anouncements.resource.Resource;
import com.sst.anouncements.service.ResourceService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventSystemTest
{
    private static boolean eventStatus;

    @Test
    public void testEventController()
    {

        EventController eventController = new EventController();
        Event testEvent = new Event("test.event", new Date(0), "Hello");
        eventStatus = false;

        //Test Listen
        eventController.listen(this.toString(), testEvent.getIdentifier(), new EventHandler() {
            @Override
            public void handle(Event event) {
                //Test Passing of Evnet
                assertEquals(event.getIdentifier(), "test.event");
                assertEquals(event.getTimeStamp(), new Date(0));
                assertEquals(event.getData(), "Hello");
                eventStatus = true;
            }
        });
        //Test Raise
        eventController.raise(testEvent);
        assertTrue(eventStatus);

        //Test Unlisten
        eventController.unlisten(this.toString(), "test.event");
        eventStatus = false;
        eventController.raise(testEvent);
        assertFalse(eventStatus);

        //Test Wildcard
        eventStatus = false;
        eventController.listen(this.toString(), "*", new EventHandler() {
            @Override
            public void handle(Event event) {
                eventStatus = true;
            }
        });
        eventController.raise(testEvent);
        assertTrue(eventStatus);
    }

    @Test
    public void testAndroidEventAdaptor()
    {
        EventController localEventController = new EventController();
        EventController remoteEventController = new EventController();
        AndroidEventAdaptor localEventAdaptor = new AndroidEventAdaptor(localEventController);
        AndroidEventAdaptor remoteEventAdaptor = new AndroidEventAdaptor(remoteEventController);
        Event testEvent = new Event("test.event", new Date(0), "Hello");
        eventStatus = false;

        //Connection Adaptors
        localEventAdaptor.connect(remoteEventAdaptor.getLocalMessenger());

        //Transmission of Event
        localEventController.listen(this.toString(), "test.event", new EventHandler() {
            @Override
            public void handle(Event event) {
                //Test Passing of Event
                assertEquals(event.getIdentifier(), "test.event");
                assertEquals(event.getTimeStamp(), new Date(0));
                assertEquals(event.getData(), "Hello");
                eventStatus = true;
            }
        });

        remoteEventController.raise(testEvent);

        try
        {
            Thread.sleep(10); //Event Processing Must be done within 10ms
        }catch(InterruptedException exp){};

        assertTrue(eventStatus);

        localEventAdaptor.disconnect();
        remoteEventAdaptor.disconnect();
    }

    @Test
    public void testResourceEventInterpreter()
    {
        EventController eventController = new EventController();
        ResourceEventInterpreter resourceEventInterpreter =
                new ResourceEventInterpreter(eventController, null);

        resourceEventInterpreter.unBind();
        resourceEventInterpreter.bind(eventController);

        final Resource resource = new
                Resource("http://test-sst.blogspot.sg/feeds/posts/default", new Date(0), "");
        HTTPResourceAcquirer resourceAcquirer = new HTTPResourceAcquirer();
        resourceAcquirer.retrieve(resource);

        eventStatus = false;
        eventController.listen(this.toString(),
                        FeedEventInterpreter.getFeedChangedEvent().getIdentifier(),
                        new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                assertEquals(event.getIdentifier(),
                                        FeedEventInterpreter.getFeedChangedEvent().getIdentifier());
                                assertEquals(new Resource(event.getData()), resource);
                                eventStatus = true;

                            }
                        });

        Event resourceChangedEvent = new
                Event(ResourceService.getResourceChangedEvent().getIdentifier(),
                new Date(),
                resource.toString());
        eventController.raise(resourceChangedEvent);

        try
        {
            Thread.sleep(10); //Event Processing must be done in 10ms
        }catch(InterruptedException exp){};

        assertTrue(eventStatus);

        resourceEventInterpreter.unBind();
    }

    @Test
    public void testFeedEventInterpreter()
    {
        EventController eventController = new EventController();
        Context context = InstrumentationRegistry.getTargetContext();
        AndroidNotificationAdaptor notificationAdaptor =
                new AndroidNotificationAdaptor(context, R.drawable.notifcation_icon);

        final Resource resource = new
                Resource("http://test-sst.blogspot.sg/feeds/posts/default", new Date(0), "");
        HTTPResourceAcquirer resourceAcquirer = new HTTPResourceAcquirer();
        resourceAcquirer.retrieve(resource);

        FeedEventInterpreter feedEventInterpreter = new FeedEventInterpreter(eventController,
                notificationAdaptor);

        feedEventInterpreter.unBind();
        feedEventInterpreter.bind(eventController);

        Event feedChangedEvent =
                new Event(FeedEventInterpreter.getFeedChangedEvent().getIdentifier(),
                        new Date(0),
                        resource.toString());


        eventController.raise(feedChangedEvent);

        try
        {
            Log.d("TFeedEventInterpreter", "Check for notification in notification drawer.");
            Thread.sleep(1000 * 5); //Delay to show the lack in notification.
        }catch(InterruptedException exp){};
    }
}
