package com.sst.anouncements.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.Nullable;

import com.sst.anouncements.R;
import com.sst.anouncements.event.Event;
import com.sst.anouncements.event.EventController;
import com.sst.anouncements.event.EventHandler;
import com.sst.anouncements.event.FeedEventInterpreter;
import com.sst.anouncements.event.ResourceEventInterpreter;
import com.sst.anouncements.resource.HTTPResourceAcquirer;
import com.sst.anouncements.resource.Resource;
import com.sst.anouncements.service.ResourceService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    public static final String FILENAME_STATE = "serve_state.blob";



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

        this.eventController = new EventController();
        this.androidEventAdaptor = new AndroidEventAdaptor(this.eventController);
        this.androidNotificationAdaptor =
                new AndroidNotificationAdaptor(this, R.drawable.notifcation_icon);
        this.setupResourceEventInterpreter();
        this.feedEventInterpreter =
                new FeedEventInterpreter(this.eventController, this.androidNotificationAdaptor);

        this.setupResourceService();
        this.resourceService.start();

        String resourceURL = getString(R.string.blog_rss_url);
        Resource previousResource = this.resourceEventInterpreter.getResourceState(
                new Resource(resourceURL, new Date(0), ""));
        Resource resource = new Resource(resourceURL, new Date(0), "");
        if(previousResource != null)
        {
            resource =  new Resource(resourceURL, previousResource.getTimeStamp(),
                    previousResource.getData());
        }

        this.resourceService = new ResourceService(resource, new HTTPResourceAcquirer());
        ResourceService.setResourceChangedEvent(
                new Event(getString(R.string.event_resource_changed_blog), null, null));
        this.resourceService.bind(this.eventController);
        this.resourceService.setFrequency(0.1 / 6); //Check Every Minute
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

        //Connect with Main App Process
        if(intent != null && intent.hasExtra(AndroidServiceAdaptor.INTENT_EXTRA_REMOTE_MESSENGER) == true)
        {
            Messenger remoteMessenger = intent.
                    getParcelableExtra(AndroidServiceAdaptor.INTENT_EXTRA_REMOTE_MESSENGER);

            this.androidEventAdaptor.connect(remoteMessenger);
        }

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

        try
        {
            FileOutputStream stateFileOut =
                    this.openFileOutput(AndroidServiceAdaptor.FILENAME_STATE, 0);
            stateFileOut.write(resourceEventInterpreter.getState().getBytes());

        }catch(Exception exp){}

        this.resourceService.stop();
        this.androidEventAdaptor.disconnect();

    }

    private void setupResourceService()
    {
        String resourceURL = getString(R.string.blog_rss_url);
        Resource resource = new Resource(resourceURL, new Date(), "");
        Resource previousResource = this.resourceEventInterpreter.getResourceState(resource);
        if(previousResource != null) resource = previousResource;

        this.resourceService = new ResourceService(resource, new HTTPResourceAcquirer());
        this.resourceService.setFrequency(0.1 / 6); //Check Every Minute
        this.resourceService.bind(eventController);
    }


    private void setupResourceEventInterpreter()
    {
        //Read State From File
        String interpreterState = "";
        try
        {
            FileInputStream stateFileIn = this.openFileInput(FILENAME_STATE);
            byte[] stateBytes = new byte[stateFileIn.available()];
            stateFileIn.read(stateBytes);
            interpreterState = new String(stateBytes);
        }
        catch(Exception exp){}

        this.resourceEventInterpreter =
                new ResourceEventInterpreter(this.eventController, interpreterState);

        //Write State to File on State Change
        final AndroidServiceAdaptor serviceAdaptor = this;
        this.eventController.listen(this.toString(),
                ResourceEventInterpreter.getStateChangeEvent().getIdentifier(),
                new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        try
                        {
                            FileOutputStream stateFileOut =
                                    serviceAdaptor.openFileOutput(FILENAME_STATE, 0);
                            stateFileOut.write(event.getData().getBytes());

                        }catch(Exception exp){}
                    }
                });
    }

}
