package sstinc.sstannouncer.resource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import sstinc.sstannouncer.resource.Resource;
import sstinc.sstannouncer.resource.ResourceAcquirer;

/**
 * Defines a resource acquirer that obtains resources via HTTP.
 * @see ResourceAcquirer
 */
public class HTTPResourceAcquirer extends ResourceAcquirer
{

    private String convertStreamToString(java.io.InputStream is)
    {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Retrieves the resource
     * Retrieves the data or information of the resource.
     * Returns a Status value, which describes the state of the retrival:
     * 0 - The retrival was successful.
     * < 0 (Less then 0) - The retrival failed, the HTTP response code is returned as a negative
     * integer. (eg. Returns -404 if HTTP response code was 404)
     *
     * @param resource The resource to retrieve
     * @return Returns the status of the retrival.
     */
    public int retrieve(Resource resource)
    {
        Date resourceTimeStamp = null;
        String resourceData = null;
        HttpURLConnection checkConnection = null;
        int status = 0;

        try
        {

            //Retrieve the HTTP Header to test for a resource change
            URL resourceURL = new URL(resource.getURL());
            checkConnection = (HttpURLConnection) resourceURL.openConnection();
            checkConnection.setRequestMethod("HEAD");
            checkConnection.connect();

            resourceTimeStamp = new Date(checkConnection.getLastModified());

            status = - checkConnection.getResponseCode();

            if(resourceTimeStamp.compareTo(resource.getTimeStamp()) >= 0)
            {
                //Retrieve the Resource itself.
                HttpURLConnection retrieveConnection =
                        (HttpURLConnection) resourceURL.openConnection();
                retrieveConnection.setRequestMethod("GET");

                resourceData = this.convertStreamToString(retrieveConnection.getInputStream());
                resource.mutate(resourceData, resourceTimeStamp);

            }

        }
        catch (IOException exp)
        {
            exp.printStackTrace();
            return status;
        }

        return 0;
    }

}
