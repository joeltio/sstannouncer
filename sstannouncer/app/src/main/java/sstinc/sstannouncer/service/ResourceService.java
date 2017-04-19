package sstinc.sstannouncer.service;

import java.util.Date;

import sstinc.sstannouncer.resource.Resource;
import sstinc.sstannouncer.resource.ResourceAcquirer;
import sstinc.sstannouncer.event.Event;
import sstinc.sstannouncer.event.EventController;

/**
 * Defines a service which maintains a resource
 *
 * @see Resource
 * @see Service
 */

public class ResourceService extends Service
{
    /**
     * Resource Changed Event
     * The Resource Service would raise this event when the resource is changed.
     * The event data field would the changed resource that is encoded in a string format defined by
     * <code>Resource.toString()</code>.
     *
     * @see Resource#toString()
     */
    public Event ResourceChangedEvent;

    private double frequency;

    private ResourceAcquirer acquirer;
    private Resource resource;
    private int status;

    private String serviceThreadName;
    private volatile boolean serviceThreadStop;
    Thread serviceThread;

    private EventController boundEventControl;


    /**
     * Constructor for ResourceService
     * Creates a new resource service that would maintain the resource passed by the caller.
     * The resource maintains the resource using the methodology provided by the ResourceAcquirer.
     * The <code>frequency</code> defines the frequency of the service to checl for new updates to
     * resource.
     *
     * @param resource The resource to maintain.
     * @param acquirer The way the resource is acquired.
     *
     * @see ResourceAcquirer
     */
    ResourceService(Resource resource, ResourceAcquirer acquirer)
    {
        this.resource = resource;
        this.acquirer = acquirer;
        this.ResourceChangedEvent = new Event(String.format("EVENT_RS_ResourceChanged_%s",
                this.resource.getURL()));
        this.status = 0;
        this.serviceThreadName = "ResourceService/" + resource.getURL();
        this.serviceThread = new Thread(this, this.serviceThreadName);
        this.serviceThreadStop = false;
    }


    /**
     * Status of the resource.
     * This value may be useful to determine the validity of the resource obtained.
     * Returns the status of the Resource Acquirer for the most recent retrival of the resource.
     * The status value is defined as per implementation of Resource Acquirer.
     *
     * @return Returns the status of the ResourceAcquirer.
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Determines if service is currently maintaining the resource.
     *
     * @return Returns if the service is currently maintaining the resource, false if not.
     */
    public boolean isAlive()
    {
        Thread.State serviceThreadState = serviceThread.getState();

        if(serviceThreadState == Thread.State.RUNNABLE) return true;
        else return false;
    }

    /**
     * Change the Frequency of the Resource Service.
     * Change the frequency that the Resource Service checks for changes to the resource.
     * Changes to the frequency would only take effect on the next run of the service.
     * The frequency best accuracy be 1/1000 Hz.
     *
     * @param frequency Frequency to check for changes to the resource in Hertz (Hz)
     *
     */
    public void setFrequency(double frequency)
    {
        this.frequency = frequency;
    }

    /**
     * Bind to an Event Controller
     * Bind to the specified Event Controller.
     * The Resource Service would raise a <code>ResourceChanged</code> event when the resource the
     * service maintains changes.
     *
     * @param eventController-The event controller to bind to.
     */
    public void bind(EventController eventController)
    {
        this.boundEventControl = eventController;
    }

    /**
     * Unbind from an Event Controller
     * Unbind from the specified Event Controller
     * The Resource Service would stop raising a <code>ResourceChanged</code> event when the
     * resource to service maintains changes.
     */
    public void unbind()
    {
        this.boundEventControl = null;
    }

    public void start()
    {
        if(this.isAlive() == true )  this.kill();

        this.serviceThread = new Thread(this, this.serviceThreadName);
        this.serviceThreadStop = false;
        this.serviceThread.start();
    }

    public void stop()
    {
        if(this.isAlive() == true)
        {
            this.serviceThreadStop = true;
            try {
                this.serviceThread.join();
            }
            catch(InterruptedException e) {
                this.kill();
            }
        }
    }

    public void kill()
    {
        if(this.isAlive() == true)
        {
            this.serviceThread.interrupt();
        }
    }

    //Service Thread
    @Override
    public void run()
    {
        while(this.serviceThreadStop == false)
        {
            //Set current time stamp to the current resource time stamp or to the unix epoch.
            Date currentTimeStamp  = (this.resource.getTimeStamp() == null) ? new Date(0) :
                    this.resource.getTimeStamp();

            this.status = this.acquirer.retrieve(this.resource);

            if(this.status == 0 && currentTimeStamp.before(this.resource.getTimeStamp()))
            {
                //Retrieve Successful and Resource Changed
                if(this.boundEventControl != null)
                {
                    Event changeEvent = new Event(ResourceChangedEvent.getIdentifier(),
                            this.resource.getTimeStamp(), this.resource.toString());
                    this.boundEventControl.raise(changeEvent);
                }
            }

            //Frequency Control
            double delay = 1.0/this.frequency;
            long delayMillis = (long) delay * 1000;
            try {
                Thread.sleep(delayMillis);
            }
            catch(InterruptedException e){
                this.kill();
            }
        }
    }
}
