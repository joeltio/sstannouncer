package sstinc.sstannouncer.android;

import android.app.Presentation;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Exchanger;
import java.util.prefs.Preferences;

import sstinc.sstannouncer.Feed.Feed;
import sstinc.sstannouncer.Feed.FeedNotificationAdaptor;
import sstinc.sstannouncer.Feed.RSSParser;
import sstinc.sstannouncer.Feed.XML;
import sstinc.sstannouncer.R;
import sstinc.sstannouncer.SettingsFragment;
import sstinc.sstannouncer.event.Event;
import sstinc.sstannouncer.event.EventController;
import sstinc.sstannouncer.event.EventHandler;
import sstinc.sstannouncer.resource.HTTPResourceAcquirer;
import sstinc.sstannouncer.resource.Resource;
import sstinc.sstannouncer.resource.ResourceAcquirer;
import sstinc.sstannouncer.service.ResourceService;

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
 * @see sstinc.sstannouncer.event.EventController
 * @see AndroidEventAdaptor
 */
public class AndroidServiceAdaptor extends Service
{
    public static final String INTENT_EXTRA_START_BOOTUP =
            "sstinc.sstannouncer.service.start_up.intent.extra.boot";
    public static final String INTENT_EXTRA_REMOTE_MESSENGER =
        "sstinc.sstannouncer.service.connect.extra.remote_messenger";
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
        }catch(Exception exp){};
        //Storage - Remove on REDESIGN

        Log.d(this.LOG_TAG, stringTimeStamp);

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
                            Log.e("Resource Service", "Caught Exception trying to parse blog rss",
                                    exp);
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


        if(intent != null && intent.hasExtra(AndroidServiceAdaptor.INTENT_EXTRA_REMOTE_MESSENGER) == true)
        {
            //Service Connect
            try {
                Thread.sleep(1000 * 5);
            }
            catch(InterruptedException e){};
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
