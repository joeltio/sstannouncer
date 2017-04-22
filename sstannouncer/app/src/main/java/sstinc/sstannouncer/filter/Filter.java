package sstinc.sstannouncer.filter;



import java.util.ArrayList;
import java.util.Collection;

/**
 * Filter
 * Defines a filter, which removes or retains objects according to a filter predicate
 * @see FilterPredicate
 */

public class Filter{
    private FilterPredicate predicate;
    private Collection<Object> feed;

    /**
     * Filter Constructor
     * Constructs a new filter object, which filters the objects in feed according to the passed
     * predicate
     *
     * @param predicate Predicate defines whether the object gets removed or retained
     * @param feed A collection of objects to filter.
     */
    public Filter(FilterPredicate predicate, Collection<Object> feed)
    {
        this.predicate = predicate;
        this.feed = feed;
    }

    /**
     * Change Feed
     * Change the objects that the filter filters according to the predicate.
     *
     * @param feed A collection of objects to filter.
     */
    public void setFeed(Collection<Object> feed)
    {
        this.feed = feed;
    }

    /**
     * Filter the feed.
     * Filter the feed, using the criteria defined by the predicate, returning the filtrate, or
     * objects that have not been filtered out.
     *
     * @return Returns the filtrate , a collection of objects that have not been removed.
     */
    public Collection<Object> filter()
    {
        ArrayList <Object> feedList = new ArrayList<>(this.feed);
        for(Object object : feedList)
        {
            boolean shouldFilter = this.predicate.filter(object);
            if(shouldFilter == true)
            {
                feedList.remove(object);
            }
        }

        return (Collection<Object>) feedList;
    }
}

