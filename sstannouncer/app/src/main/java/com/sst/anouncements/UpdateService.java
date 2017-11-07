package com.sst.anouncements;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sst.anouncements.Feed.Feed;

import java.text.ParseException;

//Update Service
//Polls for updates to resource.
public class UpdateService extends Service implements Runnable
{
    public static String TAG = "UpdateService";
    public static String STORAGE_NAME = "update_service";

    public static String ACTION_UPDATE = "com.sst.anouncements.action.update_feed";
    public static String EXTRA_FREQUENCY_DELAY = "UpdateService.frequency";

    //Service Parameters
    private String resourceURL;

    //Service Worker
    private Thread worker;
    private boolean runFlag;
    private int frequencyDelay; //Delay in Seconds

    //Utility Objects
    private HTTPFetchMethod fetchMethod;
    private FeedUpdateNotification updateNotifier;

    //Service Lifecycle
    @Override
    public void onCreate() {
        super.onCreate();

        this.resourceURL = this.getString(R.string.blog_rss_url);

        this.worker = new Thread(this);
        this.runFlag = false;
        this.frequencyDelay = 60; //Default Delay: 1 minute
        this.fetchMethod = new HTTPFetchMethod();
        this.updateNotifier = FeedUpdateNotification.stateInstance(this);

        this.readState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.writeState();
    }

    //Service Callbacks
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Parse Command
        this.frequencyDelay = intent.getIntExtra(UpdateService.EXTRA_FREQUENCY_DELAY,
                this.frequencyDelay);

        //Start Thread
        if(!this.worker.isAlive()) this.worker.start();

        return START_REDELIVER_INTENT;
    }


    //Service Binding not supported
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Service Runnable
    @Override
    public void run() {
        //Thread Setup
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        //Thread Loop
        while(this.runFlag) {
            try {
                String resource = this.fetchMethod.getResource(this.resourceURL);
                Feed feed = Feed.parse(resource);
                if(feed == null) throw new ParseException("", 0);

                if(this.updateNotifier.isUpdate(feed))
                {
                    this.updateNotifier.update(feed); //Send Notification to user
                    this.sendBroadcast(new Intent(UpdateService.ACTION_UPDATE));
                }
            }catch(HTTPFetchMethod.FetchException e) {
                Log.e(UpdateService.TAG, "Resource Fetch via HTTP Failed:" + e.what(), e);
            }catch (ParseException e) {
                Log.e(UpdateService.TAG, "Parsing of Resource Failed");
            }

            //Frequency Delay
            try {
                Thread.sleep((long) frequencyDelay * 1000);
            } catch (InterruptedException e) {
                this.runFlag = false;
                Log.e(UpdateService.TAG, "Worker Threaded Interrupted ", e);
            }
        }
    }

    //Persistence Methods
    private void writeState()
    {
        SharedPreferences preferences =
                this.getSharedPreferences(UpdateService.STORAGE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(UpdateService.EXTRA_FREQUENCY_DELAY, this.frequencyDelay);
        editor.apply();
    }

    private void readState()
    {
        SharedPreferences preferences =
                this.getSharedPreferences(UpdateService.STORAGE_NAME, 0 );
        this.frequencyDelay = preferences.getInt(UpdateService.EXTRA_FREQUENCY_DELAY, 60);
    }
}
