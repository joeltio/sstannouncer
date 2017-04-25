package com.sst.announcements.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Event Controller
 * Facilitates the prorogation of events.
 * @see Event
 */
public class EventController {
    private Map<String, Map<String, EventHandler>> eventMap;

    /**
     * Event Controller Constructor.
     * Creates a new event controller if one does not already exist.
     * Returns
     */
    public EventController()
    {
        this.eventMap = new HashMap<String, Map<String, EventHandler>>();
    }

    /**
     * Listen for a event
     * Listen for the event specified by <code>eventIdentifier</code>.
     * If the event specified by eventIdentifier is raised, call the event handler provided.
     * Note that listening for a event does not require the event to exist.
     * Only one listen request would be accepted for a single listener event pair.
     * It is the responsibility of the caller to stop listening to  the notification using
     * To listen to all events, call this method with eventIdentifier as "*".
     * <code>unlisten()</code>
     *
     * @param listenerIdentifier A unique identifier to identify the caller.
     * @param eventIdentifier The Event Identifier of the event to listen to.
     * @param handler The event handled to be call when the event is raised.
     *
     * @return Returns true if listen request was successful, false if there has been a been a
     * previous request with the same <code>listenerIdentifier</code> and <code>eventIdentifier.</code>
     *
     * @see EventController#unlisten(String, String)
     */
    public boolean listen(String listenerIdentifier, String eventIdentifier, EventHandler handler)
    {
        if(this.eventMap.get(eventIdentifier) == null)
        {
            this.eventMap.put(eventIdentifier, new HashMap<String, EventHandler>());
        }

        if(this.eventMap.get(eventIdentifier).get(listenerIdentifier) != null)
        {
            //Caller has previously requested for listen on this event
            return false;
        }

        //Register for listener and handler
        this.eventMap.get(eventIdentifier).put(listenerIdentifier, handler);

        return true;
    }

    /**
     * UnListen to a event
     * Stop listening to a event specified by <code>eventIdentifier</code>.
     * If the event specified is currently does not exist or has never been listened to by the
     * listener, the invocation of this method would do nothing and return false.
     * To revert a previous request to listen to all events, pass eventIdentifier as "*".
     * NOTE: You can only revert a request to listen to all events, this does not revert all
     * individual listen requests.
     *
     * @param listenerIdentifier A unique identifier to identify the caller.
     * @param eventIdentifier The Event Identifier of the event to unlisten from.
     *
     * @return Returns true if unlisten request was successful, false if the event specified does
     * not exist or has never been listed too be the listener.
     *
     * @see EventController#listen(String, String, EventHandler)
     *
     */
    public boolean unlisten(String listenerIdentifier, String eventIdentifier)
    {
        //Event does not exist
        if(this.eventMap.get(eventIdentifier) == null) return false;
        //No previous listen request from the caller can be found.
        if(this.eventMap.get(eventIdentifier).get(listenerIdentifier) == null) return false;

        //Unregister for listener and handler
        this.eventMap.get(eventIdentifier).remove(listenerIdentifier);

        return true;
    }


    /**
     * Raise a Event
     * Raise the event provided.
     * Any event handlers listening for the event provided would be called.
     *
     * @param event
     *
     * @see EventController#listen(String, String, EventHandler)
     */
    public void raise(Event event)
    {
        ArrayList<EventHandler> handlerList = this.buildHandlerList(event.getIdentifier());
        for(EventHandler handler : handlerList)
        {
            handler.handle(event);
        }

        return;
    }

    //Private Utility Methods
    //Build a list of event handlers for event specified by event Identifier
    public ArrayList<EventHandler> buildHandlerList(String eventIdentifier)
    {
        Map<String, EventHandler> eventHandlerMap = this.eventMap.get(eventIdentifier);
        Map<String, EventHandler> wildCardHandlerMap = this.eventMap.get("*");

        ArrayList<EventHandler> handlerList = new ArrayList<EventHandler>();

        if(eventHandlerMap != null)
        {
            handlerList.addAll(eventHandlerMap.values());
        }
        if(wildCardHandlerMap != null)
        {
            handlerList.addAll(wildCardHandlerMap.values());
        }

        return handlerList;
    }


}
