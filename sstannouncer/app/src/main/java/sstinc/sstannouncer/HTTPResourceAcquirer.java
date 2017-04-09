package sstinc.sstannouncer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Defines a resource acquirer that obtains resources via HTTP.
 * @see sstinc.sstannouncer.ResourceAcquirer
 */
public class HTTPResourceAcquirer implements ResourceAcquirer
{
    private static String convertStreamToString(java.io.InputStream is) {
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
    public static int retrieve(Resource resource)
    {
        Date resourceTimeStamp = null;
        String resourceData = null;
        HttpURLConnection retrieveConnection = null;
        int status = 0;

        try
        {
            URL resourceURL = new URL(resource.getURL());
            retrieveConnection = (HttpURLConnection) resourceURL.openConnection();
            retrieveConnection.connect();

            resourceTimeStamp = new Date(); //Time Stamp set to current time
            resourceData = convertStreamToString(retrieveConnection.getInputStream());

            status = - retrieveConnection.getResponseCode();
        }
        catch (IOException exp)
        {
            exp.printStackTrace();
            return status;
        }

        resource.mutate(resourceData, resourceTimeStamp);
        return 0;
    }

}
