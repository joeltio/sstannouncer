package sstinc.sstannouncer;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sstinc.sstannouncer.Feed.Entry;
import sstinc.sstannouncer.Feed.Feed;
import sstinc.sstannouncer.Feed.RSSParser;
import sstinc.sstannouncer.Feed.XML;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RSSParserTest {
    @Test
    public void feed_last_changed_isDate() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
            Feed feed = RSSParser.parse(xml);

            assertTrue(feed.getLastChanged() != null);
            assertTrue(feed.getLastChanged() instanceof Date);

            Date now = new Date();

            assertTrue(feed.getLastChanged().before(now));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void feed_hasCategories() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
            Feed feed = RSSParser.parse(xml);

            assertFalse(feed.getCategories().isEmpty());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void feed_hasEntries() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
            Feed feed = RSSParser.parse(xml);

            assertFalse(feed.getEntries().isEmpty());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void feed_entry_data_notEmpty() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");
            Feed feed = RSSParser.parse(xml);

            List<Entry> entries = feed.getEntries();
            for (Entry entry : entries) {
                assertFalse(entry.getId().isEmpty());
                assertFalse(entry.getPublished().isEmpty());
                assertFalse(entry.getLastUpdated().isEmpty());
                assertFalse(entry.getAuthorName().isEmpty());
                assertFalse(entry.getBloggerLink().isEmpty());

                assertFalse(entry.getTitle().isEmpty());
                assertFalse(entry.getContent().isEmpty());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private boolean validate_with_regex(String value, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    @Test
    public void feed_entries_hasValidData() {
        XML xml = new XML();
        try {
            xml.fetch("http://studentsblog.sst.edu.sg/feeds/posts/default");

            Feed feed = RSSParser.parse(xml);

            List<Entry> entries = feed.getEntries();
            for (Entry entry : entries) {
                // Example id: tag:blogger.com,1999:blog-2263345748458699524.post-351551394302599923
                String idRegex = "tag:blogger\\.com,\\d{4}:blog-\\d+\\.post-\\d+";
                assertTrue(validate_with_regex(entry.getId(), idRegex));

                // The regex only matches for numbers, a month like 14 would still be accepted
                String dateRegex = "\\d{4}(-\\d{2}){2}T\\d{2}(:\\d{2}){2}\\.\\d{3}\\+\\d{2}:\\d{2}";
                assertTrue(validate_with_regex(entry.getPublished(), dateRegex));
                assertTrue(validate_with_regex(entry.getLastUpdated(), dateRegex));

                // This is a very basic check, other links may pass
                assertTrue(entry.getBloggerLink().startsWith("http://"));
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
