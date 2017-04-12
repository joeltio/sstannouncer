package sstinc.sstannouncer;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class XMLTest {
    @Test
    public void XML_fetch_not_empty() {
        XML xml = new XML();
        assertTrue(xml.getRawXML().isEmpty());
        xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
        assertFalse(xml.getRawXML().isEmpty());
    }

    @Test
    public void XML_fetch_correct_XML() {
        XML xml = new XML();
        xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
        try {
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
        xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");

        String id = (String) xml.xpath("/feed/id/text()");
        assertEquals(id, "tag:blogger.com,1999:blog-226334574845869952");
    }

    @Test
    public void XML_xpath_returns_sub_XML_object() {
        XML xml = new XML();
        xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");

        XML feed = (XML) xml.xpath("/feed/");
        String id = (String) feed.xpath("/id/text()");
        assertEquals(id, "tag:blogger.com,1999:blog-226334574845869952");
    }
}
