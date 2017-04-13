package sstinc.sstannouncer.Feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Feed {
    public Date getLastChanged() {
        return new Date();
    }

    public List<String> getCategories() {
        return new ArrayList<>();
    }

    public List<Entry> getEntries() {
        return new ArrayList<>();
    }
}
