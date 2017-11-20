package com.sst.anouncements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;

//HTTP Fetching Methodology
//Fetches Resources via HTTP
public class HTTPFetchMethod {
    private static String TAG = "HTTPFetchMethod";

    //HTTP Fetch Exception
    //Defines Exception when fetching resources via URL fails
    public class FetchException extends Exception
    {
        public final static int ID_NO_CONNECTION = 0;
        public final static int ID_NOT_FOUND = 1;
        public final static int ID_FORBIDDEN = 2;
        public final static int ID_TIMEOUT = 3;
        public final static int ID_RESPONSE_CORRUPT = 4;

        private int errorID;

        public FetchException(int errorID)
        {
            this.errorID = errorID;
        }

        public int what(){
            return this.errorID;
        }

        @Override
        public String getMessage() {
            if(this.errorID == ID_NO_CONNECTION)
                return "FetchException: A connection to the server could not be established.";
            else if(this.errorID == ID_NOT_FOUND)
                return "FetchException: The Resource could not be found on the server";
            else if(this.errorID == ID_FORBIDDEN)
                return "FetchException: The request to fetch the resource was declined by the server";
            else if(this.errorID == ID_TIMEOUT)
                return "FetchException: The server took too long to respond to the request for the resource";
            else if(this.errorID == ID_RESPONSE_CORRUPT)
                return "FetchException: The response from the server could not be read and may be corrupted";
            else return "FetchException: Unknown error";
        }

        @Override
        public String getLocalizedMessage() {
            return this.getMessage();
        }
    }

    public Date getModified(String location) throws FetchException
    {
        try {
            URL url = new URL(location);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(2000);
            connection.setInstanceFollowRedirects(true);

            int response =  connection.getResponseCode();

            if (response == 404) throw new FetchException(FetchException.ID_NOT_FOUND);
            if (response == 401 || response == 403)
                throw new FetchException(FetchException.ID_FORBIDDEN);
            if (response == 503) throw new FetchException(FetchException.ID_TIMEOUT);
            if (response != 200) throw new FetchException(-1); //Unknown Error

            Date lastModified = new Date(connection.getLastModified());
            connection.disconnect();

            return lastModified;

        } catch (SocketTimeoutException e) {
            throw new FetchException(FetchException.ID_TIMEOUT);
        } catch (MalformedURLException e) {
            throw new FetchException(FetchException.ID_NOT_FOUND);
        } catch (IOException e) {
            throw new FetchException(FetchException.ID_NO_CONNECTION);
        }
    }

    public String getResource(String location) throws FetchException {
        try {
            URL url = new URL(location);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/rss+xml");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setConnectTimeout(10000);
            connection.setInstanceFollowRedirects(true);

            int response = connection.getResponseCode();

            if (response == 404) throw new FetchException(FetchException.ID_NOT_FOUND);
            if (response == 401 || response == 403)
                throw new FetchException(FetchException.ID_FORBIDDEN);
            if (response == 503) throw new FetchException(FetchException.ID_TIMEOUT);
            if (response != 200) throw new FetchException(-1); //Unknown Error

            String responseBody = this.extractResponse(connection);
            if(responseBody == null) throw new FetchException(FetchException.ID_RESPONSE_CORRUPT);

            connection.disconnect();

            return responseBody;
        } catch (SocketTimeoutException e) {
            throw new FetchException(FetchException.ID_TIMEOUT);
        } catch (MalformedURLException e) {
            throw new FetchException(FetchException.ID_NOT_FOUND);
        } catch (IOException e) {
            throw new FetchException(FetchException.ID_NO_CONNECTION);
        }
    }


    private String extractResponse(HttpURLConnection connection)
    {
        try
        {
            InputStream stream = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }catch (Exception e){ return null; }
    }
}

