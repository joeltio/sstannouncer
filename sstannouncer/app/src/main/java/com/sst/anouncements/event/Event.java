package com.sst.anouncements.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An Event.
 * A significant happening that may be important to objects that are interested in the event.
 * An event is identified by its identifier and may contain data useful to interested parties.
 */
public class Event {

    private String identifier;
    private Date timeStamp;
    private String data;
    private String delim = "\035\006\005\022\001\002";

    /**
     * Event Constructor.
     * Creates a new event for the given identifier, timestamp and user defined data.
     *
     * @param identifier - An unique identified used to identify the event.
     * @param timeStamp - The date/time that the event occurred
     * @param data - User defined data that may be significant to the users of the event.
     *
     */
    public Event(String identifier, Date timeStamp, String data)
    {
        this.identifier = identifier;
        this.timeStamp = timeStamp;
        this.data = data;
    }

    /**
     * Event Constructor.
     * Creates a new event for the a string.
     * The string must be from the <code>toString()</code> method.
     *
     * @param event String to convert to an event.
     *
     * @see Event#toString()
     */
    public Event(String event)
    {
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String []slicedEvent = event.split(this.delim);

        this.identifier = slicedEvent[0];
        try
        {
            this.timeStamp = dateFormatter.parse(slicedEvent[1]);

        }
        catch(java.text.ParseException exp)
        {
            this.timeStamp = new Date();
        }
        this.data = slicedEvent[2];
    }

    /**
     * Event Copy Constructor.
     * Constructs a new event that is a copy, as defined by <code>equals()</code>, to the passed
     * event.
     * @param event The event to copy.
     */
    public Event(Event event)
    {
        this.identifier = event.getIdentifier();
        this.timeStamp = event.getTimeStamp();
        this.data = event.getData();
    }



    public String getIdentifier() {
        return identifier;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != this.getClass()) return false;

        Event otherObject = (Event)obj;
        return otherObject.getIdentifier().equals(this.getIdentifier());

    }


    @Override
    public String toString()
    {
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        return String.format("%s%s%s%s%s", this.getIdentifier(), this.delim,
                dateFormatter.format(this.getTimeStamp()), this.delim,
                this.getData());
    }
}
