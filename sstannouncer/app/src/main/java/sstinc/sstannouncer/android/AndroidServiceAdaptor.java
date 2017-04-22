package sstinc.sstannouncer.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.Exchanger;

import sstinc.sstannouncer.Feed.Feed;
import sstinc.sstannouncer.Feed.FeedNotificationAdaptor;
import sstinc.sstannouncer.Feed.RSSParser;
import sstinc.sstannouncer.Feed.XML;
import sstinc.sstannouncer.R;
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

        this.eventController = new EventController();
        this.androidEventAdaptor = new AndroidEventAdaptor(this.eventController);
        this.resource = new
                Resource(getString(R.string.blog_rss_url), new Date(0), null);
        ResourceAcquirer resourceAcquirer = new HTTPResourceAcquirer();

        this.resourceService = new ResourceService(this.resource, resourceAcquirer);
        this.resourceService.setResourceChangedEvent(new
                Event(getString(R.string.event_resource_changed_blog), null, null));
        this.resourceService.bind(this.eventController);
        this.resourceService.start();

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

        if(intent.getBooleanExtra(AndroidServiceAdaptor.INTENT_EXTRA_START_BOOTUP, false) == true)
        {
            //Service Start
            Log.i(this.LOG_TAG, "Resource Service Started");
        }
        else
        {
            //Service Connect
            Log.i(this.LOG_TAG, "Connection Requests Recieved");
            Log.i(this.LOG_TAG, "Attempting Connection");
            Messenger remoteMessenger = intent.
                    getParcelableExtra(AndroidServiceAdaptor.INTENT_EXTRA_REMOTE_MESSENGER);
            this.androidEventAdaptor.connect(remoteMessenger);

            if(this.androidEventAdaptor.connected())
            {
                Log.i(this.LOG_TAG, "Connection Established");
            }
            else
            {
                Log.i(this.LOG_TAG, "Connection Failed ");
            }
        }

        return Service.START_REDELIVER_INTENT;
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
