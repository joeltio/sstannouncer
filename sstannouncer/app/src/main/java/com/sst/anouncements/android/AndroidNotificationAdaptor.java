package com.sst.anouncements.android;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;

import com.sst.anouncements.EntryActivity;

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
    private boolean notificationAutoCancel;
    private int notificationIconID;

    /**
     * Android Notification Adaptor Constructor
     * Creates a new Android Notification Adaptor for the given Android <code>context</code>.
     * The context would be used to send the notification to the user.
     * The  <code>notificationIcon</code> is the resource defines the small notification icon to
     * show the user when displaying the  notification.
     *
     * @param context The context to use to send notifications
     * @param notificationIcon The resource ID of the icon to use in the notification.
     */
    public AndroidNotificationAdaptor(Context context, int notificationIcon)
    {
        this.context = context;
        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.notificationIconID = notificationIcon;
        this.notificationAutoCancel = true;
    }


    /**
     * Create a new Notification.
     * Create a new Notification for the passed title and content.
     * The small icon that would be used for the notification is defined by the resource ID passed
     * to the Constructor, or set using <code>setNotificationIcon()</code>
     * Whether the notification would be dismissed automatically when the user interacts with it is
     * defined by <code>setNotificationAutoCancel()</code>, or by default true.
     *
     * @param title
     * @param content
     *
     * @see AndroidNotificationAdaptor#setNotificationAutoCancel(boolean)
     * @see AndroidNotificationAdaptor#setNotificationIconID(int)
     */
    public void create(String title, String content, String extraIdentifier, Parcelable extra)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(this.notificationIconID);
        builder.setAutoCancel(this.notificationAutoCancel);

        Intent notificationIntent = new Intent(this.context, EntryActivity.class);
        notificationIntent.putExtra(extraIdentifier, extra);
        PendingIntent contentIntent = PendingIntent.getActivity(this.context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);

        this.notification =  builder.build();
    }

    /**
     * Display the Notification.
     * Display the Notification created by <code>create()</code> to the user.
     * Fails if notification was never created by <code>create()</code>.
     *
     * @param id An ID to identify the notification.
     * @return Returns false if displaying the notification failed, else returns true.
     *
     * @see AndroidNotificationAdaptor#create(String, String, String, Parcelable)
     */
    public boolean display(int id)
    {
        if(this.notification != null)
        {
            this.notificationManager.notify(id, this.notification);
            return true;
        }
        return false;
    }

    public void setNotificationAutoCancel(boolean notificationAutoCancel) {
        this.notificationAutoCancel = notificationAutoCancel;
    }

    public void setNotificationIconID(int notificationIconID) {
        this.notificationIconID = notificationIconID;
    }
}
