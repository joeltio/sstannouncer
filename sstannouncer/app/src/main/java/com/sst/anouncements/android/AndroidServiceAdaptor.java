package com.sst.anouncements.android;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.sst.anouncements.R;
import com.sst.anouncements.event.EventController;
import com.sst.anouncements.event.FeedEventInterpreter;
import com.sst.anouncements.event.ResourceEventInterpreter;
import com.sst.anouncements.resource.HTTPResourceAcquirer;
import com.sst.anouncements.resource.Resource;
import com.sst.anouncements.service.ResourceService;

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
    public static final String RESOURCE_INTERPRETER_STATE = "resource_interpreter.state";
    public static final String LOG_TAG = "sstannouncer.resservice";

    //Event
    private EventController eventController;
    private AndroidEventAdaptor androidEventAdaptor;
    private ResourceEventInterpreter resourceEventInterpreter;
    private FeedEventInterpreter feedEventInterpreter;

    //Service
    private ResourceService resourceService;

    //Notification
    private AndroidNotificationAdaptor androidNotificationAdaptor;

    @Override
    public void onCreate() {
        super.onCreate();

        try
        {
            Thread.sleep(1000 * 5);
        }catch(Exception exp){};

        final SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String interpeterState =
                settings.getString(this.RESOURCE_INTERPRETER_STATE, "");

        this.eventController = new EventController();
        this.androidEventAdaptor = new AndroidEventAdaptor(this.eventController);
        this.androidNotificationAdaptor =
                new AndroidNotificationAdaptor(this, R.drawable.notifcation_icon);
        this.resourceEventInterpreter =
                new ResourceEventInterpreter(this.eventController, interpeterState);
        this.feedEventInterpreter =
                new FeedEventInterpreter(this.eventController, this.androidNotificationAdaptor);

        String resourceURL = getString(R.string.blog_rss_url);
        Resource resource = new Resource(resourceURL, new Date(0), "");
        Resource previousResource = this.resourceEventInterpreter.getResourceState(resource);
        if(previousResource != null) resource = previousResource;

        this.resourceService = new ResourceService(resource, new HTTPResourceAcquirer());

        this.resourceService.bind(this.eventController);
        this.resourceService.setFrequency(0.1 / 6); //Check Every Minute

        this.resourceService.start();
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

        final SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor  settingsEditor = settings.edit();

        this.resourceService.stop();
        this.androidEventAdaptor.disconnect();

        settingsEditor.putString(this.RESOURCE_INTERPRETER_STATE,
                this.resourceEventInterpreter.getState());
        settingsEditor.apply();
    }

}
