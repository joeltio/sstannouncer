package sstinc.sstannouncer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RSSFeed {
    private String rawXML;

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void fetchRSS() {
        try {
            URL url = new URL("http://studentsblog.sst.edu.sg/feeds/posts/default");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();
            this.rawXML = convertStreamToString(urlConnection.getInputStream());
        } catch (MalformedURLException|IOException e) {
            e.printStackTrace();
        }
    }

    public String getRawXML() {
        return this.rawXML;
    }
}
