package sstinc.sstannouncer.resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Defines a resource.
 * A resource is a piece of information or data that is dynamic and mutable
 */

public class Resource {
    private String URL;
    private Date timeStamp;
    private String data;
    private String delim = "\034\005\006\022\001\002";


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

    /**
     * Resource constructor
     * Constructs a new resource from a string.
     * The String must be from the output of <code>toString()</code>
     * @param resource The string to create a resource from
     *
     * @see Resource#toString() .
     */
    Resource(String resource)
    {
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String []slicedResource = resource.split(this.delim);

        this.URL = slicedResource[0];
        try
        {
            this.timeStamp = dateFormatter.parse(slicedResource[1]);

        }
        catch(java.text.ParseException exp)
        {
            this.timeStamp = new Date();
        }
        this.data = slicedResource[2];
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

    //Equality
    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj) == false) return false;
        if (obj.getClass() != this.getClass()) return false;

        Resource otherObject = (Resource) obj;
        if (otherObject.getURL().equals(this.getURL()) &&
                otherObject.getTimeStamp().equals(this.getTimeStamp()) &&
                otherObject.getData().equals(this.getData()))
        {
            return true;
        }

        return false;
    }

    //Serialise Resource
    @Override
    public String toString()
    {
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        return String.format("%s%s%s%s%s", this.getURL(), delim,
                dateFormatter.format(this.timeStamp),
                delim, this.getData());
    }
}
