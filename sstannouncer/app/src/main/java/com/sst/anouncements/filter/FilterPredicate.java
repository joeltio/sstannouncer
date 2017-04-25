package com.sst.anouncements.filter;

/**
 * Filter Predicate
 * Defines a interface for a filter predicate
 */

public interface FilterPredicate {
    /**
     * Determines whether to filter the object.
     * Determines whether the object passed should be filtered.
     * Returns true if object should be filtered, aka removed.
     *
     * @param object The object to determine whether to filter.
     *
     * @return Returns true if the object should be filtered, false otherwise.
     */
    public boolean filter(Object object);
}
