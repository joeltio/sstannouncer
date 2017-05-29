package com.sst.anouncements.event;

import com.sst.anouncements.Feed.Feed;
import com.sst.anouncements.Feed.RSSParser;
import com.sst.anouncements.Feed.XML;
import com.sst.anouncements.resource.Resource;
import com.sst.anouncements.service.ResourceService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource Event Interpreter.
 *
 * Interprets the types of resources, triggering the necessary actions to handle the resource event.
 */
public class ResourceEventInterpreter implements EventHandler {
    public static String RESOURCE_TYPE_FEED = "resource.type.feed";
    public static String RESOURCE_TYPE_UNKNOWN = "resource.type.unknown";
    private static String INTERPRETER_STATE_CHANGED ="resource.event.interpreter.state.change";

    private Event resourceChangedEvent;
    private EventController eventController;
    private Map<String, Resource> resourceMap;

    /**
     * Resource Event Interpreter Constructor.
     * Creates a new Resource Event Interpreter for event controller and internal state.
     * Binds to the given Event Controller <code>eventController</code>, listening for resource
     * events on the event controller.
     *
     * @param eventController The event controller to bind to.
     * @param state           The internal state used to initialise the interpreter
     */
    public ResourceEventInterpreter(EventController eventController, String state) {
        this.resourceChangedEvent = ResourceService.getResourceChangedEvent();
        this.bind(eventController);
        this.readState(state);
    }

    /**
     * Bind to a Event Controller.
     * Bind to the passed Event Controller, listening for resource events raised on that event
     * controller.
     * If currently bound to a event controller, the method unbind as defined by
     * <code>unBind()</code> from the current event controller and bind to the passed event
     * Controller.
     *
     * @param eventController The event controller to bind to.
     */
    public void bind(EventController eventController) {
        if (this.eventController != null) this.unBind();
        this.eventController = eventController;
        this.eventController.
                listen(this.toString(), ResourceService.getResourceChangedEvent().getIdentifier(),
                        this);
    }

    /**
     * Unbind from the Event Controller.
     * Unbind from the currently bound event controller.
     * If not currently bound to a event controller, nothing would be done.
     */
    public void unBind() {
        if (this.eventController != null) {
            this.eventController.unlisten(this.toString(),
                    this.resourceChangedEvent.getIdentifier());
            this.eventController = null;
        }
    }

    /**
     * Retrieve State Changed Event
     * Retrieve the event that would be raised in the bound event controller when the object state
     * changes.
     * Listen for this event for changes in object state.
     * The changed object state would be stored in the events data field.
     *
     * @return Returns the state event
     */
    public static Event getStateChangeEvent()
    {
        return new Event(ResourceEventInterpreter.INTERPRETER_STATE_CHANGED, new Date(0), "");
    }

    /**
     * Retrieve Interpreter State
     * Retrieve the internal interpreter state that may be used to initalise the interpreter by
     * passing the state to the constructor.
     *
     * @return The interpreter state.
     */
    public String getState()
    {
        return this.writeState();
    }

    /**
     * Retrieve Resource State
     * Retrieve interpreter resource internal state.
     *
     * @param resource The resource to retrieve the state for.
     *
     * @return Returns the resource internal state, returns null if internal state not present.
     */
    public Resource getResourceState(Resource resource)
    {
        return this.resourceMap.get(resource.getURL());
    }


    @Override
    public void handle(Event event) {
        if(event.getIdentifier().equals(this.resourceChangedEvent.getIdentifier()))
        {
            this.handleResourceChanged(event);
        }
    }

    //Private Utility Methods
    private void handleResourceChanged(Event event)
    {
        Resource resource = new Resource(event.getData());
        String resourceType = this.interpretResource(resource);

        if(resourceType.equals(ResourceEventInterpreter.RESOURCE_TYPE_FEED))
        {
            this.raiseFeedChanged(event);
        }
        else
        {

        }

        Resource previousResource = this.getResourceState(resource);
        Date previousTimeStamp = (previousResource != null) ? previousResource.getTimeStamp()
                : new Date();

        this.resourceMap.put(resource.getURL(),
                new Resource(resource.getURL(), resource.getTimeStamp(), ""));
        this.eventController.raise(new Event(ResourceEventInterpreter.INTERPRETER_STATE_CHANGED,
                previousTimeStamp, this.getState()));
    }

    private String interpretResource(Resource resource)
    {
        boolean typeFeed = true;
        //Test for Feed
        try {
            XML resourceXML = new XML(resource.getData());
            Feed feed = RSSParser.parse(resourceXML);
        }catch(Exception exp){
            typeFeed = false;
        }


        if(typeFeed) return ResourceEventInterpreter.RESOURCE_TYPE_FEED;
        else return ResourceEventInterpreter.RESOURCE_TYPE_UNKNOWN;
    }

    private void raiseFeedChanged(Event event)
    {
        //TODO: Send Feed Objects Instead

        if(this.eventController != null)
        {
            Resource resource = new Resource(event.getData());
            Resource previousResource = this.resourceMap.get(resource.getURL());

            //Determine the Previous Time Stamp.
            //Prevents the notifications from entries before the app was first run.
            Date previousTimeStamp = new Date();
            if(previousResource != null)
            {
                previousTimeStamp = previousResource.getTimeStamp();
            }

            Event feedChanged =
                    new Event(FeedEventInterpreter.getFeedChangedEvent().getIdentifier(),
                            previousTimeStamp,
                            event.getData());

            this.eventController.raise(feedChanged);
        }
    }

    private void readState(String state)
    {
        if(state != null && state.length() > 0)
        {
            Map<String, String> mapState = new HashMap<String, String>();
            try {
                byte stateBytes[] = state.getBytes("ISO-8859-1");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stateBytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                mapState = (Map<String, String>) objectInputStream.readObject();
            } catch (Exception e) {
            }

            this.resourceMap = new HashMap<String, Resource>();
            for(String key : mapState.keySet())
            {
                this.resourceMap.put(key, new Resource(mapState.get(key)));
            }


        }
        else
        {
            this.resourceMap = new HashMap<String, Resource>();
        }
    }


    private String writeState()
    {
        Map<String, String> mapState = new HashMap<String, String >();

        for(String key : this.resourceMap.keySet())
        {
            String resourceString = this.resourceMap.get(key).toString();
            mapState.put(key, this.resourceMap.get(key).toString());
        }

        String state = "";
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(mapState);
            outputStream.flush();
            state = new String(byteArrayOutputStream.toString("ISO-8859-1"));
        } catch (Exception e) {
        }

        return state;
    }
}
