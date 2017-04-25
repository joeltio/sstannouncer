package com.sst.anouncements.resource;

/**
 * Acquires a resource using a predefined methodology
 * The interface encompasses a methodology for retrieving resources
 *
 * @see Resource
 */

public abstract class ResourceAcquirer
{
    /**
     * Retrieves a resource
     * Retrieves the data or information for a given resource.
     * Stores the retrieved data or information in the passed resource object.
     * Returns a status value, the meaning of which is defined as per implementation.
     *
     * @param resource The resource object describing the resource to retrieve.
     * @return Returns An integer value describing the state of the retrival.
     *
     */
    public abstract int retrieve(Resource resource);
}
