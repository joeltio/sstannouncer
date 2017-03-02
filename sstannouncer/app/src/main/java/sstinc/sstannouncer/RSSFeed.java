package sstinc.sstannouncer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RSSFeed {
    private String rawXML;
    private Date lastChanged;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        processRSS();
    }

    private void processRSS() {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(this.rawXML));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("updated")) {
                    parser.next();

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSSz",
                            Locale.ENGLISH);
                    this.lastChanged = format.parse(parser.getText());
                }
                eventType = parser.next();
            }
        } catch (IOException|XmlPullParserException|ParseException e) {
            e.printStackTrace();
        }
    }

    public String getRawXML() {
        return this.rawXML;
    }

    public Date getLastChanged() {
        return this.lastChanged;
    }

    public List<Entry> getEntries() {
        return new ArrayList<>();
    }

    public List<String> getCategories() {
        return new ArrayList<>();
    }
}
