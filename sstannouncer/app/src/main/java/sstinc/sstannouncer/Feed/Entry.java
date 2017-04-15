package sstinc.sstannouncer.Feed;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Entry {
    private String id, publishDate, lastUpdated, author, bloggerLink, title, content;
    private ArrayList<String> categories;
    public Entry(String id, String publishDate, String lastUpdated,
                 ArrayList<String> categories, String author, String bloggerLink,
                 String title, String content) {
        this.id = id;
        this.publishDate = publishDate;
        this.lastUpdated = lastUpdated;
        this.categories = categories;
        this.author = author;
        this.bloggerLink = bloggerLink;
        this.title = title;
        this.content = content;
    }
    public String getId() {
        return this.id;
    }
    public String getPublished() {
        return this.publishDate;
    }
    public String getLastUpdated() {
        return this.lastUpdated;
    }
    public ArrayList<String> getCategories() {
        return this.categories;
    }
    public String getAuthorName() {
        return this.author;
    }
    public String getBloggerLink() {
        return this.bloggerLink;
    }
    public String getTitle() {
        return this.title;
    }
    public String getContent() {
        return this.content;
    }

    public String makeFilteredContent() {
        String content = this.content;
        // Remove HTML tags
        content = content.replaceAll("<[^>]*>", "");
        // Unescape characters
        content = StringEscapeUtils.unescapeHtml4(content);

        return content;
    }

    public static String toShortDate(String dateString) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSSz",
                Locale.ENGLISH);
        Date date = format.parse(dateString);
        Date now = new Date();

        Calendar dateCalendar = new GregorianCalendar();
        Calendar nowCalendar = new GregorianCalendar();

        dateCalendar.setTime(date);
        nowCalendar.setTime(now);

        int dateYear = dateCalendar.get(Calendar.YEAR);
        int nowYear = nowCalendar.get(Calendar.YEAR);

        DateFormat outputFormat;
        if (dateYear == nowYear) {
            outputFormat = new SimpleDateFormat("d MMM", Locale.ENGLISH);
        } else {
            outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        }

        return outputFormat.format(date);
    }
}
