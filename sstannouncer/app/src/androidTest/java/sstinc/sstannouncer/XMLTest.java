package sstinc.sstannouncer;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import sstinc.sstannouncer.Feed.XML;

import static org.junit.Assert.*;

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
        } catch (IOException e) {
            e.printStackTrace();
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

            assertEquals(fetchedXML, xml.getRawXML());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void XML_xpath_returns_correct_id() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String id = (String) xml.xpath("/feed/id/text()");
        assertEquals(id, "tag:blogger.com,1999:blog-226334574845869952");
    }

    @Test
    public void XML_xpath_returns_sub_XML_object() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
        } catch (IOException e) {
            e.printStackTrace();
        }
        XML feed = (XML) xml.xpath("/feed/");
        String id = (String) feed.xpath("/id/text()");
        assertEquals(id, "tag:blogger.com,1999:blog-226334574845869952");
    }
}
