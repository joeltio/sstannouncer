package sstinc.sstannouncer.service;

/**
 * Service
 * A interface that describes a background process that does a job
 */
public abstract class Service implements Runnable
{
    /**
     * Obtains the status of the service.
     * The status value is defined as per each implementation.
     *
     * @return Returns status value.
     */
    public abstract int getStatus();

    /**
     * Determines whether the service is alive and doing its work, or not
     *
     * @return Returns true if the service is alive, false if not alvie;
     */
    public abstract boolean isAlive();

    /**
     * Initiate the service.
     * If the service is aliva, the current service would be killed as per <code>kill()</code>. A
     * new service would be started.
     *
     * @see Service#kill()
     */
    public abstract void start();

    /**
     * Stop the service.
     * The Service is allowed to run cleanup actions
     * If the service is not alive, nothing would be done.
     */
    public abstract void stop();

    /**
     * Terminate the service immediately.
     * The service would not be able to run cleanup actions
     * If the service is not alive, nothing would be done.
     */
    public abstract void kill();
}
