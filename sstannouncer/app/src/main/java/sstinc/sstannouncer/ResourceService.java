package sstinc.sstannouncer;

/**
 * Defines a service which maintains a resource
 *
 * @see sstinc.sstannouncer.Resource
 * @see sstinc.sstannouncer.Service
 */

public class ResourceService extends Service
{
    private ResourceAcquirer acquirer;
    private Resource resource;
    private int status;
    private String serviceThreadName;
    private volatile boolean serviceThreadStop;
    Thread serviceThread;


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
     * Constructor for ResourceService
     * Creates a new resource service that would maintain the resource passed by the caller.
     * The resource mantains the resource using the methodology provided by the ResourceAcquirer.
     *
     * @param resource The resource to maintain.
     * @param acquirer The way the resource is acquired.
     *
     * @see sstinc.sstannouncer.ResourceAcquirer
     */
    ResourceService(Resource resource, ResourceAcquirer acquirer)
    {
        this.resource = resource;
        this.acquirer = acquirer;
        this.status = 0;
        this.serviceThreadName = "ResourceService##" + resource.getURL();
        this.serviceThread = new Thread(this, this.serviceThreadName);
        this.serviceThreadStop = false;
    }

    public void start()
    {
        if(this.isAlive() == true )  this.kill();

        this.serviceThread = new Thread(this, this.serviceThreadName);
        this.serviceThreadStop = false;
        this.serviceThread.start();
    }

    public void end()
    {
        if(this.isAlive() == true)
        {
            this.serviceThreadStop = true;
            try {
                this.serviceThread.join();
            }
            catch(InterruptedException e) {
                return;
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
}
