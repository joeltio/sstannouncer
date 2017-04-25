package com.sst.anouncements.android;

//**** REMOVE ON NOTIFICATION REDESIGN

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.sst.anouncements.EntryActivity;
import com.sst.anouncements.Feed.Entry;
import com.sst.anouncements.R;

/**
 * Android notification Adaptor
 * Facade to the Android Notification System.
 * Facilitates the creation and displaying of notifications on the Android OS.
 */

public class AndroidNotificationAdaptor
{
    private Context context;
    private NotificationManager notificationManager;
    private Notification notification;
    private int notifcationID;

    /**
     * Android Notification Adaptor Adaptor
     *
     * Creates a new Android Notification Adaptor for the given context.
     *
     * @param context The context to use.
     */
    public AndroidNotificationAdaptor(Context context)
    {
        this.context = context;
        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Build a new Notification.
     * Build a new Notification for the given title, content and target.
     * Target defines the object to start when the user interacts with the notification.
     * Overwrites the previous build and all its data.
     *
     * @param ID An user specified unique identifier that identifies the notification
     * @param title The title of the notification.
     * @param content The content of the notification.
     */
    public void build(int ID, String title, String content, Entry entry)
    {
        this.notifcationID = ID;

        NotificationCompat.Builder builder = null;
        builder = new NotificationCompat.Builder(this.context);
        builder.setContentTitle(title);
        builder.setContentText(content);

        Intent targetIntent = new Intent(this.context, EntryActivity.class);
        targetIntent.putExtra(EntryActivity.ENTRY_EXTRA, entry);
        PendingIntent pendingTargetIntent =
                PendingIntent.getActivity(this.context,
                        this.notifcationID,
                        targetIntent,
                        0);
        builder.setContentIntent(pendingTargetIntent);
        builder.setSmallIcon(R.drawable.notifcation_icon);
        builder.setAutoCancel(true);

        this.notification = builder.build();
    }

    /**
     * Display the Notification
     * Display the Notification that has been built by <code>build()</code> method.
     * If notification has never been built, no notification would be displayed.
     *
     */
    public void display()
    {
        this.notificationManager.notify(this.notifcationID, this.notification);
    }
}
