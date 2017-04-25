package com.sst.anouncements.Feed;

import java.util.ArrayList;
import java.util.Date;

public class Feed {
    private final Date lastChanged;
    private final ArrayList<String> categories;
    private final ArrayList<Entry> entries;

    public Feed(Date lastChanged, ArrayList<String> categories, ArrayList<Entry> entries) {
        this.lastChanged = lastChanged;
        this.categories = categories;
        this.entries = entries;
    }

    public Date getLastChanged() {
        return this.lastChanged;
    }

    public ArrayList<String> getCategories() {
        return this.categories;
    }

    public ArrayList<Entry> getEntries() {
        return this.entries;
    }
}
