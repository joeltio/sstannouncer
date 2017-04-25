package com.sst.anouncements.android;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sst.anouncements.Feed.Feed;
import com.sst.anouncements.Feed.FeedNotificationAdaptor;
import com.sst.anouncements.Feed.RSSParser;
import com.sst.anouncements.Feed.XML;
import com.sst.anouncements.R;
import com.sst.anouncements.event.Event;
import com.sst.anouncements.event.EventController;
import com.sst.anouncements.event.EventHandler;
import com.sst.anouncements.resource.HTTPResourceAcquirer;
import com.sst.anouncements.resource.Resource;
import com.sst.anouncements.resource.ResourceAcquirer;
import com.sst.anouncements.service.ResourceService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Defines an Adaptor between Android and Service.
 * The adaptor between integrating with the android service system and the  <code>Service</code>
 * system.
 *
 * Connecting or Binding to the Service
 *
 * To connect to the Service, use <code>Context.startService()</code>with an intent extra with the
 * key <code>INTENT_EXTRA_REMOTE_MESSENGER</code> and the value of the Local Messenger from the
 * AndroidEventAdaptor instance, henceforth communication with the instance would be done on the
 * through the bound EventController. Do not use <code>Context.bindService()</code>
 *
 * @see android.app.Service
 * @see AndroidEventAdaptor
 */
public class AndroidServiceAdaptor extends Service
{
    public static final String INTENT_EXTRA_START_BOOTUP =
            "sstinc.com.sst.anouncements.service.start_up.intent.extra.boot";
    public static final String INTENT_EXTRA_REMOTE_MESSENGER =
        "sstinc.com.sst.anouncements.service.connect.extra.remote_messenger";
    public static final String LOG_TAG = "sstannouncer.resservice";

    private EventController eventController;
    private AndroidEventAdaptor androidEventAdaptor;
    private Resource resource;
    private ResourceService resourceService;

    @Override
    public void onCreate() {
        super.onCreate();

        //Storage - Remove on REDESIGN
        final SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor  settingsEditor = settings.edit();
        final DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String stringTimeStamp = dateFormatter.format(new Date(0));
        stringTimeStamp = settings.getString("resource.timestamp", stringTimeStamp);
        Date timeStamp = null;
        try {
            timeStamp = dateFormatter.parse(stringTimeStamp);
        }catch(Exception exp){}
        //Storage - Remove on REDESIGN

        this.eventController = new EventController();
        this.androidEventAdaptor = new AndroidEventAdaptor(this.eventController);
        this.resource = new
                Resource(getString(R.string.blog_rss_url), timeStamp, null);
        ResourceAcquirer resourceAcquirer = new HTTPResourceAcquirer();

        this.resourceService = new ResourceService(this.resource, resourceAcquirer);
        this.resourceService.setResourceChangedEvent(new
                Event(getString(R.string.event_resource_changed_blog), null, null));
        this.resourceService.bind(this.eventController);
        this.resourceService.setFrequency(0.1 / 6); //Check Every Minute

        //NOTIFICATION ~ REMOVE ON REDESIGN, TIGHTLY COUPLED CODE>>>
        this.eventController.listen(this.toString(), this.resourceService.getResourceChangedEvent().getIdentifier(),
                new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        Resource resource = new Resource(event.getData());
                        AndroidNotificationAdaptor notificationAdaptor =
                                new AndroidNotificationAdaptor(getApplicationContext());
                        XML rss = null;
                        Feed feed = null;

                        //Storage - remove on redesign
                        settingsEditor.putString("resource.timestamp", dateFormatter.format(resource.getTimeStamp()));
                        settingsEditor.apply();

                        try
                        {
                            rss = new XML(resource.getData());
                            feed = RSSParser.parse(rss);
                        }catch (Exception exp)
                        {
                            return;
                        }

                        FeedNotificationAdaptor feedNotificationAdaptor =
                                new FeedNotificationAdaptor(feed, "Student Blog", notificationAdaptor);
                        feedNotificationAdaptor.changeFeed(feed);
                    }
                });
        //NOTIFICATION ~ REMOVE ON REDESIGN

    }

    /**
     * Connect or bind to the Service.
     * Connect or bind to the Service.
     * To connect, call with intent extra INTENT_EXTRA_REMOTE_MESSENGER with value of local
     * Messenger of the Local Android Event Adaptor.
     *
     * @see android.app.Service
     * @see AndroidEventAdaptor
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        try
        {
            Thread.sleep(1000 * 5);
        }catch(Exception exp){}

        if(intent != null && intent.hasExtra(AndroidServiceAdaptor.INTENT_EXTRA_REMOTE_MESSENGER) == true)
        {
            Messenger remoteMessenger = intent.
                    getParcelableExtra(AndroidServiceAdaptor.INTENT_EXTRA_REMOTE_MESSENGER);

            this.androidEventAdaptor.connect(remoteMessenger);
        }

        //Service Start
        this.resourceService.start();

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.androidEventAdaptor.getLocalMessenger().getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.resourceService.stop();
        this.androidEventAdaptor.disconnect();
    }
}
