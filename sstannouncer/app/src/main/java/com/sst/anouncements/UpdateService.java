package com.sst.anouncements;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Process;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.sst.anouncements.Feed.Feed;

import java.text.ParseException;
import java.util.Date;

//Update Service
//Polls for updates to resource.
public class UpdateService extends JobService implements Runnable
{
    public static String TAG = "UpdateService";
    public static String STORAGE_NAME = "update_service";
    public static int jobID = 1;

    public static String ACTION_UPDATE = "com.sst.anouncements.action.update_feed";
    public static String EXTRA_FREQUENCY_DELAY = "UpdateService.frequency";

    //Service Parameters
    private String resourceURL;
    private JobParameters parameters;

    //Service Worker
    private Thread worker;

    //Utility Objects
    private HTTPFetchMethod fetchMethod;
    private FeedUpdateNotification updateNotifier;

    //Service Lifecycle
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Service Callbacks
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.parameters = jobParameters;
        this.resourceURL = this.getString(R.string.blog_rss_url);

        this.worker = new Thread(this);
        this.fetchMethod = new HTTPFetchMethod();
        this.updateNotifier = FeedUpdateNotification.stateInstance(this);


        //Start Thread
        if(!this.worker.isAlive()) this.worker.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        this.worker.interrupt();

        return false;
    }

    //Service Runnable
    @Override
    public void run() {
        //Thread Setup
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        //Thread Work
        try {
            Date lastModified = this.fetchMethod.getModified(this.resourceURL);
            if(this.updateNotifier.isUpdate(lastModified))
            {
                String resource = this.fetchMethod.getResource(this.resourceURL);
                Feed feed = Feed.parse(resource);
                if(feed == null) throw new ParseException("", 0);

                this.updateNotifier.update(feed); //Send Notification to user
                this.sendBroadcast(new Intent(UpdateService.ACTION_UPDATE));
            }
        }catch(HTTPFetchMethod.FetchException e) {
            Log.e(UpdateService.TAG, "Resource Fetch via HTTP Failed:" + e.what(), e);
        }catch (ParseException e) {
            Log.e(UpdateService.TAG, "Parsing of Resource Failed");
        }

        this.jobFinished(this.parameters, Thread.currentThread().isInterrupted());
    }

    //Schedules Job for frequency delay, if frequency delay is zero, would read delay from storage
    //If frequency delay is -1, would never run service.
    public static void schedule(Context context, int frequencyDelay)
    {
        if(frequencyDelay == -1) return;

        //Determine Delay
        int delay = 0;
        if(frequencyDelay == 0) {
            SharedPreferences preferences =
                    context.getSharedPreferences(UpdateService.STORAGE_NAME, 0);
            delay = preferences.getInt(UpdateService.EXTRA_FREQUENCY_DELAY, 60); //Default : 1min
        }
        if(frequencyDelay > 0)
        {
            delay = frequencyDelay;
            SharedPreferences preferences =
                    context.getSharedPreferences(UpdateService.STORAGE_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(UpdateService.EXTRA_FREQUENCY_DELAY, frequencyDelay);
            editor.apply();
        }

        //Schedule Job
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                .setService(UpdateService.class)
                .setTag(UpdateService.TAG)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(delay, (int)Math.round(delay * 1.5)))
                .setConstraints(Constraint.ON_ANY_NETWORK | Constraint.DEVICE_IDLE)
                .build();
        dispatcher.mustSchedule(myJob);
    }
}
