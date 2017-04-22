package sstinc.sstannouncer;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import sstinc.sstannouncer.resource.HTTPResourceAcquirer;
import sstinc.sstannouncer.resource.Resource;
import sstinc.sstannouncer.resource.ResourceAcquirer;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ResourceSystemTest
{
    @Test
    public void testResource()
    {
        Resource testResource = new Resource("test.resource", new Date(0), "Test Data");
        Resource copyResource = new Resource(testResource.toString());
        assertTrue(testResource.equals(copyResource));
    }


    @Test
    public void testHTTPResource()
    {
        Resource testResource = new Resource("http://studentsblog.sst.edu.sg/feeds/posts/default",
                new Date(0), "");
        ResourceAcquirer resourceAcquirer = new HTTPResourceAcquirer();
        resourceAcquirer.retrieve(testResource);
        assertTrue(testResource.getData().length() > 0);
    }
}
