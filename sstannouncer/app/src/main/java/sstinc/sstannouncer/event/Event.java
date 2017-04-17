package sstinc.sstannouncer.event;

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

    /**
     * Event Constructor.
     * Creates a new event for the given identifier.
     * Sets the timeStamp and data of the new event to <code>null</code>.
     *
     * @param identifier A unique identifier used to identify the event;
     */
    public Event(String identifier)
    {
        this.identifier = identifier;
        this.timeStamp = null;
        this.data = null;
    }

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

    public String getIdentifier() {
        return identifier;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }


    public String getData() {
        return data;
    }

    //Equality
    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj) == false) return false;
        if(obj.getClass() != this.getClass()) return false;

        Event otherObject = (Event)obj;
        if(otherObject.getIdentifier().equals(this.getIdentifier()))
        {
            return true;
        }
        return false;
    }
}
