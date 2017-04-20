package sstinc.sstannouncer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.ref.WeakReference;
import java.util.Date;

import sstinc.sstannouncer.event.AndroidEventAdaptor;
import sstinc.sstannouncer.event.Event;
import sstinc.sstannouncer.event.EventController;
import sstinc.sstannouncer.event.EventHandler;

import static org.junit.Assert.*;

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
        assertTrue(localEventAdaptor.connected());

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
            Thread.sleep(1); //Event Processing Must be done within 1ms
        }catch(InterruptedException exp){};

        assertTrue(eventStatus);

        localEventAdaptor.disconnect();
        remoteEventAdaptor.disconnect();
    }

}
