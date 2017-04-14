package sstinc.sstannouncer.Feed;

import java.util.ArrayList;

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
}
