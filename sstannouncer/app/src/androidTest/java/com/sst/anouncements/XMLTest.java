package com.sst.anouncements;

import android.support.test.runner.AndroidJUnit4;

import com.sst.anouncements.Feed.XML;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class XMLTest {
    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Test
    public void XML_fetch_not_empty() {
        XML xml = new XML();
        assertTrue(xml.getRawXML().isEmpty());
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertFalse(xml.getRawXML().isEmpty());
    }

    @Test
    public void XML_fetch_correct_XML() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
            URL url = new URL("http://studentsblog.sst.edu.sg/feeds/posts/default");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();
            String fetchedXML = convertStreamToString(urlConnection.getInputStream());

            assertEquals(xml.getRawXML(), fetchedXML);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void XML_xpath_returns_correct_id() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
            String id = xml.xpathString("/feed/id/text()");
            assertEquals("tag:blogger.com,1999:blog-2263345748458699524", id);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void XML_xpath_returns_sub_XML_object() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
            XML feed = xml.xpathNode("/feed/id");
            String id = feed.xpathString("/id/text()");
            assertEquals("tag:blogger.com,1999:blog-2263345748458699524", id);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void XML_xpath_returns_multiple_XML_objects() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
            ArrayList<XML> entries = xml.xpathMultipleNodes("/feed/entry");
            assertEquals(25, entries.size());

            for (XML entry : entries) {
                String publishedDate = entry.xpathString("/entry/published/text()");
                assertEquals(29, publishedDate.length());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
