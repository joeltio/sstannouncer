package sstinc.sstannouncer;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import sstinc.sstannouncer.event.Event;
import sstinc.sstannouncer.event.EventController;
import sstinc.sstannouncer.event.EventHandler;
import sstinc.sstannouncer.resource.HTTPResourceAcquirer;
import sstinc.sstannouncer.resource.Resource;
import sstinc.sstannouncer.resource.ResourceAcquirer;
import sstinc.sstannouncer.service.ResourceService;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ServiceSystemTest {
    private static boolean eventStatus;
    @Test
    public void testResourceService()
    {
        Resource testResource =
                new Resource("http://studentsblog.sst.edu.sg/feeds/posts/default", new Date(0), "");
        ResourceAcquirer resourceAcquirer = new HTTPResourceAcquirer();
        ResourceService resourceService = new ResourceService(testResource, resourceAcquirer);

        resourceService.start();
        assertTrue(resourceService.isAlive());
        resourceService.stop();
        assertFalse(resourceService.isAlive());
    }

    @Test
    public void testResourceEvent()
    {
        Resource testResource =
                new Resource("http://studentsblog.sst.edu.sg/feeds/posts/default", new Date(0), "");
        ResourceAcquirer resourceAcquirer = new HTTPResourceAcquirer();
        ResourceService resourceService = new ResourceService(testResource, resourceAcquirer);
        Event testEvent = new Event("test.event", new Date(0), "");
        EventController testEventController = new EventController();
        eventStatus = false;

        resourceService.bind(testEventController);
        resourceService.setResourceChangedEvent(testEvent);
        resourceService.setFrequency(0.5);
        testEventController.listen(this.toString(), testEvent.getIdentifier(), new EventHandler() {
            @Override
            public void handle(Event event) {
                Log.d("testResourceEvent", "Resource changed event");
                eventStatus = true;
            }
        });

        resourceService.start();
        try
        {
            Thread.sleep(1200);
        }catch(InterruptedException exp) {}

        resourceService.stop();
        assertTrue(eventStatus);
    }
}
