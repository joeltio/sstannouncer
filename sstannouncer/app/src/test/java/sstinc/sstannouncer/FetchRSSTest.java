package sstinc.sstannouncer;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import static org.junit.Assert.*;



public class FetchRSSTest {
    @Test
    public void fetch_rss() {
        RSSFeed rssFeed = new RSSFeed();
        RSSFeed.fetchRSS();
        String rss = RSSFeed.getRawXML();
        assertFalse(rss.isEmpty());
    }

    @Test
    public void rss_isXML() throws XmlPullParserException {
        RSSFeed rssFeed = new RSSFeed();
        RSSFeed.fetchRSS();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(rssFeed.getRawXML));
    }
}
