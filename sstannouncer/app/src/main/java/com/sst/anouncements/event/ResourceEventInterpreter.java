package com.sst.anouncements.event;

import com.sst.anouncements.Feed.Feed;
import com.sst.anouncements.Feed.RSSParser;
import com.sst.anouncements.Feed.XML;
import com.sst.anouncements.resource.Resource;
import com.sst.anouncements.service.ResourceService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource Event Interpreter.
 *
 * Interprets the types of resources, triggering the necessary actions to handle the resource event.
*/
public class ResourceEventInterpreter implements EventHandler
{
    public static String RESOURCE_TYPE_FEED = "resource.type.feed";
    public static String RESOURCE_TYPE_UNKNOWN = "resource.type.unknown";

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
     * @param state Internal Resource interpreter state, pass null to create a stateless object.
     *
     */
    public ResourceEventInterpreter(EventController eventController, Map<String, String> state)
    {
        this.resourceChangedEvent = ResourceService.getResourceChangedEvent();
        this.bind(eventController);
        if(state != null)
        {
            this.resourceMap = new HashMap<String, Resource>();
            for(String key : state.keySet())
            {
                this.resourceMap.put(key, new Resource(state.get(key)));
            }
        }
    }

    /**
     * Bind to a Event Controller.
     * Bind to the passed Event Controller, listening for resource events raised on that event
     * controller.
     * If currently bound to a event controller, the method unbind as defined by
     * <code>unBind()</code> from the current event controller and bind to the passed event
     * Controller.
     * @param eventController The event controller to bind to.
     */
    public void bind(EventController eventController)
    {
        if(this.eventController != null) this.unBind();
        this.eventController = eventController;
        this.eventController.
                listen(this.toString(), this.resourceChangedEvent.getIdentifier(), this);
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


    @Override
    public void handle(Event event) {
        if(event.getIdentifier().equals(this.resourceChangedEvent.getIdentifier()))
        {
            this.handleResourceChanged(event);
        }
    }

    /**
     * Get Interpreter State
     * Retrieve the internal interpreter state that can be used to reinitialize the object.
     *
     * @return The interpreter state.
     */
    public Map<String, String> getState()
    {
        Map<String, String> state = new HashMap<String, String>();
        for(String key : this.resourceMap.keySet())
        {
            state.put(key, this.resourceMap.get(key).toString());
        }

        return state;
    }


    //Private Utility Methods
    private void handleResourceChanged(Event event)
    {
        Resource resource = new Resource(event.getData());
        String resourceType = this.interpretResource(resource);

        if(resourceType.equals(this.RESOURCE_TYPE_FEED))
        {
            this.raiseFeedChanged(event);
        }
        else
        {

        }

        this.resourceMap.put(resource.getURL(), resource);
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
        };


        if(typeFeed == true) return this.RESOURCE_TYPE_FEED;
        else return this.RESOURCE_TYPE_UNKNOWN;
    }

    private void raiseFeedChanged(Event event)
    {
        //TODO: Send Feed Objects Instead

        if(this.eventController != null)
        {
            Resource resource = new Resource(event.getData());
            Date previousTimeStamp = this.resourceMap.get(resource.getURL()).getTimeStamp();
            if(previousTimeStamp == null)
            {
                previousTimeStamp = new Date(0);
            }

            Event feedChanged =
                    new Event(FeedEventInterpreter.getFeedChangedEvent().getIdentifier(),
                            previousTimeStamp,
                            event.getData());

            this.eventController.raise(feedChanged);
        }
    }

}
