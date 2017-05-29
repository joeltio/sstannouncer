package com.sst.anouncements;


import android.support.test.runner.AndroidJUnit4;

import com.sst.anouncements.event.Event;
import com.sst.anouncements.event.EventController;
import com.sst.anouncements.event.EventHandler;
import com.sst.anouncements.resource.HTTPResourceAcquirer;
import com.sst.anouncements.resource.Resource;
import com.sst.anouncements.resource.ResourceAcquirer;
import com.sst.anouncements.service.ResourceService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        ResourceService.setResourceChangedEvent(testEvent);
        resourceService.setFrequency(0.5);
        testEventController.listen(this.toString(), testEvent.getIdentifier(), new EventHandler() {
            @Override
            public void handle(Event event) {
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
