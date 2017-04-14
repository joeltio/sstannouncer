package sstinc.sstannouncer;

import java.util.Date;

/**
 * Defines a resource.
 * A resource is a piece of information or data that is dynamic and mutable
 */

public class Resource {
    private String URL;
    private Date timeStamp;
    private String data;

    /**
     * Resource constructor
     * Constructs a new resource.
     * @param URL An URL describing the location of where the resource is found.
     * @param timeStamp A time stamp describing when the data is last changed or updated.
     * @param data The data or information of the resource.
     */
    Resource(String URL, Date timeStamp, String data)
    {
        this.URL = URL;
        this.timeStamp = timeStamp;
        this.data = data;
    }

    //Resource Metadata
    public String getURL()
    {
        return this.URL;
    }

    public Date getTimeStamp()
    {
        return this.timeStamp;
    }

    public int getSize()
    {
        return this.data.length();
    }

    //Resource Data
    public String getData() {
        return data;
    }

    //Manipulate Resource
    /**
     * Mutate or Change the Resource
     * Change the data or information that the resource encompasses.
     *
     * @param data The changed data or information
     * @param timeStamp A time stamp describing when the data is last changed or updated.
     */
    public void mutate(String data, Date timeStamp)
    {
        this.timeStamp = timeStamp;
        this.data = data;
    }
}
