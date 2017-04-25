package com.sst.announcements.Feed;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

public class RSSParser {
    public static Feed parse(XML xml) throws XPathExpressionException, ParseException,
            IOException, SAXException, TransformerException, ParserConfigurationException {
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();

        String xpathLastChanged = "/feed/updated/text()";
        String xpathCategories = "/feed/category";
        String xpathEntries = "/feed/entry";

        String lastChangedString = xml.xpathString(xpathLastChanged);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSSz",
                Locale.ENGLISH);
        Date lastChanged = format.parse(lastChangedString);

        for (XML categoryXML : xml.xpathMultipleNodes(xpathCategories)) {
            categories.add(categoryXML.xpathString("/category/@term"));
        }

        for (XML entryXML : xml.xpathMultipleNodes(xpathEntries)) {
            String id = entryXML.xpathString("/entry/id/text()");
            String publishDate = entryXML.xpathString("/entry/published/text()");
            String lastUpdated = entryXML.xpathString("/entry/updated/text()");
            String author = entryXML.xpathString("/entry/author/name/text()");
            String bloggerLink = entryXML.xpathString("/entry/link[@rel='alternate']/@href");
            String title = entryXML.xpathString("/entry/title/text()");
            String content = entryXML.xpathString("/entry/content/text()");

            ArrayList<String> entryCategories = new ArrayList<>();
            for (XML entryCategoryXML : entryXML.xpathMultipleNodes("/entry/category")) {
                entryCategories.add(entryCategoryXML.xpathString("/category/@term"));
            }

            entries.add(new Entry(id, publishDate, lastUpdated, entryCategories, author,
                    bloggerLink, title, content));
        }

        return new Feed(lastChanged, categories, entries);
    }
}
