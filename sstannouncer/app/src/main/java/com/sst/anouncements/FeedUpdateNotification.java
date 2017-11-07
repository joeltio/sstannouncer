package com.sst.anouncements;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.sst.anouncements.Feed.Entry;
import com.sst.anouncements.Feed.Feed;

import java.sql.Date;
import java.util.ArrayList;

//Feed Update Notification
//Determine changed posts in the Feed
//Sends a notification to the user for each user.
//NOTE: Object is a state dependent and henceforth NOT thread safe.
public class FeedUpdateNotification {
    private static String TAG = "FeedUpdateNotification";
    private static String storageName = "com_sst.announcer_notification_state";
    private static String storageFeedKey= "Feed";

    private Feed previousFeed;
    private Context context;
    private NotificationCompat.Builder builder;

    private FeedUpdateNotification(Feed previousFeed, Context context)
    {
        this.previousFeed = previousFeed;
        this.context = context;

        //Setup Notification Channel (Android 26 and Above
        String channelID = "com.sst.anouncements";
        if(Build.VERSION.SDK_INT >= 26)
        {
            NotificationChannel channel = new NotificationChannel(channelID, "Announcer",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
        }

        //Setup Default Configuration for Notifications
        this.builder = new NotificationCompat.Builder(this.context, channelID);
        this.builder.setAutoCancel(true);
        this.builder.setBadgeIconType(R.drawable.notifcation_icon);

    }

    //Retrieve instance with state information.
    public static FeedUpdateNotification stateInstance(Context context)
    {
        Feed feedState = FeedUpdateNotification.readFeedState(context);
        if(feedState == null) //Object State not found: Assume Feed Never Retrieved.
            feedState = new Feed(new Date(0), new ArrayList<String>(), new ArrayList<Entry>());

        return new FeedUpdateNotification(feedState, context);
    }

    //Determines whether the new feed is newever
    public boolean isUpdate(Feed newFeed)
    {
        if(newFeed.compareTo(this.previousFeed) == 1) return true;
        else return false;
    }

    //Notifies users about changes to new feed compared to the stored internal previous feed object
    //Would not do anything if the feed has not changed respect to the previous feed
    public void update(Feed newFeed)
    {
        if(this.isUpdate(newFeed))
        {
            ArrayList<Entry> diff = newFeed.diffEntry(this.previousFeed);
            for(Entry entry : diff)
            {
                this.notify(entry);
            }

            this.previousFeed = newFeed;
            this.writeFeedState(newFeed);
        }
    }

    //Send Notification to User
    private void notify(Entry entry)
    {
        //Setup Notification Intent
        Intent intent = new Intent(this.context, EntryActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(EntryActivity.ENTRY_EXTRA, entry);
        intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context,0, intent,
                0);

        //Setup
        this.builder.setContentIntent(pendingIntent);
        this.builder.setContentTitle(this.context.getString(R.string.notification_title_feed_update));
        this.builder.setContentText(entry.getTitle());
        this.builder.setSubText(entry.getContent());

        //Send Notification to User
        NotificationManager manager =
                (NotificationManager)this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = this.builder.build();
        String tag =
                (entry.getTitle() == null) ? entry.getLastUpdated().toString() : entry.getTitle();
        if(notification != null){
            manager.notify(tag, 0, notification);
        }else{
            Log.e(FeedUpdateNotification.TAG,
                    "Failed to deploy notification.: Notification Builder returned null");
        }
    }

    //Attempts to Write Feed State information
    private void writeFeedState(Feed feedState)
    {
        if(feedState != null) {
            SharedPreferences preferences =
                    this.context.getSharedPreferences(FeedUpdateNotification.storageName, 0);
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            editor.putString(FeedUpdateNotification.storageFeedKey, gson.toJson(feedState));
            editor.apply();
        }
    }

    //Read Feed State information
    private static Feed readFeedState(Context context)
    {
        SharedPreferences preferences =
                context.getSharedPreferences(FeedUpdateNotification.storageName, 0 );
        String feedJson = preferences.getString(FeedUpdateNotification.storageFeedKey, "");
        Gson gson = new Gson();
        return  gson.fromJson(feedJson, Feed.class);
    }
}
