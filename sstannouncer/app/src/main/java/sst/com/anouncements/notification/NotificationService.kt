package sst.com.anouncements.notification

import sst.com.anouncements.feed.model.Entry

interface NotificationService {
    fun setup()

    fun pushNewEntry(entry: Entry)
}