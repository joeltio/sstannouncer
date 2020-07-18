package sst.com.anouncements.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import sst.com.anouncements.R
import sst.com.anouncements.feed.model.Entry

const val POST_CHANNEL_ID = "FEED_NEW_POST"

class DefaultNotificationService(
    private val appContext: Context
) : NotificationService {
    override fun setup() {
        // Create Notification Channels
        // Notification channels are only supported from Android 8.0 onwards
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                POST_CHANNEL_ID,
                appContext.getString(R.string.post_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            with(channel) {
                description = appContext.getString(R.string.post_channel_description)
                enableVibration(true)
            }

            // Register the channel with the system
            val manager = appContext.getSystemService(
                Application.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun pushNewEntry(entry: Entry) {
        val builder = NotificationCompat.Builder(appContext, POST_CHANNEL_ID)
            .setAutoCancel(true)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(entry.title)
            .setContentText(entry.contentWithoutHTML)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(POST_CHANNEL_ID)
        }

        with(NotificationManagerCompat.from(appContext)) {
            // The notification does not need to be unique for each version of an entry
            // Hence, using the entry id as the tag is sufficient
            notify(entry.id, 0, builder.build())
        }
    }
}