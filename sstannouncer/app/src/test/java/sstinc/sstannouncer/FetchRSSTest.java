package sstinc.sstannouncer;

import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;


public class FetchRSSTest {
    @Test
    public void fetch_rss() {
        RSSFeed rssFeed = new RSSFeed();
        rssFeed.fetchRSS();
        String rss = rssFeed.getRawXML();
        assertFalse(rss.isEmpty());
    }

    @Test
    public void rss_isXML() throws XmlPullParserException {
        RSSFeed rssFeed = new RSSFeed();
        rssFeed.fetchRSS();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(rssFeed.getRawXML()));
    }

    @Test
    public void rss_isBlogger() throws XmlPullParserException, IOException {
        RSSFeed rssFeed = new RSSFeed();
        rssFeed.fetchRSS();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(rssFeed.getRawXML()));

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("id")) {
                parser.next();
                assertTrue(parser.getText().startsWith("tag:blogger.com"));
                break;
            }
            parser.nextTag();
        }
    }
}
