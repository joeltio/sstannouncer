package com.sst.anouncements;


import android.support.test.runner.AndroidJUnit4;

import com.sst.anouncements.resource.HTTPResourceAcquirer;
import com.sst.anouncements.resource.Resource;
import com.sst.anouncements.resource.ResourceAcquirer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertTrue;

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
