package sstinc.sstannouncer.Feed;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class XML {
    private String rawXml;
    public XML() {
        this.rawXml = "";
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void fetch(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.connect();
        this.rawXml = convertStreamToString(urlConnection.getInputStream());
    }

    public String getRawXML() {
        return this.rawXml;
    }

    public Object xpath(String xpath) {
        return "";
    }
}
